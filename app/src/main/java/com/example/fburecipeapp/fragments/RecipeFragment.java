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

public class RecipeFragment extends Fragment implements FilterRecipeDialogFragment.Listener {

    private static final int NUM_COLUMNS = 2;
    private static final String TAG = "RecipeFragment";

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Recipes> mRecipes = new ArrayList<>();
    private StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter;
    private AsyncHttpClient client;
    private ImageButton searchBtn;
    private ArrayList<String> selectedChipGroup;
    private Fragment recipeFragment;
    private List<Ingredient> savedIngredients;

    private ArrayList<Ingredient> myIngredients;


    public RecipeFragment() {
        selectedChipGroup = new ArrayList<>();
        savedIngredients = new ArrayList<>();
        myIngredients = new ArrayList<Ingredient>();
        client = new AsyncHttpClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        loadUserIngredients();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void loadRecipes() {
        Recipes.Query query = new Recipes.Query();
        query.withIngredients(myIngredients);
        query.findInBackground(new FindCallback<Recipes>() {
            @Override
            public void done(List<Recipes> objects, ParseException e) {
                if (e == null) {
                    Log.d("item count", String.format("%s", objects.size()));
                    mRecipes.removeAll(mRecipes);
                    mRecipes.addAll(objects);
                    staggeredRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadUserIngredients() {
        User.Query query = new User.Query();
        query.forCurrentUser().withSavedIngredients();
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (e == null) {
                    for (User user : users) {
                        savedIngredients = user.getSavedIngredients();
                        myIngredients.removeAll(myIngredients);

                        if (selectedChipGroup.size() != 0) {
                            for (int i = 0; i < savedIngredients.size(); i++) {
                                if (selectedChipGroup.contains(savedIngredients.get(i).getName())) {
                                    myIngredients.add(savedIngredients.get(i));
                                }
                            }
                        } else {
                                myIngredients.addAll(savedIngredients);
                            }
                        }
                    }

                    loadRecipes();
                }
        });
    }

    private void loadCurrentIngredients() {
        loadRecipes();
    }

    private void showEditDialog(){
        FragmentManager fm = getFragmentManager();
        if(fm != null){
            FilterRecipeDialogFragment frag = FilterRecipeDialogFragment.newInstance();
            frag.setTargetFragment(this, 0);
            frag.show(fm, "fragment_filter");
        }
    }

    @Override
    public void onFinishEditingList(ArrayList<String> chipGroup) {
        Log.d("filter transfer", "finished editing list");
        selectedChipGroup = chipGroup;
        loadUserIngredients();
    }
}
