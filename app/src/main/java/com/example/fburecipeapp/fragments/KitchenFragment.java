package com.example.fburecipeapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.SwipeableRecyclerViewTouchListener;
import com.example.fburecipeapp.activities.LoginActivity;
import com.example.fburecipeapp.activities.TypeSelectionActivity;
import com.example.fburecipeapp.adapters.KitchenAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.parse.ParseUser;

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
    public ImageButton addBtn;
    public CardView card;
    public ImageButton logoutBtn;
    private ParseUser currentUser;
    public ImageButton addFoodBtn;
    public JSONArray JSONsavedItems;
    public List<String> savedItems;


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
        JSONsavedItems = new JSONArray();
        savedItems = new ArrayList<>();
        kitchenAdapter = new KitchenAdapter(savedItems);
        recyclerView = view.findViewById(R.id.rvSaved);
        recyclerView.setAdapter(kitchenAdapter);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        loadSavedItems();


        // brings user to login activity when logout button is pressed
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    ParseUser.logOut();
                    currentUser = ParseUser.getCurrentUser(); // this will now be null
                    final Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    final Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
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
                                    savedItems.remove(position);
                                    kitchenAdapter.notifyItemRemoved(position);
                                }
                                kitchenAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    savedItems.remove(position);
                                    kitchenAdapter.notifyItemRemoved(position);
                                }
                                kitchenAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }

    // loads the specific items for the food category
    public void loadSavedItems() {
        currentUser = ParseUser.getCurrentUser();
        JSONsavedItems = currentUser.getJSONArray("userItems");
        if (JSONsavedItems != null) {
            for (int i = 0; i < JSONsavedItems.length(); i++) {
                try {
                    savedItems.add(JSONsavedItems.getString(i));
                    kitchenAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.d("savedItems", Integer.toString(savedItems.size()));
    }
}
