package com.example.fburecipeapp.fragments;

import android.annotation.TargetApi;
import android.graphics.Color;
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
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CalendarFragment extends Fragment implements OnDateSelectedListener{

    private static final String TAG = "CalendarFragment";

    private final OneDayDecorator oneDayDecoratorIcon;
    private Map<LocalDate, LinkedHashSet<Ingredient>> expiringItemsAndAssociatedDates;
    protected ArrayList<Ingredient> allIngredients;
    private TextView expireText;
    private Unbinder unbinder;

    @BindView(R.id.calendarView) MaterialCalendarView widget;

    /**
     * Constructs CalendarFragment
     * Initializes expiringItemsAndAssociatedDates as new HashMap to keep track of all dates on which
     * items will be expiring along with which items
     * Initializes oneDayDecoratorIcon as a new Decorator which is responsible for showing the circle
     * icon on dates an item is expiring on
     * Initializes allIngredients as an ArrayList which contains all ingredients from the receipts
     */
    public CalendarFragment() {
        this.expiringItemsAndAssociatedDates = new HashMap<LocalDate, LinkedHashSet<Ingredient>>();
        this.oneDayDecoratorIcon = new OneDayDecorator();
        this.allIngredients = new ArrayList<Ingredient>();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @TargetApi(26)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expireText = view.findViewById(R.id.expireText);

        fetchTimelineAsync(0);

        widget.setOnDateChangedListener(this);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        widget.addDecorator(oneDayDecoratorIcon);
    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView,
                               @NonNull CalendarDay calendarDay,
                               boolean selected) {
        oneDayDecoratorIcon.setDate(calendarDay.getDate());

        LinkedHashSet<Ingredient> itemsExpiringOnSelectedDate = getItems(calendarDay.getDate());

        if (!itemsExpiringOnSelectedDate.isEmpty()) {
            showCalendarDialog(itemsExpiringOnSelectedDate);
        } else {
            expireText.setText("No Expiring Items Here");
        }
    }

    /**
     * Gets a set of strings of ingredients expiring on given date
     * If there are no items expiring on given date, returns an empty LinkedHashSet
     */
    public LinkedHashSet<Ingredient> getItems(LocalDate calendarDay) {
        LinkedHashSet<Ingredient> items = expiringItemsAndAssociatedDates.get(calendarDay);

        if (items == null) {
            LinkedHashSet<Ingredient> temp = new LinkedHashSet<Ingredient>();
            return temp;
        }

        return items;
    }


    /**
     * Sets up data given a date
     * Calculates expiring date and gets ingredient name to send to setUpExpiringItemsAndAssociatedDates
     */
    private void setUpInitialDataFromServer(Date date) {

        for(Ingredient ingredient : allIngredients) {
            LocalDate localDate = convertToLocalDateViaMilisecond(date);
            localDate = localDate.plusDays(ingredient.getShelfLife());

            setUpExpiringItemsAndAssociatedDates(localDate, ingredient);

        }
        allIngredients.clear();

    }

    /**
     * Given a LocalDate and ingredient name, populates expiringItemsAndAssociatedDates
     */
    private void setUpExpiringItemsAndAssociatedDates(LocalDate day, Ingredient ingredient) {
        if (expiringItemsAndAssociatedDates.containsKey(day)) {

            boolean isContained = false;

            // checks if given ingredient is already set to expire on the same day via another receipt
            for(Ingredient existingIngredient: expiringItemsAndAssociatedDates.get(day)) {
                if (existingIngredient.getName().equals(ingredient.getName())) {
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
                expiringItemsAndAssociatedDates.get(day).add(ingredient);
            }

        } else {
            boolean isContained = false;

            // checks if given ingredient is already set to expire on another date
            for (LinkedHashSet<Ingredient> currentItems: expiringItemsAndAssociatedDates.values()) {
                LinkedHashSet<String> temp = new LinkedHashSet<>();
                for (Ingredient i : currentItems) {
                    temp.add(i.getName());
                }

                if (temp.contains(ingredient.getName())) {
                    isContained = true;
                    break;
                }
            }

            if (!isContained) {
                LinkedHashSet<Ingredient> items = new LinkedHashSet<Ingredient>();
                items.add(ingredient);
                expiringItemsAndAssociatedDates.put(day, items);
            }
        }
    }


    /**
     * Given a Date, converts it to a LocalDate
     */
    @TargetApi(26)
    public LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


    /**
     * Gets all receipts from Parse Server, populates allIngredients with all receipt items from each receipt
     * For all dates on which ingredients are going to expire, adds a blue circle decorator
     */
    public void fetchTimelineAsync(int page) {
        ParseQuery<Receipt> receiptItemQuery = new ParseQuery<Receipt>(Receipt.class);
        receiptItemQuery.include(Receipt.KEY_RECEIPT_ITEMS);

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
                    List<Ingredient> ingredients = receipt.getReceiptItems();
                    if (ingredients != null) {
                        allIngredients.addAll(ingredients);
                        date = receipt.getCreatedAt();
                        setUpInitialDataFromServer(date);
                    }
                }

                ArrayList<CalendarDay> dates = new ArrayList<>();

                // dates on which items will expire
                for (LocalDate temp : expiringItemsAndAssociatedDates.keySet()) {
                    CalendarDay day = CalendarDay.from(temp);
                    dates.add(day);
                }

                widget.addDecorator(new EventDecorator(Color.BLUE, dates));

            }
        });
    }

    /**
     * Shows Calendar Dialog Fragment upon clicking a day which contains expiring items
     */
    private void showCalendarDialog(LinkedHashSet<Ingredient> expiringItems) {

        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            ItemDialogFragment frag = ItemDialogFragment.newInstance(expiringItems);
            frag.setTargetFragment(this, 0);
            frag.show(fm, "item_dialog_details");
        }
    }


}
