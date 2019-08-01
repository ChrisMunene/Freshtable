package com.example.fburecipeapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.fburecipeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcel;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fburecipeapp.activities.ui.main.SectionsPagerAdapter;

import org.parceler.Parcels;

import java.util.List;

public class EditReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_receipt);
        List<String> selectedIngredientIds = getIntent().getStringArrayListExtra("selectedIngredientIds");
        Uri photoUri = Parcels.unwrap(getIntent().getParcelableExtra("receiptImageUri"));
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedIngredientIds, photoUri);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }
}