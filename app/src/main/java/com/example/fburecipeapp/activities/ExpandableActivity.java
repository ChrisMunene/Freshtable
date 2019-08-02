package com.example.fburecipeapp.activities;

import android.os.Bundle;


import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.ExpandableFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class ExpandableActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        if (savedInstanceState == null) {
            Fragment fragment = ExpandableFragment.newInstance(new ArrayList<>());
            fragmentManager.beginTransaction().replace(R.id.mContainer, fragment).commitNow();
        }
    }

}