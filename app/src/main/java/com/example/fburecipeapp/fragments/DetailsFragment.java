package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Recipe;
import com.example.fburecipeapp.models.Recipes;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.parceler.Parcel;
import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsFragment extends Fragment {

    Recipe recipe;
    private String recipeID;
    private String name;
    private ParseFile image;
    private String ingredients;
    private String instructions;


//    public DetailsFragment(String name, ParseFile image, String ingredients, String instructions) {
//        this.name = name;
//        this.image = image;
//        this.ingredients = ingredients;
//        this.instructions = instructions;
//    }

    public DetailsFragment(String recipeID) {
        this.recipeID = recipeID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvRecipeName = view.findViewById(R.id.tv_recipeTitle);
        ImageView ivRecipeImage = view.findViewById(R.id.iv_recipeImage);
        TextView tvRecipeIngredients = view.findViewById(R.id.tv_recipeAllIngredients);
        TextView tvRecipeInstructions = view.findViewById(R.id.tv_recipeInstructions);

        getRecipeDetails(tvRecipeName, ivRecipeImage, tvRecipeIngredients, tvRecipeInstructions);
    }

    private void getRecipeDetails(TextView tvRecipeName, ImageView ivRecipeImage, TextView tvRecipeIngredients, TextView tvRecipeInstructions) {
        Recipes.Query query = new Recipes.Query();
        query.withRecipeID(recipeID);
        query.findInBackground(new FindCallback<Recipes>() {
            @Override
            public void done(List<Recipes> recipes, ParseException e) {
                if (e == null) {
                    Recipes recipe = recipes.get(0);
                    name = recipe.getName();
                    image = recipe.getImage();
                    ingredients = recipe.getAllIngredients();
                    instructions = recipe.getInstructions();
                    
                    tvRecipeName.setText(name);
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(15)))
                            .into(ivRecipeImage);
                    tvRecipeIngredients.setText(ingredients);
                    tvRecipeInstructions.setText(instructions);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}