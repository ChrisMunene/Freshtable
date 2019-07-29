package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;
import com.parse.ParseUser;

import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.ViewHolder> {

    private Context context;
    private List<Ingredient> savedIngredients;

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
        Ingredient ingredient = savedIngredients.get(position);
        holder.bind(ingredient);

    }

    @Override
    public int getItemCount() {
        return savedIngredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView savedItem;

        public ViewHolder(View itemView) {
            super(itemView);


            savedItem = itemView.findViewById(R.id.tvSavedItem);
        }

        public void bind(final Ingredient ingredient){
            savedItem.setText(ingredient.getName());

        }

    }

    public KitchenAdapter (List<Ingredient> ingredients) {
        savedIngredients = ingredients;
    }
}
