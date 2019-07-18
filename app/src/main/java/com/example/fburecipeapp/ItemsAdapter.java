package com.example.fburecipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public Context context;
    public ArrayList<FoodType> mItems;
    public String objectId;
    public List<FoodType> mTypes;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.single_food_option, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodType foodType = mItems.get(position);
        holder.foodItem.setText("eggs");

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView foodItem;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            foodItem = itemView.findViewById(R.id.tvFoodItem);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

    }

    public ItemsAdapter (ArrayList<FoodType> items) {
        mItems = items;
    }


}