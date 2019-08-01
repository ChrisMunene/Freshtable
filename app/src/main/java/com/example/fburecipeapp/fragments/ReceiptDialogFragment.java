package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Receipt;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class ReceiptDialogFragment extends DialogFragment {

    private ImageView receiptImage;
    private TextView titleReceipt;
    private TextView descriptionReceipt;
    private String objectId;

    public ReceiptDialogFragment(){
    }

    public static ReceiptDialogFragment newInstance(String objectId) {
        ReceiptDialogFragment fragment = new ReceiptDialogFragment();
        Bundle args = new Bundle();
        args.putString("objectId", objectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.receipt_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        receiptImage = view.findViewById(R.id.receiptScan);
        titleReceipt = view.findViewById(R.id.titleReceipt);
        descriptionReceipt = view.findViewById(R.id.receiptDescription);

        objectId = getArguments().getString("objectId");

        getReceiptDetails(objectId);

        List<Receipt> receipts = getReceiptDetails(objectId);
        if(receipts != null){
            Receipt receipt = receipts.get(0);
            ParseFile image = receipt.getImage();
            String description = receipt.getDescription();
            String title = receipt.getTitle();

            descriptionReceipt.setText(description);
            titleReceipt.setText(title);


            if (image != null) {
                Glide.with(getContext()).load(image.getUrl()).into(receiptImage); // setting ParseFile image to our layout
            }
        }

        receiptImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private List<Receipt> getReceiptDetails(String id) {
        Receipt.Query query = new Receipt.Query();
        query.whereEqualTo("objectId", id);
        try {
            List<Receipt> receipts = query.find();
            return receipts;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
