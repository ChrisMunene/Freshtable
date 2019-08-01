package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.fburecipeapp.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

public class CalendarItemCarouselDialogFragment extends DialogFragment {

    CarouselView carouselView;
    TextView carouselLabel;

    int[] sampleImages = {R.drawable.temp_image_1, R.drawable.temp_image_2, R.drawable.temp_image_3, R.drawable.temp_image_4, R.drawable.temp_image_5};
    String[] sampleTitles = {"Orange", "Grapes", "Strawberry", "Cherry", "Apricot"};

    public CalendarItemCarouselDialogFragment() {

    }

    public static CalendarItemCarouselDialogFragment newInstance(String item) {
        CalendarItemCarouselDialogFragment fragment = new CalendarItemCarouselDialogFragment();
        Bundle args = new Bundle();
        args.putString("item", item);
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

        carouselView.setPageCount(sampleImages.length);
        carouselView.setSlideInterval(4000);
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


    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.carousel_dialog_details, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.ingredientName);
            ImageView fruitImageView = (ImageView) customView.findViewById(R.id.ingredientImage);

            fruitImageView.setImageResource(sampleImages[position]);
            labelTextView.setText(sampleTitles[position]);

            return customView;
        }
    };
}
