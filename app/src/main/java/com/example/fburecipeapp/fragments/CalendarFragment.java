package com.example.fburecipeapp.fragments;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.decorators.EventDecorator;
import com.example.fburecipeapp.decorators.OneDayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CalendarFragment extends Fragment implements OnDateSelectedListener{

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private static final Map<LocalDate, List<String>> fakeData = new HashMap<LocalDate, List<String>>();

    @BindView(R.id.calendarView) MaterialCalendarView widget;
    @BindView(R.id.textView) TextView textView;
    private Unbinder unbinder;

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

        // Set-up initial text
        textView.setText("No Expired Items Here");

        // Set-up fake data
        setUpData();

        widget.setOnDateChangedListener(this);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        final LocalDate instance =  LocalDate.now();
        widget.setSelectedDate(instance);

        final LocalDate min = LocalDate.of(instance.getYear(), Month.JANUARY, 1);
        final LocalDate max = LocalDate.of(instance.getYear(), Month.DECEMBER, 31);

        widget.state().edit().setMinimumDate(min).setMaximumDate(max).commit();

        widget.addDecorator(oneDayDecorator);

        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
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
        widget.invalidateDecorators();
        textView.setText(b ? getItems(calendarDay.getDate()).toString() : "No Expired Items Here");
    }

    /**
     * Get items to set text when a date is clicked
     */
    public List<String> getItems(LocalDate calendarDay) {
        List<String> items = fakeData.get(calendarDay);

        if (items == null) {
            List<String> temp = new ArrayList<String>();
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final ArrayList<CalendarDay> dates = new ArrayList<>();

            // dates on which items will expire
            for (LocalDate temp : fakeData.keySet()) {
                final CalendarDay day = CalendarDay.from(temp);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            // adds dot on respective dates
            widget.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }

    /**
     * Set up fake data
     * TODO: replace with information from Parse
     */
    private void setUpData() {
        populateMap(LocalDate.of(2019, 7, 24), "Bread");
        populateMap(LocalDate.of(2019, 7, 28), "Milk");
        populateMap(LocalDate.of(2019, 8, 5), "Eggs");
        populateMap(LocalDate.of(2019, 8, 8), "Strawberries");
        populateMap(LocalDate.of(2019, 8, 8), "Raspberries");
        populateMap(LocalDate.of(2019, 7, 30), "Banana");
        populateMap(LocalDate.of(2019, 7, 28), "Cheese");
        populateMap(LocalDate.of(2019, 8, 12), "Potatoes");
        populateMap(LocalDate.of(2019, 9, 15), "Butter");
        populateMap(LocalDate.of(2019, 7, 29), "Fish");
        populateMap(LocalDate.of(2019, 7, 29), "Chicken");
        populateMap(LocalDate.of(2020, 7, 24), "Honey");
        populateMap(LocalDate.of(2019, 8, 23), "Yogurt");
        populateMap(LocalDate.of(2020, 8, 7), "Peanut Butter");
        populateMap(LocalDate.of(2019, 7, 30), "Ham");
    }

    /**
     * Populate map with fake data
     */
    private void populateMap(LocalDate day, String item) {
        if (fakeData.containsKey(day)) {
            fakeData.get(day).add(item);
        } else {
            List<String> items = new ArrayList<String>();
            items.add(item);
            fakeData.put(day, items);
        }
    }

//    @TargetApi(26)
//    public java.time.LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
//        return Instant.ofEpochMilli(dateToConvert.getTime())
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//    }
}
