package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.ReceiptDialogFragment;
import com.example.fburecipeapp.models.Receipt;
import com.parse.ParseFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Receipt> mReceipts = new ArrayList<>();
    private Context mContext;
    private Fragment receiptFragment;

    @NonNull
    @Override
    public ReceiptAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptAdapter.ViewHolder holder, int position) {

        Receipt receipt = mReceipts.get(position);
        ParseFile receiptImg = receipt.getImage();
        Date date = receipt.getCreatedAt();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);


        holder.dateUploaded.setText(strDate);

        Glide.with(mContext)
                .load(receiptImg.getUrl())
                .into(holder.receiptImg);

    }

    @Override
    public int getItemCount() {
        return mReceipts.size();
    }

    public ReceiptAdapter(ArrayList<String> mImages, ArrayList<Receipt> mReceipts, Context mContext, Fragment receiptFragment) {
        this.mImages = mImages;
        this.mReceipts = mReceipts;
        this.mContext = mContext;
        this.receiptFragment = receiptFragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView receiptImg;
        TextView dateUploaded;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            receiptImg = itemView.findViewById(R.id.receiptImg);
            cardView = itemView.findViewById(R.id.card_view_receipt);
            dateUploaded = itemView.findViewById(R.id.dateUploaded);

            receiptImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    Receipt receipt = mReceipts.get(position);
                    String objectId = receipt.getObjectId();

                    Log.d("clicked", "card view clicked");
                    showReceiptDialog(objectId);
                }
            });
        }
    }

    // calls dialog fragment for receipt details
    private void showReceiptDialog(String objectId) {
        FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
        if (fm != null) {
            ReceiptDialogFragment frag = ReceiptDialogFragment.newInstance(objectId);
            frag.setTargetFragment(receiptFragment, 0);
            frag.show(fm, "receipt_dialog_fragment");
        }
    }
}
