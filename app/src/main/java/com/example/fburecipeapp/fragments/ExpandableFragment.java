package com.example.fburecipeapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import com.example.fburecipeapp.models.Receipt;
import com.example.fburecipeapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpandableFragment
        extends Fragment
        implements ExpandableAdapter.onSelectedItemsChangedListener, SelectedItemAdapter.onItemsChangedListener, RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private static final String TAG = ExpandableFragment.class.getSimpleName();
    private static final String KEY_SELECTED_INGREDIENTS = "selectedIngredientIds";

    private String title;
    private String description;
    private Uri photoUri;
    private Boolean isEditingReceipt;

    private FloatingActionButton fabSave;
    private RecyclerView rvExpandable;
    private RecyclerView rvSelectedItems;
    private View mLayout;
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

    public static ExpandableFragment newInstance(List<String> selectedIngredientIds, String title, String description, Uri photoUri, Boolean isEditingReceipt) {
        ExpandableFragment fragment = new ExpandableFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_SELECTED_INGREDIENTS, Parcels.wrap(selectedIngredientIds));
        args.putParcelable("photoUri", Parcels.wrap(photoUri));
        args.putString("title", title);
        args.putString("description", description);
        args.putBoolean("isEditingReceipt", isEditingReceipt);
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
        pd = new ProgressDialog(getContext(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);

        pd.show();

        //noinspection ConstantConditions
        mLayout = getView().findViewById(R.id.main_content);
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
        photoUri = Parcels.unwrap(getArguments().getParcelable("photoUri"));
        title = getArguments().getString("title");
        description = getArguments().getString("description");
        isEditingReceipt =getArguments().getBoolean("isEditingReceipt");

        selectedIngredients = new ArrayList<Ingredient>();

        //adapters
        myItemAdapter = new ExpandableAdapter(new ExpandableDataProvider(), selectedIngredients);
        selectedItemsAdapter = new SelectedItemAdapter(getContext(), selectedIngredients, this::onItemsChanged);
        myItemAdapter.setOnSelectedItemsChangedListener(this::onSelectedItemsChanged);

        // wrap for expanding
        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(myItemAdapter);

        //Save Button click listener
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectedIngredients.isEmpty()){

                    pd.show();

                    updateUser();

                    //Post receipt if from editing
                    if(isEditingReceipt) postReceipt();

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

    public void updateUser(){
        User.Query userQuery = new User.Query();
        userQuery.forCurrentUser().withSavedIngredients();
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if(e == null){
                    // Check if user already has selected Ingredients
                    User currentUser = users.get(0);
                    List<Ingredient> userIngredients = currentUser.getSavedIngredients();
                    List<Ingredient> listToSave = removeDuplicates(userIngredients);
                    if(!listToSave.isEmpty()){
                        saveUser(currentUser, listToSave);
                    } else {
                        if(!isEditingReceipt){
                            if(pd.isShowing()) pd.dismiss();
                            Snackbar.make(mLayout, "Selected items already saved.", Snackbar.LENGTH_LONG)
                                    .show(); // Don’t forget to show!
                        }
                    }
                } else {
                    Log.e(TAG, "Error fetching user", e);
                    if(pd.isShowing()) pd.dismiss();
                }

            }
        }); }

    public List<Ingredient> removeDuplicates(List<Ingredient> userIngredients){
        List<Ingredient> listToSave = new ArrayList<Ingredient>();
        for(Ingredient selectedIngredient: selectedIngredients){
            if(!userIngredients.contains(selectedIngredient)){
                listToSave.add(selectedIngredient);
            }
        }

        return listToSave;
    }

    public void saveUser(User currentUser, List<Ingredient> listToSave){
        currentUser.addAll("savedIngredients", listToSave);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("Parse", "items added to Parse");
                    if(pd.isShowing()) pd.dismiss();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    if(pd.isShowing()) pd.dismiss();
                    Log.e(TAG, "Error saving items", e);
                    Toast.makeText(getContext(), "Error saving items", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Creates a new post in Parse
    private void postReceipt(){

        try {

            // Get bitmap from Uri
            Bitmap receiptImageBmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);

            // Convert Bitmap to ByteArray -- To be used when creating a parsefile
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            receiptImageBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            receiptImageBmp.recycle();

            // Covert ByteArray to ParseFile
            ParseFile receiptImage = new ParseFile(generateUniqueFileName(), byteArray);

            Receipt newReceipt = new Receipt();
            newReceipt.setTitle(title);
            newReceipt.setDescription(description);
            newReceipt.setImage(receiptImage);
            newReceipt.setUser(ParseUser.getCurrentUser());
            newReceipt.setReceiptItems(selectedIngredients);

            newReceipt.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Log.d(TAG, "Receipt Saved Successfully");
                    } else {
                        Log.e(TAG, "Failed to post receipt", e);
                    }

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onSelectedItemsChanged() {
        selectedItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemsChanged() {
        myItemAdapter.notifyDataSetChanged();
    }

    // Returns a unique file name using current timestamp.
    public String generateUniqueFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        return imageFileName;
    }
}
