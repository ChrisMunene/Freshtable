package com.example.fburecipeapp.activities;

import android.app.ProgressDialog;
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
import com.example.fburecipeapp.models.Ingredient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class KitchenMenuActivity extends AppCompatActivity {

    private String objectId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Ingredient> items;
    private ItemsAdapter itemsAdapter;
    private CheckBox checkBox;
    private Button saveBtn;
    private ProgressDialog progressDialog;
    private static final String TAG = KitchenMenuActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_menu);

        // need this view to access the checkbox on a separate xml
        View singleView = LayoutInflater.from(this).inflate(R.layout.single_food_option, null);

        recyclerView = findViewById(R.id.rvItemMenu);
        checkBox = singleView.findViewById(R.id.checkBox);
        saveBtn = findViewById(R.id.kitchenSaveBtn);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        items = new ArrayList<Ingredient>();
        itemsAdapter = new ItemsAdapter(items);
        recyclerView.setAdapter(itemsAdapter);

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait.");
        progressDialog.setCancelable(false);

        // objectId tells us which food  category was chosen so we can show the corresponding item list
        objectId = getIntent().getStringExtra("objectId");

        //Pass the id to load items
        loadItems(objectId);

        // when save button is hit, want to save items that are checked to an array that corresponds to the user
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ingredient> selectedItemsList = itemsAdapter.getSelectedItems();
                ParseUser.getCurrentUser().addAll("savedIngredients", selectedItemsList);
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
        progressDialog.show();
        FoodType.Query foodTypeQuery = new FoodType.Query();
        foodTypeQuery.whereEqualTo("objectId", id);
        foodTypeQuery.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> types, ParseException e) {
                if (e == null) {

                    for(FoodType type: types){
                        loadIngredients(type);
                    }
                }
                else {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        });

    }

    public void loadIngredients(FoodType type){
        Ingredient.Query ingredientQuery = new Ingredient.Query();
        ingredientQuery.forFoodType(type);
        ingredientQuery.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> ingredients, ParseException e) {
                if(e == null){
                    items.addAll(ingredients);
                    itemsAdapter.notifyDataSetChanged();
                    Log.d(TAG, String.format("Id: %s Ingredients: %s", type.getObjectId(), ingredients.size()));
                } else {
                    Log.e(TAG, "Error fetching ingredients", e);
                }

                progressDialog.dismiss();
            }
        });
    }
}
