package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcel;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {

    String recipeName = "recipeName";
    String recipeImage = "recipeImage";

    public Recipe() {}

    // get food category
    public String getName() {
        return getString(recipeName);
    }

    public void setRecipe(String name) { put(recipeName, name); }

    // get food category image
    public ParseFile getImage() {
        return getParseFile(recipeImage);
    }

    public void setImage(ParseFile Image) {
        put(recipeImage, Image);
    }

    public static class Query extends ParseQuery<Recipe> {
        public Query() {
            super(Recipe.class);
        }
    }
}
