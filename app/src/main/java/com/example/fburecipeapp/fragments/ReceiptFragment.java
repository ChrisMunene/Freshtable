package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.ReceiptAdapter;
import com.example.fburecipeapp.models.Receipt;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ReceiptFragment extends Fragment {

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Receipt> mReceipts = new ArrayList<>();
    private CardView cardView;
    private ReceiptAdapter receiptAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Kitchen fragment", "OnCreateView success");
        return inflater.inflate(R.layout.fragment_receipt, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.receipt_single_item, null);

        RecyclerView recyclerView = view.findViewById(R.id.rvReceipts);
        cardView = v.findViewById(R.id.card_view_receipt);
        receiptAdapter = new ReceiptAdapter(mImages, mReceipts, getContext(), this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(receiptAdapter);

        loadReceipts();

    }

    private void loadReceipts() {
        Receipt.Query query = new Receipt.Query();
        query.findInBackground(new FindCallback<Receipt>() {
            public void done(List<Receipt> receipt, ParseException e) {
                if (e == null) {
                    Log.d("item count", String.format("%s" , receipt.size()));
                    mReceipts.addAll(receipt);
                    receiptAdapter.notifyDataSetChanged(); // update adapter
                }
                else {
                    e.printStackTrace();
                }
            }
        });

    }
}
