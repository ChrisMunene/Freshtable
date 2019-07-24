package com.example.fburecipeapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.fburecipeapp.adapters.EditListAdapter;
import com.example.fburecipeapp.models.FoodType;
import com.example.fburecipeapp.models.ReceiptItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fburecipeapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

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

    private RecyclerView rvIngredients;
    private List<String> foodTypes;
    private List<ReceiptItem> receiptItems;
    private List<String> precheckedIngredients = new ArrayList<>();
    private EditListAdapter adapter;
    private Button submitBtn;
    private static final String TAG = IngredientListDialogFragment.class.getSimpleName();

    public static IngredientListDialogFragment newInstance(List<ReceiptItem> receiptItems) {
        final IngredientListDialogFragment fragment = new IngredientListDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable("ReceiptItems", Parcels.wrap(receiptItems));
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
        submitBtn = view.findViewById(R.id.submitBtn);
        rvIngredients = view.findViewById(R.id.rvFoodTypes);
        foodTypes = new ArrayList<>();
        receiptItems = Parcels.unwrap(getArguments().getParcelable("ReceiptItems"));
        adapter = new EditListAdapter(getContext(), foodTypes, precheckedIngredients);
        rvIngredients.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvIngredients.setLayoutManager(linearLayoutManager);

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

                    getPrecheckedIngredients();
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

    // List the ingredient found in a receipt
    public void getPrecheckedIngredients(){
        for (ReceiptItem receiptItem: receiptItems){
            for(String ingredient: foodTypes){
                // toLowercase used because .contains is case sensitive -- Java SMH :(
                if(receiptItem.getDescription().toLowerCase().contains(ingredient.toLowerCase()) && !precheckedIngredients.contains(ingredient.toLowerCase())){
                    precheckedIngredients.add(ingredient);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

}
