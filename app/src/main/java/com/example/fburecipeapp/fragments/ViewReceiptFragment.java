package com.example.fburecipeapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fburecipeapp.R;

import org.parceler.Parcels;

import java.io.IOException;

public class ViewReceiptFragment extends Fragment {

    private ImageView receiptImgView;

    public ViewReceiptFragment() {
        // Required empty public constructor
    }

    public static ViewReceiptFragment newInstance(Uri photoUri, String photoFilePath) {
        ViewReceiptFragment fragment = new ViewReceiptFragment();
        Bundle args = new Bundle();
        args.putParcelable("receiptImageUri", Parcels.wrap(photoUri));
        args.putString("photoFilePath", photoFilePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_receipt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receiptImgView = view.findViewById(R.id.ivReceiptImage);
        Uri photoUri = Parcels.unwrap(getArguments().getParcelable("receiptImageUri"));
        String photoFilePath = getArguments().getString("photoFilePath");
        Bitmap receiptImage = null;
        try {
            if(photoFilePath != null){
                receiptImage = rotateBitmapOrientation(photoFilePath);
            } else {
                receiptImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }

            receiptImgView.setImageBitmap(receiptImage);
        } catch (IOException e) {
            e.printStackTrace();
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
