package com.example.fburecipeapp.fragments;

import android.os.Bundle;
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
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class DetailsFragment extends Fragment {

    private String recipeID;
    private String name;
    private ParseFile image;
    private String allIngredients;
    private String instructions;
    private List<Ingredient> containsIngredients;
    private String containsIngredientDetails;
    private TextView tvRecipeName;
    private ImageView ivRecipeImage;
    private TextView tvRecipeIngredients;
    private TextView tvRecipeInstructions;
    private TextView tvRecipeContains;

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

        tvRecipeName = view.findViewById(R.id.tv_recipeTitle);
        ivRecipeImage = view.findViewById(R.id.iv_recipeImage);
        tvRecipeIngredients = view.findViewById(R.id.tv_recipeAllIngredients);
        tvRecipeInstructions = view.findViewById(R.id.tv_recipeInstructions);
        tvRecipeContains = view.findViewById(R.id.containsIngredientsDetails);

        getRecipeDetails();
    }

    private void getRecipeDetails() {
        Recipe.Query query = new Recipe.Query();
        query.withRecipeID(recipeID).containsIngredients();
        query.findInBackground(new FindCallback<Recipe>() {
            @Override
            public void done(List<Recipe> recipes, ParseException e) {
                if (e == null) {
                    Recipe recipe = recipes.get(0);
                    name = recipe.getName();
                    image = recipe.getImage();
                    allIngredients = recipe.getAllIngredients();
                    instructions = recipe.getInstructions();
                    containsIngredients = recipe.getContainsIngredients();
                    containsIngredientDetails = "";

                    for (int i = 0; i < containsIngredients.size() - 1; i++) {
                        Ingredient ingredient = containsIngredients.get(i);
                        containsIngredientDetails += (ingredient.getName() + ", ");
                    }
                    containsIngredientDetails += containsIngredients.get(containsIngredients.size() - 1).getName();

                    tvRecipeName.setText(name);
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(15)))
                            .into(ivRecipeImage);
                    tvRecipeIngredients.setText(allIngredients);
                    tvRecipeInstructions.setText(instructions);
                    tvRecipeContains.setText(containsIngredientDetails);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}