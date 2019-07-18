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

import com.example.fburecipeapp.activities.LoginActivity;
import com.example.fburecipeapp.models.FoodType;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.KitchenAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Kitchen fragment", "OnCreateView success");
        return inflater.inflate(R.layout.fragment_kitchen, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d("Kitchen Fragment", "Adapter set successfully");
        recyclerView = view.findViewById(R.id.rvTypes);
        addBtn = view.findViewById(R.id.addBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        currentUser = ParseUser.getCurrentUser();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        types = new ArrayList<>();
        kitchenAdapter = new KitchenAdapter(types);
        recyclerView.setAdapter(kitchenAdapter);
        Log.d("Kitchen Fragment", "Adapter set successfully");
        loadTypes();


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

    }

    protected void loadTypes() {
        FoodType.Query query = new FoodType.Query();
        query.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> type, ParseException e) {
                if (e == null) {
                    Log.d("item count", String.format("%s" , type.size()));
                    //types.clear();
                    types.addAll(type);
                    kitchenAdapter.notifyDataSetChanged(); // update adapter
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
