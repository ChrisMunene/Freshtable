package com.example.fburecipeapp.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.fburecipeapp.fragments.CalendarFragment;
import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.KitchenFragment;
import com.example.fburecipeapp.fragments.ScannerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    private Fragment fragment;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_kitchen:
                    fragment = new KitchenFragment();
                    break;
                case R.id.action_scanner:
                    fragment = new ScannerFragment();
                    break;
                case R.id.action_recipes:
                    break;
                case R.id.action_calendar:
                    fragment = new CalendarFragment();
                    break;
            }

            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commitNow();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.action_kitchen);

    }

}
