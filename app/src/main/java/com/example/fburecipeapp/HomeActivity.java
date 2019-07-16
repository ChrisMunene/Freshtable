package com.example.fburecipeapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fburecipeapp.fragments.ScannerFragment;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.action_kitchen:
                    Toast.makeText(HomeActivity.this, "Kitchen", Toast.LENGTH_SHORT);
                    break;
                case R.id.action_scanner:
                    fragment = new ScannerFragment();
                    break;
                case R.id.action_recipes:
                    Toast.makeText(HomeActivity.this, "Kitchen", Toast.LENGTH_SHORT);
                    break;
                case R.id.action_calendar:
                    Toast.makeText(HomeActivity.this, "Kitchen", Toast.LENGTH_SHORT);
                    break;
            }


            //Todo: Remove this if statement when all fragments are complete
            if (fragment != null) fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
