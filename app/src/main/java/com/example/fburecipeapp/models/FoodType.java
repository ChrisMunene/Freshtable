package com.example.fburecipeapp.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.List;

@ParseClassName("foodTypes")
public class FoodType extends ParseObject {

    private static final String TYPE = "Type";
    private static final String IMAGE = "Image";
    private static final String ITEMS = "Items";

    // get food category
    public String getType() {
        return getString(TYPE);
    }

    public void setType(String type) {
        put(TYPE, type);
    }

    // get food category image
    public ParseFile getImage() {
        return getParseFile(IMAGE);
    }

    public void setImage(ParseFile Image) {
        put(IMAGE, Image);
    }

    // get specific food items
    public List<String> getFoodItems(){
        String items = getString("Items");
        return Arrays.asList(items.split(","));
    }

    public static class Query extends ParseQuery<FoodType> {
        public Query() {
            super(FoodType.class);
        }
    }
}
