package com.example.fburecipeapp.activities;

import android.os.Bundle;


import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.ExpandableExampleFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ExpandableExampleActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        if (savedInstanceState == null) {
            Fragment fragment = new ExpandableExampleFragment();
            fragmentManager.beginTransaction().replace(R.id.mContainer, fragment).commitNow();
        }
    }

}