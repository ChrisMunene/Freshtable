package com.example.fburecipeapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;

import java.util.List;

public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.ViewHolder>{

    private Context context;
    private List<Ingredient> ingredients;
    private List<Ingredient> selectedFoodItems;

    public EditListAdapter(Context context, List<Ingredient> ingredients, List<Ingredient> precheckedIngredients) {
        this.context = context;
        this.ingredients = ingredients;
        this.selectedFoodItems = precheckedIngredients;
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
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        private CheckBox cbFoodItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbFoodItem = itemView.findViewById(R.id.cbFoodItem);
        }

        // Binds data to the view
        public void bind(Ingredient ingredient, final int position){
            // Set the fooditem
            cbFoodItem.setText(ingredient.getName());

            // Reset the state of the checkbox - Because the recyclerview retains the state of recycled components.
            cbFoodItem.setChecked(selectedFoodItems.contains(ingredient));

            // Set on check listener
            cbFoodItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
                    if(checked){
                        // If selected item is not in list, add to list
                        if(!selectedFoodItems.contains(ingredient))selectedFoodItems.add(ingredient);
                    } else {
                        // If deselected item is in list, remove it
                        if(selectedFoodItems.contains(ingredient)) selectedFoodItems.remove(ingredient);
                    }
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        ingredients.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Ingredient> list) {
        ingredients.addAll(list);
        notifyDataSetChanged();
    }

    public List<Ingredient> getSelectedFoodItems(){
        return selectedFoodItems;
    }

}