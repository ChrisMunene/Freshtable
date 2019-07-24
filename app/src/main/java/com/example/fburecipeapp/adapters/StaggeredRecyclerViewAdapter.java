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
import com.example.fburecipeapp.models.Recipe;
import com.parse.ParseFile;

import java.util.ArrayList;

public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder>  {

    private static final String TAG = "StaggeredRecyclerViewAdapter";

    private ArrayList<Recipe> mRecipes = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

    public StaggeredRecyclerViewAdapter(ArrayList<Recipe> mRecipes, ArrayList<String> mImages, Context mContext) {
        this.mRecipes = mRecipes;
        this.mImages = mImages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Recipe recipe = mRecipes.get(position);
        ParseFile image = recipe.getImage();


        Glide.with(mContext)
                .load(image.getUrl())
                .apply(requestOptions)
                .into(holder.recipeImg);
        holder.recipeName.setText(recipe.getRecipe());


    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         ImageView recipeImg;
         TextView recipeName;

         public ViewHolder(View itemView) {
             super(itemView);
             this.recipeImg = itemView.findViewById(R.id.recipeImg);
             this.recipeName = itemView.findViewById(R.id.tvRecipe);
         }
    }
}
