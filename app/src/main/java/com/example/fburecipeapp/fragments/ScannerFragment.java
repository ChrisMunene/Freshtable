package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.Receipt;
import com.example.fburecipeapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fburecipeapp.R;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import static android.app.Activity.RESULT_OK;

public class ScannerFragment extends Fragment implements IngredientListDialogFragment.Listener {
    private EditText descriptionInput;
    private Button createBtn;
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private ImageView ivPreview;
    private AsyncHttpClient client;

    private final static String OCR_URL = "https://api.ocr.space/parse/image";
    public final String TAG = ScannerFragment.class.getSimpleName();
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    private File photoFile;
    private Bitmap receiptImageBmp;
    private Uri photoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        createBtn = view.findViewById(R.id.uploadFileBtn);
        fab = view.findViewById(R.id.fab);
        ivPreview = view.findViewById(R.id.ivPreview);


        // Set Click listeners
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera(view);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        //Initialize http client
        client =  new AsyncHttpClient();

        // Initialize Progress Dialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

    }

    // Creates a new post in Parse
    private void postReceipt(String title, String description, ParseFile image, ParseUser user, List<Ingredient> receiptItems){
        Receipt newReceipt = new Receipt();
        newReceipt.setTitle(title);
        newReceipt.setDescription(description);
        newReceipt.setImage(image);
        newReceipt.setUser(user);
        newReceipt.setReceiptItems(receiptItems);

        newReceipt.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d(TAG, "Receipt Saved Successfully");
                } else {
                    Log.e(TAG, "Failed to post receipt", e);
                }

                //Reset image field
                ivPreview.setImageResource(0);

                //Update user
                updateUser(receiptItems);

            }
        });

    }

    private void updateUser(List<Ingredient> ingredients){
        User currentUser = (User) ParseUser.getCurrentUser();
        List<Ingredient> savedIngredients = currentUser.getSavedIngredients();
        savedIngredients.addAll(ingredients);
        currentUser.setSavedIngredients(savedIngredients);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d(TAG, "User updated successfully");
                    Toast.makeText(getContext(), "Receipt Saved Successfully!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "Failed to update user", e);
                }

                // Dismiss progress dialog
                pd.dismiss();

                // Redirect
                redirectToFragment(new KitchenFragment());
            }
        });
    }


    // Launch Camera
    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(generateUniqueFileName());

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    // Returns a unique file name using current timestamp.
    public String generateUniqueFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        return imageFileName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Set uri -- Used to send photo through intents
                    photoUri = Uri.fromFile(photoFile);

                     // Rotate the image to the correct orientation
                     receiptImageBmp = rotateBitmapOrientation(photoFile.getAbsolutePath());

                     // Create Image for MLKit
                     FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(receiptImageBmp);

                    processImage(firebaseVisionImage);

                    // Load the taken image into a preview
                    ivPreview.setImageBitmap(receiptImageBmp);

                } else { // Result was a failure
                    Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;

            case PICK_PHOTO_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    photoUri = data.getData();

                    // Do something with the photo based on Uri
                    try {
                        // Get bitmap from Uri
                        receiptImageBmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);

                        // Create Image for MLKit
                        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(receiptImageBmp);

                        processImage(firebaseVisionImage);

                        // Load the selected image into a preview
                        ivPreview.setImageBitmap(receiptImageBmp);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getContext(), "Problem picking image", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                return;
        }
    }

    private void processImage(FirebaseVisionImage image){
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.show();

        // Initialize OCR MLkit
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        // Perform processing
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                // Task completed successfully
                                // ...
                                List<String> receiptItems = processRecognizedText(result);
                                pd.dismiss();
                                String photoFilePath = photoFile != null ? photoFile.getAbsolutePath() : null;
                                showEditDialog(receiptItems, photoFilePath);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        pd.dismiss();
                                        Log.e(TAG, "Error processing text", e);
                                    }
                                });
    }

    // Callback for successful OCR operation
    private List<String> processRecognizedText(FirebaseVisionText result) {
        List<String> receiptItems = new ArrayList<String>();

        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();

                    // Get all words that are in all Caps(Most receipts use ALL CAPS keywords for receipt items).
                    if(elementText.matches("^([A-Z]{2,})*$")){
                        receiptItems.add(elementText);
                    }
                }
            }
        }

        return receiptItems;
    }

    // Rotates imagefile to device orientation
    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }


    private void showEditDialog(List<String> receiptItems, String photoFilePath){
        FragmentManager fm = getFragmentManager();
        if(fm != null){
            IngredientListDialogFragment frag = IngredientListDialogFragment.newInstance(receiptItems, photoUri, photoFilePath);
            frag.setTargetFragment(this, 0);
            frag.show(fm, "fragment_edit_items");
        }
    }

    @Override
    public void onFinishEditingList(String title, String description, List<Ingredient> foodItems) {
        // Show Progress dialog
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.show();

        // Get current user
        final ParseUser user = ParseUser.getCurrentUser();

        // Convert Bitmap to ByteArray -- To be used when creating a parsefile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        receiptImageBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        receiptImageBmp.recycle();

        // Covert ByteArray to ParseFile
        ParseFile receiptImage = new ParseFile(generateUniqueFileName(), byteArray);

        postReceipt(title, description, receiptImage, user, foodItems);
    }

    private void redirectToFragment(Fragment destination){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, destination);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }


}
