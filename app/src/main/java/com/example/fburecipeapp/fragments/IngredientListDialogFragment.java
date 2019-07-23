package com.example.fburecipeapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.fburecipeapp.adapters.EditListAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fburecipeapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     IngredientListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link IngredientListDialogFragment.Listener}.</p>
 */
public class IngredientListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private RecyclerView rvIngredients;
    private List<String> foodTypes;
    private EditListAdapter adapter;
    private Button cancelBtn;
    private Button submitBtn;

    // TODO: Customize parameters
    public static IngredientListDialogFragment newInstance() {
        final IngredientListDialogFragment fragment = new IngredientListDialogFragment();
        final Bundle args = new Bundle();
       // args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn =view.findViewById(R.id.cancelBtn);
        submitBtn = view.findViewById(R.id.submitBtn);
        rvIngredients = view.findViewById(R.id.rvFoodTypes);
        foodTypes = new ArrayList<>();
        adapter = new EditListAdapter(getContext(), foodTypes);
        rvIngredients.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvIngredients.setLayoutManager(linearLayoutManager);


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
                // Get selected items
                List<String> selectedFoodItems = adapter.getSelectedFoodItems();
                sendBackResult(selectedFoodItems);
            }
        });

        loadFoodTypes();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface Listener {
        void onFinishEditingList(List<String> foodItems);
    }

    private void loadFoodTypes() {
        FoodType.Query query = new FoodType.Query();
        query.findInBackground(new FindCallback<FoodType>() {
            public void done(List<FoodType> types, ParseException e) {
                if (e == null) {

                    // For each food type, get individual ingredients
                    for(FoodType type: types){
                        List items = type.getFoodItems();
                        foodTypes.addAll(items);
                        adapter.notifyDataSetChanged();
                    }

                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendBackResult(List<String> selectedFoodItems){
        Listener listener = (Listener) getTargetFragment();
        listener.onFinishEditingList(selectedFoodItems);
        dismiss();
    }

}
