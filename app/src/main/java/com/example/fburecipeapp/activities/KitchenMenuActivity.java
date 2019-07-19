package com.example.fburecipeapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.ItemsAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class KitchenMenuActivity extends AppCompatActivity {

    public String objectId;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public ArrayList<String> items;
    protected ItemsAdapter itemsAdapter;
    public CheckBox checkBox;
    public Button saveBtn;
    public Boolean isChecked;
    ArrayList<String> selectedItemsList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_menu);

        // need this view to access the checkbox on a separate xml
        View v = LayoutInflater.from(this).inflate(R.layout.single_food_option, null);

        recyclerView = findViewById(R.id.rvItemMenu);
        checkBox = v.findViewById(R.id.checkBox);
        saveBtn = findViewById(R.id.kitchenSaveBtn);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        items = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(items);
        recyclerView.setAdapter(itemsAdapter);

        // objectId tells us which fod  category was chosen so we can show the corresponding item list
        objectId = getIntent().getStringExtra("objectId");

        //Pass the id to load items
        loadItems(objectId);

        // when save button is hit, want to save items that are checked to an array that corresponds to the user
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedItemsList = itemsAdapter.getSelectedItems();
                Log.d("Selected Items", Integer.toString(selectedItemsList.size()));
                ParseUser.getCurrentUser().add("userItems", selectedItemsList);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("Parse", "items added to Parse");
                    }
                });

                Intent intent = new Intent(KitchenMenuActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });
    }

    // loads the specific items for the food category
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
