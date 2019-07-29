package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.helpers.SwipeableRecyclerViewTouchListener;
import com.example.fburecipeapp.activities.LoginActivity;
import com.example.fburecipeapp.activities.TypeSelectionActivity;
import com.example.fburecipeapp.adapters.KitchenAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.Receipt;
import com.example.fburecipeapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class KitchenFragment extends Fragment {

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
    private final static String TAG = KitchenFragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Kitchen fragment", "OnCreateView success");
        return inflater.inflate(R.layout.fragment_kitchen, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logoutBtn = view.findViewById(R.id.logoutBtn);
        addFoodBtn = view.findViewById(R.id.addFoodBtn);
        savedIngredients = new ArrayList<Ingredient>();
        removedItems = new ArrayList<Ingredient>();
        kitchenAdapter = new KitchenAdapter(savedIngredients);
        recyclerView = view.findViewById(R.id.rvSaved);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(kitchenAdapter);

        layoutManager = new LinearLayoutManager(getContext());
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
                final Intent intent = new Intent(getContext(), TypeSelectionActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {

                    @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }
                            public boolean canSwipeRight(int position) {
                                return false;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    removedItems.add(savedIngredients.get(position));
                                    savedIngredients.remove(position);
                                    kitchenAdapter.notifyItemRemoved(position);

                                    currentUser.setSavedIngredients(savedIngredients);
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null){
                                                Toast.makeText(getContext(), "Item removed.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e(TAG, "Error deleting item", e);
                                            }
                                        }
                                    });


                                }
                                kitchenAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    savedIngredients.remove(position);
                                    kitchenAdapter.notifyItemRemoved(position);
                                }
                                kitchenAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
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

}
