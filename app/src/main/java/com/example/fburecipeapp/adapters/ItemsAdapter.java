package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public Context context;
    public List<String> mItems;

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
        String foodItem = mItems.get(position);
        holder.bind(foodItem);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvFoodItem;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            tvFoodItem = itemView.findViewById(R.id.tvFoodItem);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
        public void bind(String foodItem){
            tvFoodItem.setText(foodItem);
        }

    }

    public ItemsAdapter (List<String> items) {
        mItems = items;
    }


}