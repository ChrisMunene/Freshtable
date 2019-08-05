package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.StaggeredRecyclerViewAdapter;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.Recipes;
import com.example.fburecipeapp.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private static final int NUM_COLUMNS = 2;
    private static final String TAG = "RecipeFragment";

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Recipes> mRecipes = new ArrayList<>();
    private StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter;
    private AsyncHttpClient client;
    private ImageButton searchBtn;
    private Fragment recipeFragment;

    private ArrayList<Ingredient> myIngredients;

    public RecipeFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Kitchen fragment", "OnCreateView success");
        return inflater.inflate(R.layout.fragment_recipe, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        searchBtn = view.findViewById(R.id.searchBtn);
        RecyclerView recyclerView = view.findViewById(R.id.rvRecipes);
        staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(mRecipes, mImages, getContext(), getFragmentManager(), this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);

        // Initialize http client
        client = new AsyncHttpClient();
        myIngredients = new ArrayList<Ingredient>();

        loadUserIngredients();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm != null) {
                    FilterRecipeDialogFragment frag = FilterRecipeDialogFragment.newInstance();
                    frag.setTargetFragment(recipeFragment, 0);
                    frag.show(fm, "receipt_dialog_fragment");
                }
            }
        });


    }

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

    private void loadUserIngredients() {
    User.Query query =  new User.Query();
    query.forCurrentUser().withSavedIngredients();
    query.findInBackground(new FindCallback<User>() {
        @Override
        public void done(List<User> users, ParseException e) {
            if(e == null){
                for(User user: users){
                    myIngredients.addAll(user.getSavedIngredients());

                }

                loadRecipes();
            }
        }
    });

    }
}
