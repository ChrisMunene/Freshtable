package com.example.fburecipeapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.Nullable;

import com.example.fburecipeapp.activities.EditReceiptActivity;
import com.example.fburecipeapp.adapters.EditListAdapter;
import com.example.fburecipeapp.models.Ingredient;
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
import java.util.HashMap;
import java.util.List;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     IngredientListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link IngredientListDialogFragment.Listener}.</p>
 */
public class IngredientListDialogFragment extends BottomSheetDialogFragment implements EditListAdapter.AddButtonClickListener {

    private RecyclerView rvIngredients;
    private EditText titleInput;
    private EditText descriptionInput;
    private List<String> receiptItems;
    private List<Ingredient> precheckedIngredients = new ArrayList<>();
    private HashMap<String, Ingredient> ingredientHashMap = new HashMap<String, Ingredient>();
    private EditListAdapter adapter;
    private Button submitBtn;
    private Uri photoUri;
    private String photoFilePath;
    private static final String TAG = IngredientListDialogFragment.class.getSimpleName();

    public static IngredientListDialogFragment newInstance(List<String> receiptItems, Uri photoUri, String photoFilePath) {
        final IngredientListDialogFragment fragment = new IngredientListDialogFragment();
        final Bundle args = new Bundle();
        args.putParcelable("ReceiptItems", Parcels.wrap(receiptItems));
        args.putParcelable("receiptImageUri", Parcels.wrap(photoUri));
        args.putString("photoFilePath", photoFilePath);
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
        receiptItems = Parcels.unwrap(getArguments().getParcelable("ReceiptItems"));
        photoUri = Parcels.unwrap(getArguments().getParcelable("receiptImageUri"));
        photoFilePath = getArguments().getString("photoFilePath");
        adapter = new EditListAdapter(getContext(), precheckedIngredients);
        adapter.setOnAddBtnClickedListener(this::onAddButtonClicked);
        rvIngredients.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvIngredients.setLayoutManager(linearLayoutManager);

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Get selected items
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();

                if(title.isEmpty()){
                   titleInput.setError("Please enter a title");
                } else if(description.isEmpty()) {
                    descriptionInput.setError("Please enter a description");
                } else {
                    List<Ingredient> selectedIngredients = adapter.getSelectedFoodItems();
                    sendBackResult(selectedIngredients);
                }

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

    @Override
    public void onAddButtonClicked() {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();

        if(title.isEmpty()){
            titleInput.setError("Please enter a title");
        } else if(description.isEmpty()) {
            descriptionInput.setError("Please enter a description");
        } else {
            List<Ingredient> selectedIngredients = adapter.getSelectedFoodItems();
            ArrayList selectedIngredientIds = new ArrayList<String>();
            for(Ingredient ingredient: selectedIngredients){
                selectedIngredientIds.add(ingredient.getObjectId());
            }
            final Intent intent = new Intent(getContext(), EditReceiptActivity.class);
            intent.putParcelableArrayListExtra("selectedIngredientIds", selectedIngredientIds);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("photoFilePath", photoFilePath);
            intent.putExtra("receiptImageUri", Parcels.wrap(photoUri));
            startActivity(intent);
        }
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
                        List<String> keywords = ingredient.getKeywords();
                        for(String keyword: keywords){
                            ingredientHashMap.put(keyword, ingredient);
                        }
                    }
                    if(receiptItems != null) getPrecheckedIngredients();
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
        for (String receiptItem: receiptItems){
            if(ingredientHashMap.containsKey(receiptItem)){
                Ingredient ingredient = ingredientHashMap.get(receiptItem);
                if(!precheckedIngredients.contains(ingredient)) {
                    precheckedIngredients.add(ingredient);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

}
