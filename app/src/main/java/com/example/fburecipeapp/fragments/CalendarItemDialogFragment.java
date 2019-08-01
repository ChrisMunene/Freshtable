package com.example.fburecipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fburecipeapp.R;

import java.util.List;

public class CalendarItemDialogFragment extends DialogFragment {

    private TextView itemName;
    private TextView boughtDate;
    private TextView expireDate;
    private List<String> calendarItems;

    public CalendarItemDialogFragment() {
    }

    public static CalendarItemDialogFragment newInstance(String item) {
        CalendarItemDialogFragment fragment = new CalendarItemDialogFragment();
        Bundle args = new Bundle();
        //args.putParcelable("CalendarItems", Parcels.wrap(calendarItems));
        args.putString("item", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_calendar_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        itemName = view.findViewById(R.id.itemTitle);
        boughtDate = view.findViewById(R.id.boughtDate);
        expireDate = view.findViewById(R.id.expireDate);

        //calendarItems = Parcels.unwrap(getArguments().getParcelable("CalendarItems"));
        //String calendarItem = calendarItems.get(0).replaceAll("[\\[\\](){}]", "");
        String item = getArguments().getString("item").replaceAll("[\\[\\](){}]", "");
        itemName.setText(item);


    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

}
