package com.example.fburecipeapp.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.FoodType;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.ViewHolder>{

    private Context context;
    private List<String> foodTypes;
    private List<String> selectedFoodItems;

    public EditListAdapter(Context context, List<String> foodTypes, List<String> precheckedIngredients) {
        this.context = context;
        this.foodTypes= foodTypes;
        this.selectedFoodItems = precheckedIngredients;
        for(String item: precheckedIngredients){
            Log.d("Adapter", item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_editable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        String foodItem = foodTypes.get(position);
        holder.bind(foodItem, position);
    }

    @Override
    public int getItemCount() {
        return foodTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        private CheckBox cbFoodItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbFoodItem = itemView.findViewById(R.id.cbFoodItem);
        }

        // Binds data to the view
        public void bind(final String foodItem, final int position){
            // Set the fooditem
            cbFoodItem.setText(foodItem);

            // Reset the state of the checkbox - Because the recyclerview retains the state of recycled components.
            cbFoodItem.setChecked(selectedFoodItems.contains(foodItem));

            // Set on check listener
            cbFoodItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
                    if(checked){
                        // If selected item is not in list, add to list
                        if(!selectedFoodItems.contains(foodItem))selectedFoodItems.add(foodItem);
                    } else {
                        // If deselected item is in list, remove it
                        if(selectedFoodItems.contains(foodItem)) selectedFoodItems.remove(foodItem);
                    }
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        foodTypes.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<String> list) {
        foodTypes.addAll(list);
        notifyDataSetChanged();
    }

    public List<String> getSelectedFoodItems(){
        return selectedFoodItems;
    }

}