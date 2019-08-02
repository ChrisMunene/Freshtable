package com.example.fburecipeapp.models;

import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.util.List;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject {
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_FOODTYPE = "foodType";
    private static final String KEY_OBJECT_ID = "objectId";

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

        public Query whereObjectIds(List<String> objectIds){
            whereContainedIn(KEY_OBJECT_ID, objectIds);
            return this;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Ingredient other = (Ingredient) obj;
        if (this.getObjectId() == null ? other.getObjectId() != null : !this.getObjectId().equals(other.getObjectId()))
        {
            return false;
        }
        return true;
    }
}
