package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@ParseClassName("User")
public class User extends ParseUser {

    public static final String SAVED_ITEMS = "userItems";

    public ArrayList<String> getSavedItems() {
        ArrayList<String> savedData = new ArrayList<String>();
        JSONArray jArray = getJSONArray(SAVED_ITEMS);
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                try {
                    savedData.add(jArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return savedData;
    }

    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }
    }

}
