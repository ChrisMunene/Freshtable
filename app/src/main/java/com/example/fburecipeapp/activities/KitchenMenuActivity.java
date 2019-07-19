package com.example.fburecipeapp.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.adapters.ItemsAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import com.example.fburecipeapp.R;

public class KitchenMenuActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public ArrayList<FoodType> items;
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
        loadItems();
    }

    public void loadItems() {
        FoodType.Query query = new FoodType.Query();
        query.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> type, ParseException e) {
                if (e == null) {
                    Log.d("item count", String.format("%s" , type.size()));
                    //types.clear();
                    items.addAll(type);
                    itemsAdapter.notifyDataSetChanged(); // update adapter
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
