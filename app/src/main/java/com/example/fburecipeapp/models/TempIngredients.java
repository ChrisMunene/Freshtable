package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.time.LocalDate;
import java.util.Date;

@ParseClassName("TempIngredients")
public class TempIngredients extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_CREATEDAT = "createdAt";

    public String getName() { return getString(KEY_NAME); }

    public void setName(String name) { put(KEY_NAME, name); }

    public long getDuration() { return getLong(KEY_DURATION); }

    public void setDuration(int duration) { put(KEY_DURATION, duration); }

    public static class Query extends ParseQuery<TempIngredients> {
        public Query() {
            super(TempIngredients.class);
        }

        public Query getTop() {
            return this;
        }

        public Query withUser() {
            include(KEY_NAME);
            return this;
        }
    }
}
