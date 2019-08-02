package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.activities.HomeActivity;
import com.example.fburecipeapp.adapters.ExpandableAdapter;
import com.example.fburecipeapp.adapters.SelectedItemAdapter;
import com.example.fburecipeapp.helpers.ExpandableDataProvider;
import com.example.fburecipeapp.models.Ingredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ExpandableFragment
        extends Fragment
        implements ExpandableAdapter.onSelectedItemsChangedListener, RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private static final String TAG = ExpandableFragment.class.getSimpleName();
    private static final String KEY_SELECTED_INGREDIENTS = "selectedIngredientIds";

    private FloatingActionButton fabSave;
    private RecyclerView rvExpandable;
    private RecyclerView rvSelectedItems;
    private RecyclerView.LayoutManager expandableLayoutManager;
    private RecyclerView.LayoutManager selectedItemsLayoutManager;
    private RecyclerView.Adapter selectedItemsAdapter;
    private ExpandableAdapter myItemAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private ProgressDialog pd;
    private List<Ingredient> selectedIngredients;

    public ExpandableFragment() {
        super();
    }

    public static ExpandableFragment newInstance(List<String> selectedIngredientIds) {
        ExpandableFragment fragment = new ExpandableFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_SELECTED_INGREDIENTS, Parcels.wrap(selectedIngredientIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Progress Dialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

        pd.show();

        //noinspection ConstantConditions
        View mLayout = getView().findViewById(R.id.main_content);
        rvExpandable = getView().findViewById(R.id.rvExpandable);
        rvSelectedItems = getView().findViewById(R.id.rvSelectedItems);
        fabSave = getView().findViewById(R.id.fabSave);

        expandableLayoutManager = new LinearLayoutManager(requireContext());
        selectedItemsLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        List<String> selectedIngredientsIds = Parcels.unwrap(getArguments().getParcelable(KEY_SELECTED_INGREDIENTS));

        selectedIngredients = new ArrayList<Ingredient>();

        //adapters
        myItemAdapter = new ExpandableAdapter(new ExpandableDataProvider(), selectedIngredients);
        selectedItemsAdapter = new SelectedItemAdapter(getContext(), selectedIngredients);
        myItemAdapter.setOnSelectedItemsChangedListener(this::onSelectedItemsChanged);

        // wrap for expanding
        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(myItemAdapter);

        //Save Button click listener
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Ingredient> selectedIngredients = myItemAdapter.getSelectedIngredients();
                if(selectedIngredients.size() > 0){
                    Log.d(TAG, String.format("Selected Item Count: %s", selectedIngredients.size()));
                    ParseUser.getCurrentUser().addAll("savedIngredients", selectedIngredients);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Log.d("Parse", "items added to Parse");
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "Error saving items", e);
                                Toast.makeText(getContext(), "Error saving items", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Snackbar.make(mLayout, "Please select some items", Snackbar.LENGTH_LONG)
                            .show(); // Don’t forget to show!
                }

            }
        });

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);

        // Setup expandable RecyclerView
        rvExpandable.setLayoutManager(expandableLayoutManager);
        rvExpandable.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        rvExpandable.setItemAnimator(animator);
        rvExpandable.setHasFixedSize(false);

        //Setup SelectedItems RecyclerView
        rvSelectedItems.setLayoutManager(selectedItemsLayoutManager);
        rvSelectedItems.setAdapter(selectedItemsAdapter);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            rvExpandable.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z1)));
        }
        rvExpandable.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider_h), true));

        mRecyclerViewExpandableItemManager.attachRecyclerView(rvExpandable);

        getSelectedIngredientFromIds(selectedIngredientsIds);

        pd.dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current state to support screen rotation, etc...
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager.release();
            mRecyclerViewExpandableItemManager = null;
        }

        if (rvExpandable != null) {
            rvExpandable.setItemAnimator(null);
            rvExpandable.setAdapter(null);
            rvExpandable = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        expandableLayoutManager = null;

        super.onDestroyView();
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser, Object payload) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.list_item_height);
        int margin = (int) (getActivity().getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, margin, margin);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    private void getSelectedIngredientFromIds(List<String> objectIds){
        Ingredient.Query query =  new Ingredient.Query();
        query.whereObjectIds(objectIds);
        try {
            selectedIngredients.addAll(query.find());
            myItemAdapter.notifyDataSetChanged();
            selectedItemsAdapter.notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSelectedItemsChanged() {
        selectedItemsAdapter.notifyDataSetChanged();
    }
}
