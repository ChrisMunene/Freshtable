package com.example.fburecipeapp.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class FilterRecipeDialogFragment extends DialogFragment {

    private HorizontalScrollView hScroll;
    private ArrayList<Ingredient> ingredients;
    private LinearLayout linearLayout;
    private ChipGroup chipGroup;
    private ArrayList<String> selectedChipGroup;
    private ArrayList<String> names;
    private ImageButton addFiltersBtn;
    private ArrayList<Integer> checkedIds;

    public FilterRecipeDialogFragment(){
    }

    public static FilterRecipeDialogFragment newInstance() {
        FilterRecipeDialogFragment fragment = new FilterRecipeDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_recipe_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hScroll = view.findViewById(R.id.hscrollview);
        linearLayout = view.findViewById(R.id.chipLinearLayout);
        chipGroup = view.findViewById(R.id.chipGroup);
        addFiltersBtn = view.findViewById(R.id.addFiltersBtn);
        selectedChipGroup = new ArrayList<>();
        ingredients = new ArrayList<Ingredient>();
        names = new ArrayList<>();
        checkedIds = new ArrayList<>();

        getDialog().setCanceledOnTouchOutside(true);

        getUserIngredientIds();

        // exits the fragment dialog
        addFiltersBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(getContext(), "filter clicked", Toast.LENGTH_SHORT).show();
                 Log.d("filter", selectedChipGroup.toString());
                 sendBackResult(selectedChipGroup);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void getUserIngredientIds() {
        User.Query query =  new User.Query();
        query.forCurrentUser().withSavedIngredients();
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if(e == null){
                    for(User user: users){
                        ingredients.addAll(user.getSavedIngredients());

                        // create and style chips fot all saved user ingredients
                        for (int i=0; i < ingredients.size(); i++){
                            Chip chip = new Chip(getContext()); //(new ContextThemeWrapper(getContext(), R.style.Widget_MaterialComponents_Chip_Filter));
                            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Filter);
                            chip.setChipDrawable(chipDrawable);
                            ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            chip.setLayoutParams(lp);
                            chip.setText((ingredients.get(i)).getName());
                            chip.setId(i);
                            chip.isCheckable();
                            chip.setCheckedIconVisible(true);
                            chip.setCloseIconVisible(true);
                            chip.setCloseIconTint(getResources().getColorStateList(R.color.colorWhite));
                            chip.setChipEndPadding(12);
                            chip.setChipStartPadding(12);
                            chip.setTextStartPadding(12);
                            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.filter_color));
                            chip.setTextColor(getResources().getColorStateList(R.color.colorWhite));
                            chip.setTypeface(chip.getTypeface(), Typeface.BOLD);

                            // adds ingredient to filter list if that ingredient's chip is checked
                            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        selectedChipGroup.add((chip.getText()).toString());
                                    } else {
                                        selectedChipGroup.remove((chip.getText()).toString());
                                    }
                                }
                            });

                            chipGroup.addView(chip);
                        }

                    }
                }
            }
        });
    }

    public interface Listener {
        void onFinishEditingList(ArrayList<String> selectedChipGroup);
    }

    public void sendBackResult(ArrayList<String> selectedChipGroup) {
        FilterRecipeDialogFragment.Listener listener = (FilterRecipeDialogFragment.Listener) getTargetFragment();
        listener.onFinishEditingList(selectedChipGroup);
        dismiss();
    }
}
