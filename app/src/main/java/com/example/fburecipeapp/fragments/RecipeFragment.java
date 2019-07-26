package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.StaggeredRecyclerViewAdapter;
import com.example.fburecipeapp.models.Recipe;
import com.example.fburecipeapp.models.Recipes;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private static final int NUM_COLUMNS = 2;
    private static final String EDAMAME_BASE_URL = "https://api.edamam.com/search";

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Recipes> mRecipes = new ArrayList<>();
    private StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter;
    private AsyncHttpClient client;

    private ArrayList<String> myIngredients;

    public RecipeFragment() {
        myIngredients = new ArrayList<String>();
        myIngredients.add("Honey");
        myIngredients.add("Sugar");
        myIngredients.add("Oil");
        myIngredients.add("Chicken");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Kitchen fragment", "OnCreateView success");
        return inflater.inflate(R.layout.fragment_recipe, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerView = view.findViewById(R.id.rvRecipes);
        staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(mRecipes, mImages, getContext(), getFragmentManager());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);

        // Initialize http client
        client = new AsyncHttpClient();

        loadRecipes();

    }

//    private void loadRecipes() {
//        Recipe.Query query = new Recipe.Query();
//        query.findInBackground(new FindCallback<Recipe>() {
//            public void done(List<Recipe> recipe, ParseException e) {
//                if (e == null) {
//                    Log.d("item count", String.format("%s" , recipe.size()));
//                    mRecipes.addAll(recipe);
//                    staggeredRecyclerViewAdapter.notifyDataSetChanged(); // update adapter
//                }
//                else {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


    private void loadRecipes() {
        Recipes.Query query = new Recipes.Query();
        query.withOneIngredient(myIngredients);
        query.findInBackground(new FindCallback<Recipes>() {
            @Override
            public void done(List<Recipes> objects, ParseException e) {
                if (e == null) {
                    Log.d("item count", String.format("%s", objects.size()));
                    mRecipes.addAll(objects);
                    staggeredRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
