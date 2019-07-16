package com.example.fburecipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class KitchenFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    protected ArrayList<FoodType> types;
    protected KitchenAdapter kitchenAdapter;

//    final String[] Meats = { "Chicken", "Pork", "Steak", "Sausage", "Lamb", "Bacon", "Ham", "Duck", "Turkey"};
//    final String[] Dairy = {"Butter", "Cheese", "Yogurt", "Milk", "Ice Cream", "Cream"};
//    final String[] Vegetables = {"Carrots", "Tomatoes", "Potatoes", "Lettuce", "Kale", "Cucumber", "Corn", "Peas", "Avocado"};
//    final String[] Fruits = {"Bananas", "Apples", "Pears", "Oranges", "Mangoes", "Grapes", "Kiwi", "Watermelon", "Pineapple", "Peach", "Plum", "Cherries"};

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

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        types = new ArrayList<>();
        kitchenAdapter = new KitchenAdapter(types);
        recyclerView.setAdapter(kitchenAdapter);
        Log.d("Kitchen Fragment", "Adapter set successfully");
        loadTypes();

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
