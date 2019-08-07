package com.example.fburecipeapp.fragments;

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
import com.example.fburecipeapp.models.Recipes;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class CalendarItemCarouselDialogFragment extends DialogFragment {

    CarouselView carouselView;
    TextView carouselLabel;
    ParseFile[] recipeImages;

    private ArrayList<String> ingredientNames;
    private ArrayList<ParseFile> ingredientImages;
    private ArrayList<LocalDate> ingredientBoughtDates;
    private ArrayList<Ingredient> ingredients;

    private LocalDate expirationDate;


    public CalendarItemCarouselDialogFragment(LinkedHashSet<Ingredient> expiringIngredients, LocalDate expirationDate) {
        ingredientNames = new ArrayList<String>();
        ingredientImages = new ArrayList<ParseFile>();
        ingredientBoughtDates = new ArrayList<LocalDate>();
        ingredients = new ArrayList<Ingredient>();
        recipeImages = new ParseFile[3];
        this.expirationDate = expirationDate;

        for (Ingredient ingredient : expiringIngredients) {
            ingredientNames.add(ingredient.getName());
            ingredientImages.add(ingredient.getImage());
            ingredientBoughtDates.add(expirationDate.minusDays(ingredient.getShelfLife()));
            ingredients.add(ingredient);
        }
    }

    public static CalendarItemCarouselDialogFragment newInstance(LinkedHashSet<Ingredient> expiringIngredients, LocalDate expirationDate) {
        CalendarItemCarouselDialogFragment fragment = new CalendarItemCarouselDialogFragment(expiringIngredients, expirationDate);

        Bundle args = new Bundle();
        args.putString("item", expiringIngredients.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.carousel_dialog_fragment_calendar_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        carouselView = view.findViewById(R.id.carouselView);
        carouselLabel = view.findViewById(R.id.ingredientName);

        carouselView.setPageCount(ingredientNames.size()); // change this
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

    private static String[] convertStringToArray(String item) {
        String temp = item.substring(1, item.length() - 1);
        return temp.split(",");
    }


    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.carousel_dialog_details, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.ingredientName);
            ImageView fruitImageView = (ImageView) customView.findViewById(R.id.ingredientImage);
            TextView boughtDateTextView = (TextView) customView.findViewById(R.id.ingredientBoughtDate);
            TextView expirationDateTextView = (TextView) customView.findViewById(R.id.ingredientExpireDate);

            FragmentManager fragmentManager = getFragmentManager();

            ImageView recipeOneImageView = (ImageView) customView.findViewById(R.id.iv_recipeOne);
            ImageView recipeTwoImageView = (ImageView) customView.findViewById(R.id.iv_recipeTwo);
            ImageView recipeThreeImageView = (ImageView) customView.findViewById(R.id.iv_recipeThree);


            Recipes.Query query = new Recipes.Query();
            query.withOneIngredient(ingredients.get(position));
            query.findInBackground(new FindCallback<Recipes>() {
                @Override
                public void done(List<Recipes> recipes, ParseException e) {
                    if (e == null) {

                        Glide.with(getContext())
                                .load(recipes.get(0).getImage().getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(recipeOneImageView);

                        Glide.with(getContext())
                                .load(recipes.get(1).getImage().getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(recipeTwoImageView);

                        Glide.with(getContext())
                                .load(recipes.get(2).getImage().getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(recipeThreeImageView);


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
            });

            Glide.with(getContext()).load(ingredientImages.get(position).getUrl()).into(fruitImageView);
            labelTextView.setText(ingredientNames.get(position));
            boughtDateTextView.setText(ingredientBoughtDates.get(position).toString());
            expirationDateTextView.setText(expirationDate.toString());

            return customView;
        }
    };

}
