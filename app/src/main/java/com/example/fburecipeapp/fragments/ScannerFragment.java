package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fburecipeapp.models.Receipt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

public class ScannerFragment extends Fragment {
    private EditText descriptionInput;
    private Button createBtn;
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private ImageView ivPreview;
    private AsyncHttpClient client;
    private final static String OCR_URL = "https://api.ocr.space/parse/image";
    public final String TAG = "ScannerFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    File photoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        createBtn = view.findViewById(R.id.createBtn);
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
                final String description = descriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                final ParseFile file = new ParseFile(photoFile);
                createPost(description, file, user);
            }
        });

        //Initialize http client
        client =  new AsyncHttpClient();



        // Initialize Progress Dialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

        getPost();

    }

    // Creates a new post in Parse
    private void createPost(String description, ParseFile image, ParseUser user){
        pd.show();
        final Receipt newReceipt = new Receipt();
        newReceipt.setDescription(description);
        newReceipt.setImage(image);
        newReceipt.setUser(user);

        newReceipt.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d(TAG, "Post Created Successfully");
                    Toast.makeText(getContext(), "Posted Successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate to timeline
//                    Fragment frag = new HomeFragment();
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.flContainer, frag);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                    ft.addToBackStack(null);
//                    ft.commit();
                } else {
                    e.printStackTrace();
                }

                //Reset input form
                descriptionInput.setText("");
                ivPreview.setImageResource(0);

                // Dismiss progress dialog
                pd.dismiss();
            }
        });
    }

    private void getPost(){
        pd.show();
        RequestParams params = new RequestParams();
        params.put("url", "https://pbs.twimg.com/media/DM3DpDPW0AUIDbv.jpg");
        params.put("isCreateSearchablePdf", false);
        params.put("isSearchablePdfHideTextLayer", false);
        params.put("filetype", "jpg");
        params.put("isTable", true);
        client.addHeader("apikey", "25e4ce8d0788957");
        client.post(OCR_URL,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    JSONArray results = response.getJSONArray("ParsedResults");
                    String parsedText = results.getJSONObject(0).getString("ParsedText");
                    String[] lines = parsedText.split("\n");
                    Log.d(TAG, parsedText);
                    Log.d(TAG, "--------------------------------");
                    for (String line: lines) {
                        Pattern p = Pattern.compile("(\\d*[a-zA-Z]?+)\\s+([a-zA-Z\\s]*[a-zA-Z0-9]+)\\s+([$]*\\d*\\.+\\d*)+");
                        Matcher m = p.matcher(line);
                        if(m.find()){
                            Log.d("Match", line);
                        }

                    }
                    pd.dismiss();
                } catch (JSONException e) {
                    String message = e.getMessage();
                    Log.e(TAG, message);
                    pd.dismiss();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                pd.dismiss();
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

    // Returns a unique file name using current timestamp.
    public String generateUniqueFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        return imageFileName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                // Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Rotate the image to the correct orientation
                Bitmap rotatedImage = rotateBitmapOrientation(photoFile.getAbsolutePath());

                // RESIZE BITMAP, see section below
                // Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rotatedImage, SOME_WIDTH);

                // Load the taken image into a preview
                ivPreview.setImageBitmap(rotatedImage);

            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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

}
