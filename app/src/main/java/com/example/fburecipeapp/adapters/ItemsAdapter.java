package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public Context context;
    public List<Ingredient> mItems;
    private List<Ingredient> selectedList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        selectedList = new ArrayList<Ingredient>();

        View postView = inflater.inflate(R.layout.single_food_option, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = mItems.get(position);
        holder.bind(ingredient); // setting item text to our layout

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
        public void bind(final Ingredient ingredient){
            tvFoodItem.setText(ingredient.getName());

            // when the check box is clicked, want to change the boolean to the opposite of what it was
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            selectedList.add(ingredient);
                        }
                        else {
                            selectedList.remove(ingredient);
                        }
                }
            });
        }

    }

    public ItemsAdapter (List<Ingredient> items) {
        mItems = items;
    }

    public List<Ingredient> getSelectedItems() {
        return selectedList;
    }

}