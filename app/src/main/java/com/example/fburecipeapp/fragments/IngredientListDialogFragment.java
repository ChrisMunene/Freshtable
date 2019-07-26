package com.example.fburecipeapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.fburecipeapp.adapters.EditListAdapter;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.ReceiptItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    private EditText titleInput;
    private EditText descriptionInput;
    private List<Ingredient> ingredients;
    private List<ReceiptItem> receiptItems;
    private List<Ingredient> precheckedIngredients = new ArrayList<>();
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
        rvIngredients = view.findViewById(R.id.rvIngredients);
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        ingredients = new ArrayList<>();
        receiptItems = Parcels.unwrap(getArguments().getParcelable("ReceiptItems"));
        adapter = new EditListAdapter(getContext(), ingredients, precheckedIngredients);
        rvIngredients.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvIngredients.setLayoutManager(linearLayoutManager);

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Get selected items
                List<Ingredient> selectedFoodItems = adapter.getSelectedFoodItems();
                sendBackResult(selectedFoodItems);
            }
        });

        loadIngredients();
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
        void onFinishEditingList(String title, String description, List<Ingredient> foodItems);
    }


    private void loadIngredients(){
        Ingredient.Query query = new Ingredient.Query();
        query.findInBackground(new FindCallback<Ingredient>() {
            @Override
            public void done(List<Ingredient> ingredientList, ParseException e) {
                if(e == null){
                    for (Ingredient ingredient: ingredientList) {
                        ingredients.add(ingredient);
                        adapter.notifyItemInserted(ingredients.size() - 1);
                    }
                    getPrecheckedIngredients();
                } else {
                    Log.e(TAG, "Error fetching ingredients", e);
                }
            }
        });
    }

    public void sendBackResult(List<Ingredient> selectedFoodItems){
        Listener listener = (Listener) getTargetFragment();
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        listener.onFinishEditingList(title, description, selectedFoodItems);
        dismiss();
    }

    // List the ingredient found in a receipt
    public void getPrecheckedIngredients(){
        for (ReceiptItem receiptItem: receiptItems){
            for(Ingredient ingredient: ingredients){
                // toLowercase used because .contains is case sensitive -- Java SMH :(
                if(receiptItem.getDescription().toLowerCase().contains(ingredient.getName().toLowerCase()) && !precheckedIngredients.contains(ingredient)){
                    precheckedIngredients.add(ingredient);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

}
