package com.example.fburecipeapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.activities.EditReceiptActivity;
import com.example.fburecipeapp.models.Ingredient;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.ViewHolder>{

    private Context context;
    private List<Ingredient> selectedFoodItems;
    private final int VIEW_TYPE_BTN = 0;
    private final int VIEW_TYPE_CELL = 1;
    public AddButtonClickListener mListener;

    public EditListAdapter(Context context, List<Ingredient> precheckedIngredients) {
        this.context = context;
        this.selectedFoodItems = precheckedIngredients;
    }

    public void setOnAddBtnClickedListener(AddButtonClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_BTN){
            view = LayoutInflater.from(context).inflate(R.layout.item_editable_btn, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_editable_cell, parent, false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == selectedFoodItems.size() || selectedFoodItems.isEmpty()){
            holder.bindAddButton();
        } else {
            Ingredient ingredient = selectedFoodItems.get(position);
            holder.bind(ingredient, position);
        }
    }

    @Override
    public int getItemCount() {
        return selectedFoodItems.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        private ImageView ivSelectedItemImg;
        private TextView tvSelectedItemName;
        private ImageButton ivAddBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSelectedItemImg = itemView.findViewById(R.id.ivSelectedItemImg);
            tvSelectedItemName = itemView.findViewById(R.id.tvSelectedItemName);
            ivAddBtn = itemView.findViewById(R.id.ivAddBtn);
        }

        // Binds data to the view
        public void bind(Ingredient ingredient, final int position){
            // Set the fooditem
            tvSelectedItemName.setText(ingredient.getName());
            RequestOptions options = new RequestOptions().fitCenter().circleCrop();
            Glide.with(context).load(ingredient.getImage().getUrl()).apply(options).into(ivSelectedItemImg);
            ivSelectedItemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedFoodItems.remove(ingredient);
                }
            });
        }

        public void bindAddButton(){
            ivAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAddButtonClicked();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == selectedFoodItems.size()) ? VIEW_TYPE_BTN : VIEW_TYPE_CELL;
    }

    // Clean all elements of the recycler
    public void clear() {
        selectedFoodItems.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Ingredient> list) {
        selectedFoodItems.addAll(list);
        notifyDataSetChanged();
    }

    public List<Ingredient> getSelectedFoodItems(){
        return selectedFoodItems;
    }

    public interface AddButtonClickListener{
        void onAddButtonClicked();
    }

}