package com.example.fburecipeapp.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.CalendarFragment;
import com.example.fburecipeapp.fragments.KitchenFragment;
import com.example.fburecipeapp.fragments.ReceiptFragment;
import com.example.fburecipeapp.fragments.RecipeFragment;
import com.example.fburecipeapp.fragments.ScannerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Fragment fragment;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    
    // sets up bottom navigation bar - used to hold and call each fragment/screen
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
                    fragment = new RecipeFragment();
                    break;
                case R.id.action_receipts:
                    fragment = new ReceiptFragment();
                    break;
                case R.id.action_calendar:
                    fragment = new CalendarFragment();
                    break;
            }

            // replaces empty container with whichever fragment is called
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
