package com.example.fburecipeapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class KitchenAdapter extends RecyclerView.Adapter<KitchenAdapter.ViewHolder> {

    private Context context;
    private List<Ingredient> savedIngredients;
    private ImageView savedItemImage;
    private TextView savedItem;
    private List<Ingredient> removedItems;
    private User currentUser;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            savedItem = itemView.findViewById(R.id.tvSelectedItemName);
            savedItemImage = itemView.findViewById(R.id.ivSelectedItemImg);

            removedItems = new ArrayList<Ingredient>();
            currentUser = (User) ParseUser.getCurrentUser();

            savedItemImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    removedItems.add(savedIngredients.get(position));
                    savedIngredients.remove(position);
                    notifyItemRemoved(position);

                    currentUser.setSavedIngredients(savedIngredients);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Item removed.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("delete kitchen", "Error deleting item", e);
                            }
                        }
                    });
                    return true;
                }
            });

        }
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.kitchen_item, parent, false);
        KitchenAdapter.ViewHolder viewHolder = new KitchenAdapter.ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = savedIngredients.get(position);
        ParseFile image = ingredient.getImage();
        String name = ingredient.getName();

        savedItem.setText(name);
        if (image != null) {
            Glide.with(context).load(image.getUrl()).into(savedItemImage); // setting ParseFile image to our layout
        }

    }

    @Override
    public int getItemCount() {
        return savedIngredients.size();
    }

    public KitchenAdapter (List<Ingredient> ingredients) {
        savedIngredients = ingredients;
    }
}
