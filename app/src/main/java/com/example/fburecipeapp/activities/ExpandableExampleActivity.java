package com.example.fburecipeapp.activities;

import android.os.Bundle;


import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.ExpandableExampleFragment;
import com.example.fburecipeapp.helpers.AbstractExpandableDataProvider;
import com.example.fburecipeapp.helpers.ExampleExpandableDataProviderFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ExpandableExampleActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ExampleExpandableDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mContainer, new ExpandableExampleFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }
    }

    public AbstractExpandableDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ExampleExpandableDataProviderFragment) fragment).getDataProvider();
    }
}