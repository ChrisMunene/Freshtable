package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;

import com.example.fburecipeapp.activities.ExpandableActivity;

import com.example.fburecipeapp.activities.LoginActivity;
import com.example.fburecipeapp.adapters.KitchenAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class KitchenFragment extends Fragment implements KitchenAdapter.onItemsChangedListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    protected ArrayList<FoodType> types;
    protected KitchenAdapter kitchenAdapter;
    private ImageButton logoutBtn;
    private ProgressDialog pd;
    private User currentUser;
    private ImageButton addFoodBtn;
    private List<Ingredient> savedIngredients;
    private List<Ingredient> removedItems;
    private ImageView savedItemImg;
    private final static String TAG = KitchenFragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Kitchen fragment", "OnCreateView success");
        return inflater.inflate(R.layout.fragment_kitchen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Toast.makeText(getContext(), "Press and hold an item to delete", Toast.LENGTH_LONG).show();

        logoutBtn = view.findViewById(R.id.logoutBtn);
        addFoodBtn = view.findViewById(R.id.addFoodBtn);
        savedIngredients = new ArrayList<Ingredient>();
        removedItems = new ArrayList<Ingredient>();
        recyclerView = view.findViewById(R.id.rvSaved);

        // Adapter
        kitchenAdapter = new KitchenAdapter(savedIngredients);
        kitchenAdapter.setOnItemsChangedListener(this::onItemsChanged);

        //savedItemImg = view.findViewById(R.id.savedItemImg);
        recyclerView.setAdapter(kitchenAdapter);

        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        currentUser = (User) ParseUser.getCurrentUser();

        // Initialize Progress Dialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);


        loadSavedItems();


        // brings user to login activity when logout button is pressed
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                final Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getContext(), ExpandableActivity.class);
                startActivity(intent);

            }
        });
    }

    // loads the specific fooditems for the food category
    public void loadSavedItems() {
        pd.show();
        User.Query userQuery = new User.Query();
        userQuery.forCurrentUser().withSavedIngredients();
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if(e == null){
                    for(User user: users){
                        savedIngredients.addAll(user.getSavedIngredients());
                        kitchenAdapter.notifyDataSetChanged();
                    }

                } else {
                    Log.e(TAG, "Error fetching user", e);
                }
                pd.dismiss();
            }
        });

    }

    @Override
    public void onItemsChanged(Ingredient ingredient) {

        LinkedHashSet<Ingredient> ingredients = new LinkedHashSet<Ingredient>();
        ingredients.add(ingredient);

        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            ItemDialogFragment frag = ItemDialogFragment.newInstance(ingredients);
            frag.setTargetFragment(this, 0);
            frag.show(fm, "item_dialog_details");
        }
    }
}
