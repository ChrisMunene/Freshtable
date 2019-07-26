package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";

    public String getName() {
        return getString(KEY_NAME);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public static class Query extends ParseQuery<Ingredient> {
        public Query(){
            super(Ingredient.class);
        }
    }

}
