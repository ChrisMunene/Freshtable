package com.example.fburecipeapp.fragments;

import android.annotation.TargetApi;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

@TargetApi(26)
public class DetailsFragment extends Fragment {

    private String recipeID;

    private TextView tvRecipeName;
    private ImageView ivRecipeImage;
    private TextView tvRecipeIngredients;
    private TextView tvRecipeInstructions;
    private TextView tvRecipeContains;
    private YouTubePlayerView youTubePlayerView;

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
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);

        // Add player to lifecycle
        getLifecycle().addObserver(youTubePlayerView);

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
                    ParseFile image = recipe.getImage();
                    List<Ingredient> containsIngredients = recipe.getContainsIngredients();
                    List<String> ingredients = new ArrayList<String>();
                    for(Ingredient ingredient: containsIngredients){
                        ingredients.add(ingredient.getName());
                    }
                    String ingredientNameList = String.join(", ", ingredients);

                    // Set text
                    tvRecipeName.setText(recipe.getName());
                    tvRecipeIngredients.setText(recipe.getAllIngredients());
                    tvRecipeInstructions.setText(recipe.getInstructions());

                    // Load image
                    Glide.with(getContext())
                            .load(image.getUrl())
                            .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(15)))
                            .into(ivRecipeImage);
                    tvRecipeContains.setText(ingredientNameList);

                    // Initialize youtube player
                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                            youTubePlayer.loadVideo(recipe.getVideoId(), 0);
                            youTubePlayer.pause();
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}