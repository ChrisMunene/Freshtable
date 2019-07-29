package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    private static final String KEY_SAVED_INGREDIENTS = "savedIngredients";
    private static final String KEY_OBJECT_ID = "objectId";

    public List<Ingredient> getSavedIngredients(){
        return getList(KEY_SAVED_INGREDIENTS);
    }

    public void setSavedIngredients(List<Ingredient> ingredients){
        put(KEY_SAVED_INGREDIENTS, ingredients);
    }

    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }

        public Query forCurrentUser(){
            String objectId = getCurrentUser().getObjectId();
            whereEqualTo(KEY_OBJECT_ID, objectId);
            return this;
        }

        public Query withSavedIngredients(){
            include(KEY_SAVED_INGREDIENTS);
            return this;
        }
    }

}
