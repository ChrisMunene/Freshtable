package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.parse.ParseUser;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.ViewHolder> {

    public Context context;
    public List<String> savedItems;
    public ParseUser parseUser;

    @NonNull
    @Override
    public KitchenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.kitchen_item, parent, false);
        KitchenAdapter.ViewHolder viewHolder = new KitchenAdapter.ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull KitchenAdapter.ViewHolder holder, int position) {
        String foodItem = savedItems.get(position);
        holder.bind(foodItem);

    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView savedItem;

        public ViewHolder(View itemView) {
            super(itemView);


            savedItem = itemView.findViewById(R.id.tvSavedItem);
        }
        public void bind(final String foodItem){
            savedItem.setText(foodItem);

        }

    }

    public KitchenAdapter (List<String> items) {
        savedItems = items;
    }
}
