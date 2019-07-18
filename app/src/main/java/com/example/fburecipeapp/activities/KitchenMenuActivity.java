package com.example.fburecipeapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.ItemsAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class KitchenMenuActivity extends AppCompatActivity {

    public String objectId;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public List<String> items;
    protected ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_menu);

        recyclerView = findViewById(R.id.rvItemMenu);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        items = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(items);
        recyclerView.setAdapter(itemsAdapter);

        // Get the id
        //objectId = Parcels.unwrap(getIntent().getParcelableExtra(foodType.getTypeId()));
        objectId = getIntent().getStringExtra("objectId");
        //Pass the id to load items
        loadItems(objectId);
    }

    public void loadItems(String id) {
        FoodType.Query query = new FoodType.Query();
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> types, ParseException e) {
                if (e == null) {

                    for(FoodType type: types){
                        List foodItems = type.getFoodItems();
                        items.addAll(foodItems);
                        itemsAdapter.notifyDataSetChanged(); // update adapter
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
