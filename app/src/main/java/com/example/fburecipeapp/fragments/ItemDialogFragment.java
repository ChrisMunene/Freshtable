package com.example.fburecipeapp.fragments;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class ItemDialogFragment extends DialogFragment {

    CarouselView carouselView;
    TextView carouselIngredientNameLabel;

    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> ingredientNames;
    private ArrayList<ParseFile> ingredientImages;
    private ArrayList<LocalDate> ingredientBoughtDates;
    private ArrayList<LocalDate> ingredientExpirationDates;

    /**
     * Constructs ItemDialogFragment
     * Initializes all relevant data fields
     * @param allIngredients: contains a set of all ingredients to be displayed
     */
    public ItemDialogFragment(LinkedHashSet<Ingredient> allIngredients) {
        ingredients = new ArrayList<Ingredient>();
        ingredientNames = new ArrayList<String>();
        ingredientImages = new ArrayList<ParseFile>();
        ingredientBoughtDates = new ArrayList<LocalDate>();
        ingredientExpirationDates = new ArrayList<LocalDate>();

        for (Ingredient ingredient : allIngredients) {
            ingredients.add(ingredient);
            ingredientNames.add(ingredient.getName());
            ingredientImages.add(ingredient.getImage());
            LocalDate ingredientBoughtDate = convertToLocalDateViaMilisecond(ingredient.getCreatedAt());
            ingredientBoughtDates.add(ingredientBoughtDate);
            ingredientExpirationDates.add(ingredientBoughtDate.plusDays(ingredient.getShelfLife()));
        }
    }

    public static ItemDialogFragment newInstance(LinkedHashSet<Ingredient> allIngredients) {
        ItemDialogFragment fragment = new ItemDialogFragment(allIngredients);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.carousel_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carouselView = view.findViewById(R.id.carouselView);
        carouselIngredientNameLabel = view.findViewById(R.id.ingredientName);

        carouselView.setPageCount(ingredientNames.size());
        carouselView.setSlideInterval(3000);
        carouselView.setViewListener(viewListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    /**
     * Given a Date, converts it to a LocalDate
     */
    @TargetApi(26)
    public org.threeten.bp.LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.item_dialog_details, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.ingredientName);
            ImageView ingredientImageView = (ImageView) customView.findViewById(R.id.ingredientImage);
            TextView boughtDateTextView = (TextView) customView.findViewById(R.id.ingredientBoughtDate);
            TextView expirationDateTextView = (TextView) customView.findViewById(R.id.ingredientExpireDate);

            FragmentManager fragmentManager = getFragmentManager();

            ImageView recipeOneImageView = (ImageView) customView.findViewById(R.id.iv_recipeOne);
            ImageView recipeTwoImageView = (ImageView) customView.findViewById(R.id.iv_recipeTwo);
            ImageView recipeThreeImageView = (ImageView) customView.findViewById(R.id.iv_recipeThree);


            Recipe.Query query = new Recipe.Query();
            query.withOneIngredient(ingredients.get(position));
            query.findInBackground(new FindCallback<Recipe>() {
                @Override
                public void done(List<Recipe> recipes, ParseException e) {
                    if (e == null) {

                        if (recipes.size() == 0) {
                            return;
                        }

                        if (recipes.size() == 1 || recipes.size() == 2 || recipes.size() >= 3) {
                            Glide.with(getContext())
                                    .load(recipes.get(0).getImage().getUrl())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(recipeOneImageView);

                            recipeOneImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Fragment fragment = new DetailsFragment(recipes.get(0).getObjectId()); // CHANGE THIS HERE
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.flContainer, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    dismiss();
                                }
                            });
                        }

                        if (recipes.size() == 2 || recipes.size() >= 3) {
                            Glide.with(getContext())
                                    .load(recipes.get(1).getImage().getUrl())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(recipeTwoImageView);

                            recipeTwoImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Fragment fragment = new DetailsFragment(recipes.get(1).getObjectId());
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.flContainer, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    dismiss();
                                }
                            });
                        }

                        if (recipes.size() >= 3) {
                            Glide.with(getContext())
                                    .load(recipes.get(2).getImage().getUrl())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(recipeThreeImageView);


                            recipeThreeImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Fragment fragment = new DetailsFragment(recipes.get(2).getObjectId());
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.flContainer, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    dismiss();
                                }
                            });
                        }
                    }
                }
            });

            Glide.with(getContext()).load(ingredientImages.get(position).getUrl()).into(ingredientImageView);
            labelTextView.setText(ingredientNames.get(position));
            boughtDateTextView.setText(ingredientBoughtDates.get(position).toString());
            expirationDateTextView.setText(ingredientExpirationDates.get(position).toString());

            return customView;
        }
    };
}
