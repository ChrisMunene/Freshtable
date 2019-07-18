package com.example.fburecipeapp.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@ParseClassName("foodTypes")
public class FoodType extends ParseObject {

    public static final String TYPE = "Type";
    public static final String IMAGE = "Image";
    public static final String OBJECT_ID = "objectId";
    public static final String ITEMS = "Items";

    public String getType() {
        return getString(TYPE);
    }

    public void setType(String type) {
        put(TYPE, type);
    }

    public ParseFile getImage() {
        return getParseFile(IMAGE);
    }

    public void setImage(ParseFile Image) {
        put(IMAGE, Image);
    }

    public String getObject() {
        return getString(OBJECT_ID);
    }

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
