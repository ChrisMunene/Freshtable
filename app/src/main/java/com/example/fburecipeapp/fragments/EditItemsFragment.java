package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.adapters.EditListAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class EditItemsFragment extends DialogFragment {
    private List<String> foodTypes;
    private RecyclerView rvFoodTypes;
    private EditListAdapter adapter;
    private Button cancelBtn;
    private Button submitBtn;

    public static EditItemsFragment newInstance(){
        EditItemsFragment fragment = new EditItemsFragment();
        Bundle args = new Bundle();
        args.putString("title", "Edit List Items");
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_items, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title", "Edit Items");
        getDialog().setTitle(title);
        cancelBtn =view.findViewById(R.id.cancelBtn);
        submitBtn = view.findViewById(R.id.submitBtn);
        rvFoodTypes = view.findViewById(R.id.rvFoodTypes);
        foodTypes = new ArrayList<>();
        adapter = new EditListAdapter(getContext(), foodTypes);
        rvFoodTypes.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvFoodTypes.setLayoutManager(linearLayoutManager);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to parent
                dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Submit selected items to parse server
            }
        });

        loadFoodTypes();
    }

    private void loadFoodTypes() {
        FoodType.Query query = new FoodType.Query();
        query.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> types, ParseException e) {
                if (e == null) {

                    for(FoodType type: types){
                       List foodItems = type.getFoodItems();
                       foodTypes.addAll(foodItems);
                       adapter.notifyDataSetChanged(); // update adapter
                    }

                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
