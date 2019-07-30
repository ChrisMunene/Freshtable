package com.example.fburecipeapp.fragments;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.decorators.EventDecorator;
import com.example.fburecipeapp.decorators.OneDayDecorator;
import com.example.fburecipeapp.models.Ingredient;
import com.example.fburecipeapp.models.Receipt;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CalendarFragment extends Fragment implements OnDateSelectedListener{

    private static final String TAG = "CalendarFragment";

    private final OneDayDecorator oneDayDecorator;
    private Map<LocalDate, LinkedHashSet<String>> expiringItemsAndAssociatedDates;

    protected ArrayList<Ingredient> mIngredients;


    private List<String> calendarItems;
    boolean decoratorPresent;

    private TextView expireText;

    @BindView(R.id.calendarView) MaterialCalendarView widget;
    //@BindView(R.id.textView) TextView textView;
    private Unbinder unbinder;

    public CalendarFragment() {
        this.expiringItemsAndAssociatedDates = new HashMap<LocalDate, LinkedHashSet<String>>();
        this.oneDayDecorator = new OneDayDecorator();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @TargetApi(26)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expireText = view.findViewById(R.id.expireText);

        // Set-up initial text
        //textView.setText("No Expired Items Here");

        // Create the data source
        mIngredients = new ArrayList<>();
        calendarItems = new ArrayList<String>();

        fetchTimelineAsync(0);

        widget.setOnDateChangedListener(this);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        final LocalDate instance =  LocalDate.now();
        widget.setSelectedDate(instance);

        final LocalDate min = LocalDate.of(instance.getYear(), Month.JANUARY, 1);
        final LocalDate max = LocalDate.of(instance.getYear(), Month.DECEMBER, 31);

        widget.state().edit().setMinimumDate(min).setMaximumDate(max).commit();

        widget.addDecorator(oneDayDecorator);

        // new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
        new ApiSimulator().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView,
                               @NonNull CalendarDay calendarDay,
                               boolean b) {
        oneDayDecorator.setDate(calendarDay.getDate());

        //widget.invalidateDecorators();
        String item = getItems(calendarDay.getDate()).toString();

        if (decoratorPresent == true) {
            showCalendarDialog(item);
        }
        else {
            expireText.setText("No Expiring Items Here");

        }

       // textView.setText(b ? getItems(calendarDay.getDate()).toString() : "No Expired Items Here");
    }

    /**
     * Get items to set text when a date is clicked
     */
    public LinkedHashSet<String> getItems(LocalDate calendarDay) {
        LinkedHashSet<String> items = expiringItemsAndAssociatedDates.get(calendarDay);

        if (items == null) {
            LinkedHashSet<String> temp = new LinkedHashSet<String>();
            temp.add("No Expired Items Here");
            return temp;
        }

        return items;
    }

    /**
     * Simulate an API call to show how to add decorators
     */
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

//            Handler handler = new Handler();
//
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    handler.postDelayed(this, 1500);
//                }
//            }, 1500);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<CalendarDay> dates = new ArrayList<>();

            // dates on which items will expire
            for (LocalDate temp : expiringItemsAndAssociatedDates.keySet()) {
                CalendarDay day = CalendarDay.from(temp);
                dates.add(day);
            }

            return dates;
        }


        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            // adds dot on respective dates
            widget.addDecorator(new EventDecorator(Color.BLUE, calendarDays));
            decoratorPresent = true;
        }
    }

    /**
     * Set up data
     */
    private void setUpData(Date date) {

        for(Ingredient ingredient : mIngredients) {
            LocalDate localDate = convertToLocalDateViaMilisecond(date);
            localDate = localDate.plusDays(ingredient.getShelfLife());

            String item = ingredient.getName();

            populateMap(localDate, item);

        }
        mIngredients.clear();

    }

    /**
     * Populate map with data
     */
    private void populateMap(LocalDate day, String item) {
        if (expiringItemsAndAssociatedDates.containsKey(day)) {
            expiringItemsAndAssociatedDates.get(day).add(item);
        } else {
            boolean isContained = false;

            for (LinkedHashSet<String> currentItems: expiringItemsAndAssociatedDates.values()) {
                if (currentItems.contains(item)) {
                    isContained = true;
                    break;
                }
            }

            if (isContained == false) {
                LinkedHashSet<String> items = new LinkedHashSet<String>();
                items.add(item);
                expiringItemsAndAssociatedDates.put(day, items);
            }
        }
    }

    @TargetApi(26)
    public LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }



    public void fetchTimelineAsync(int page) {
        ParseQuery<Receipt> receiptItemQuery = new ParseQuery<Receipt>(Receipt.class);
        receiptItemQuery.include(Receipt.KEY_RECEIPT_ITEMS); // should this be public???

        receiptItemQuery.findInBackground(new FindCallback<Receipt>() {
            @Override
            public void done(List<Receipt> receipts, ParseException e) {
                Date date;

                if (e != null) {
                    Log.e(TAG, "Error with query!");
                    e.printStackTrace();
                    return;
                }

                for(Receipt receipt: receipts){
                    mIngredients.addAll(receipt.getReceiptItems());
                    date = receipt.getCreatedAt();
                    setUpData(date);
                }
            }
        });
    }

    private void showCalendarDialog(String item) {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            CalendarItemDialogFragment frag = CalendarItemDialogFragment.newInstance(item);
            frag.setTargetFragment(this, 0);
            frag.show(fm, "dialog_fragment_calendar_items");
        }
    }

}
