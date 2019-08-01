package com.example.fburecipeapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.helpers.AbstractExpandableDataProvider;
import com.example.fburecipeapp.helpers.ExpandableDataProvider;
import com.example.fburecipeapp.helpers.ExpandableItemIndicator;
import com.example.fburecipeapp.models.FoodType;
import com.example.fburecipeapp.models.Ingredient;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ExpandableAdapter
        extends AbstractExpandableItemAdapter<ExpandableAdapter.MyGroupViewHolder, ExpandableAdapter.MyChildViewHolder> {
    private static final String TAG = "ExpandableItemAdapter";

    // NOTE: Make accessible with short name
    private AbstractExpandableDataProvider mProvider;
    private List<FoodType> mFoodTypes;
    private List<Ingredient> mIngredients;
    private List<Ingredient> selectedIngredients;

    static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        FrameLayout mContainer;
        TextView mTextView;

        MyBaseViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mTextView = v.findViewById(android.R.id.text1);
        }
    }

    static class MyGroupViewHolder extends MyBaseViewHolder {
        ExpandableItemIndicator mIndicator;

        MyGroupViewHolder(View v) {
            super(v);
            mIndicator = v.findViewById(R.id.indicator);
        }
    }

    static class MyChildViewHolder extends MyBaseViewHolder {
        MyChildViewHolder(View v) {
            super(v);
        }
    }

    public ExpandableAdapter(ExpandableDataProvider dataProvider, List<Ingredient> selectedIngredientsList) {
        mProvider = dataProvider;
        mFoodTypes = dataProvider.getFoodTypes();
        mIngredients = dataProvider.getIngredients();
        selectedIngredients = selectedIngredientsList;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    @NonNull
    public MyGroupViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item, parent, false);
        return new MyGroupViewHolder(v);
    }

    @Override
    @NonNull
    public MyChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item, parent, false);
        return new MyChildViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull MyGroupViewHolder holder, int groupPosition, int viewType) {
        // child item
        final AbstractExpandableDataProvider.BaseData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final ExpandableItemState expandState = holder.getExpandState();

        if (expandState.isUpdated()) {
            int bgResId;
            boolean animateIndicator = expandState.hasExpandedStateChanged();

            if (expandState.isExpanded()) {
                bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
            holder.mIndicator.setExpandedState(expandState.isExpanded(), animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        int bgResId = R.drawable.bg_item_normal_state;

        // Check if item was previously selected
        for(Ingredient ingredient: selectedIngredients){
            if(ingredient.getObjectId().equals(item.getObjectId())){
                bgResId = R.drawable.bg_item_selected_state;
                break;
            }
        }

        holder.mContainer.setBackgroundResource(bgResId);

        // Set item click listener
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int bgResourceId = 0;
                // Find clicked item in ingredients
                for(Ingredient ingredient: mIngredients){
                    if(ingredient.getObjectId().equals(item.getObjectId())){

                        // If already selected remove item, else add to selected items
                        if(selectedIngredients.contains(ingredient)){
                            selectedIngredients.remove(ingredient);
                            bgResourceId = R.drawable.bg_item_normal_state;
                            Log.d(TAG, String.format("Removed %s", ingredient.getName()));
                            break;
                        } else {
                            selectedIngredients.add(ingredient);
                            bgResourceId = R.drawable.bg_item_selected_state;
                            Log.d(TAG, String.format("Added %s", ingredient.getName()));
                            break;
                        }
                    }
                }

                // Set current state on UI
               holder.mContainer.setBackgroundResource(bgResourceId);

            }
        });
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(@NonNull MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (mProvider.getGroupItem(groupPosition).isPinned()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        return true;
    }


    public List<Ingredient> getSelectedIngredients(){
        return selectedIngredients;
    }
}