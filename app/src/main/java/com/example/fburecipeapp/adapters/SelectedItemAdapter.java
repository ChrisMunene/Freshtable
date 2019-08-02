package com.example.fburecipeapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;

import java.util.List;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.ViewHolder>{

    private Context context;
    private List<Ingredient> selectedFoodItems;
    public onItemsChangedListener mListener;

    public SelectedItemAdapter(Context context, List<Ingredient> selectedIngredients, onItemsChangedListener listener) {
        this.context = context;
        this.selectedFoodItems = selectedIngredients;
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = selectedFoodItems.get(position);
        holder.bind(ingredient, position);
    }

    @Override
    public int getItemCount() {
        return selectedFoodItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private ImageView ivSavedItemImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSelectedItemName);
            ivSavedItemImg = itemView.findViewById(R.id.ivSelectedItemImg);
        }

        // Binds data to the view
        public void bind(Ingredient ingredient, final int position){
           tvName.setText(ingredient.getName());
            RequestOptions options = new RequestOptions().fitCenter().circleCrop();
            Glide.with(context).load(ingredient.getImage().getUrl()).apply(options).into(ivSavedItemImg);
            ivSavedItemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedFoodItems.remove(ingredient);
                    notifyItemRemoved(position);
                    mListener.onItemsChanged();
                }
            });
        }
    }

    public interface onItemsChangedListener {
        void onItemsChanged();
    }



}
