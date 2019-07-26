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

import com.example.fburecipeapp.R;
import com.example.fburecipeapp.decorators.EventDecorator;
import com.example.fburecipeapp.decorators.OneDayDecorator;
import com.example.fburecipeapp.models.TempIngredients;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CalendarFragment extends Fragment implements OnDateSelectedListener{

    private final OneDayDecorator oneDayDecorator;
    private Map<LocalDate, List<String>> realData;

    private static final String TAG = "CalendarFragment";

    protected List<TempIngredients> mIngredients;

    @BindView(R.id.calendarView) MaterialCalendarView widget;
    @BindView(R.id.textView) TextView textView;
    private Unbinder unbinder;

    public CalendarFragment() {
        this.realData = new HashMap<LocalDate, List<String>>();
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

        // Set-up initial text
        textView.setText("No Expired Items Here");

        // Create the data source
        mIngredients = new ArrayList<>();

        fetchTimelineAsync(0);

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
        List<String> items = realData.get(calendarDay);

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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final ArrayList<CalendarDay> dates = new ArrayList<>();

            // dates on which items will expire
            for (LocalDate temp : realData.keySet()) {
                final CalendarDay day = CalendarDay.from(temp);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            // adds dot on respective dates
            widget.addDecorator(new EventDecorator(Color.BLUE, calendarDays));
        }
    }

    /**
     * Set up data
     */
    private void setUpData() {

        for(TempIngredients ingredient : mIngredients) {
            Date date = ingredient.getCreatedAt();
            LocalDate localDate = convertToLocalDateViaMilisecond(date);
            localDate = localDate.plusDays(ingredient.getDuration());

            String item = ingredient.getName();

            populateMap(localDate, item);

        }

    }

    /**
     * Populate map with data
     */
    private void populateMap(LocalDate day, String item) {
        if (realData.containsKey(day) && !realData.containsValue(item)) {
            realData.get(day).add(item);
        } else if (!realData.containsValue(item)){
            List<String> items = new ArrayList<String>();
            items.add(item);
            realData.put(day, items);
        }
    }

    @TargetApi(26)
    public LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        //        LOOK INTO THIS
    }


    public void fetchTimelineAsync(int page) {
        ParseQuery<TempIngredients> tempIngredientsQuery = new ParseQuery<TempIngredients>(TempIngredients.class);
        tempIngredientsQuery.include(TempIngredients.KEY_NAME);

        tempIngredientsQuery.findInBackground(new FindCallback<TempIngredients>() {
            @Override
            public void done(List<TempIngredients> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                mIngredients.addAll(objects);
                setUpData();
            }
        });
    }

}
