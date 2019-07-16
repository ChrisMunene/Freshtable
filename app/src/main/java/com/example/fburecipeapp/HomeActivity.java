package com.example.fburecipeapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Fragment fragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            System.out.println("testo");
            switch (item.getItemId()) {
                case R.id.action_kitchen:
                    System.out.println("kitchen");
                    //mTextMessage.setText("My Kitchen");
                    fragment = new KitchenFragment();
                    break;
                case R.id.action_scanner:
                    mTextMessage.setText("Scanner");
                    break;
                case R.id.action_recipes:
                    mTextMessage.setText("Recipes");
                    break;
                case R.id.action_calendar:
                    mTextMessage.setText("Calendar");
                    break;
            }

            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commitNow();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int x = 4;

        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.action_kitchen);

    }

}
