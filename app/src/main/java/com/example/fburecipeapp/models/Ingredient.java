package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_FOODTYPE = "foodType";
    private static final String KEY_SHELF_LIFE = "shelfLife";

    public int getShelfLife() { return getInt(KEY_SHELF_LIFE); }

    public void setShelfLife(int shelfLife) { put(KEY_SHELF_LIFE, shelfLife); }

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

        public Query forFoodType(FoodType type){
            whereEqualTo(KEY_FOODTYPE, type);
            return this;
        }
    }



}
