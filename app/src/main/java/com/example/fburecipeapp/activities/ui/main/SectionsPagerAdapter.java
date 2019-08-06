package com.example.fburecipeapp.activities.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.fragments.ExpandableFragment;
import com.example.fburecipeapp.fragments.ScannerFragment;
import com.example.fburecipeapp.fragments.ViewReceiptFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = {"VIEW", "EDIT"};
    private final Context mContext;
    private final List<String> mSelectedIngredientIds;
    private Uri mPhotoUri;

    public SectionsPagerAdapter(Context context, FragmentManager fm, List<String> selectedIngredientIds, Uri photoUri) {
        super(fm);
        mContext = context;
        mSelectedIngredientIds = selectedIngredientIds;
        mPhotoUri = photoUri;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        Fragment fragment = PlaceholderFragment.newInstance(position + 1);
        switch (position){
            case 0:
               fragment = ViewReceiptFragment.newInstance(mPhotoUri);
               break;
            case 1:
                fragment = ExpandableFragment.newInstance(mSelectedIngredientIds);
                break;
            default:
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}