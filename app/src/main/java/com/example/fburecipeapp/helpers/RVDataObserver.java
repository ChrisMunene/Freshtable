package com.example.fburecipeapp.helpers;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Custom implementation of AdapterDataObserver to show empty layouts
 * for RecyclerView when there's no data
 *
 * Usage:
 *
 * adapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, emptyView));
 */
public class RVDataObserver extends RecyclerView.AdapterDataObserver {
    private View emptyView;
    private RecyclerView recyclerView;


    /**
     * Constructor to set an Empty View for the RV
     */
    public RVDataObserver(RecyclerView rv, View ev) {
        this.recyclerView = rv;
        this.emptyView    = ev;
        checkIfEmpty();
    }


    /**
     * Check if Layout is empty and show the appropriate view
     */
    private void checkIfEmpty() {
        if (emptyView != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }


    /**
     Abstract method implementations
     */
    @Override
    public void onChanged() {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
    }

}
