package com.example.fburecipeapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
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

    public static ViewReceiptFragment newInstance(Uri photoUri) {
        ViewReceiptFragment fragment = new ViewReceiptFragment();
        Bundle args = new Bundle();
        args.putParcelable("receiptImageUri", Parcels.wrap(photoUri));
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
        Bitmap receiptImage = null;
        try {
            receiptImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            receiptImgView.setImageBitmap(receiptImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
