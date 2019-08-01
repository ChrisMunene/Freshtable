package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.activities.HomeActivity;
import com.example.fburecipeapp.adapters.ExpandableAdapter;
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
        implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private static final String TAG = ExpandableFragment.class.getSimpleName();
    private static final String KEY_SELECTED_INGREDIENTS = "selectedIngredientIds";

    private FloatingActionButton fabSave;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private ProgressDialog pd;

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
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        fabSave = getView().findViewById(R.id.fabSave);

        mLayoutManager = new LinearLayoutManager(requireContext());

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        List<String> selectedIngredientsIds = Parcels.unwrap(getArguments().getParcelable(KEY_SELECTED_INGREDIENTS));

        List<Ingredient> selectedIngredients = getSelectedIngredientFromIds(selectedIngredientsIds);

        //adapter
        final ExpandableAdapter myItemAdapter = new ExpandableAdapter(new ExpandableDataProvider(), selectedIngredients);

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
                            Log.d("Parse", "items added to Parse");
                        }
                    });

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
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

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider_h), true));

        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

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

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mLayoutManager = null;

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

    private List<Ingredient> getSelectedIngredientFromIds(List<String> objectIds){
        Ingredient.Query query =  new Ingredient.Query();
        query.whereObjectIds(objectIds);
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        try {
            ingredients.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ingredients;
    }


}
