package com.example.fburecipeapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.TypeAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TypeSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    protected ArrayList<FoodType> types;
    protected TypeAdapter typeAdapter;
    public ImageButton addBtn;
    public CardView card;
    public ImageButton logoutBtn;
    private ParseUser currentUser;
    public ImageButton addFoodBtn;
    public ImageButton returnBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_type);


        recyclerView = findViewById(R.id.rvTypes);
        addBtn = findViewById(R.id.addBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        addFoodBtn = findViewById(R.id.addFoodBtn);
        returnBtn = findViewById(R.id.returnBtn);
        currentUser = ParseUser.getCurrentUser();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        types = new ArrayList<>();
        typeAdapter = new TypeAdapter(types);
        recyclerView.setAdapter(typeAdapter);
        loadTypes();

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TypeSelectionActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadTypes() {
        FoodType.Query query = new FoodType.Query();
        query.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> type, ParseException e) {
                if (e == null) {
                    Log.d("item count", String.format("%s" , type.size()));
                    types.addAll(type);
                    typeAdapter.notifyDataSetChanged(); // update adapter
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}

