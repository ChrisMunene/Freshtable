package com.example.fburecipeapp.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParseClassName("foodTypes")
public class FoodType extends ParseObject {

    public static final String TYPE = "Type";
    public static final String IMAGE = "Image";
    public static final String OBJECT_ID = "objectId";
<<<<<<< HEAD:app/src/main/java/com/example/fburecipeapp/FoodType.java
    public static final String ITEMS = "Items";
=======
>>>>>>> 803096f... Scanner - Add dialog for editing list items.:app/src/main/java/com/example/fburecipeapp/models/FoodType.java

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

<<<<<<< HEAD:app/src/main/java/com/example/fburecipeapp/FoodType.java
    public String[] getItems(int i) {
        return (getString(ITEMS).split(","));
=======
    public List<String> getFoodItems(){
        String items = getString("Items");
        return Arrays.asList(items.split(","));
>>>>>>> 803096f... Scanner - Add dialog for editing list items.:app/src/main/java/com/example/fburecipeapp/models/FoodType.java
    }

    public static class Query extends ParseQuery<FoodType> {
        public Query() {
            super(FoodType.class);
        }
    }
}
