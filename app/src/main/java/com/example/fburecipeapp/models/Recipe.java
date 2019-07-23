package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {

    public static final String NAME = "recipeName";
    public static final String IMAGE = "recipeImage";

    // get food category
    public String getRecipe() {
        return getString(NAME);
    }

    public void setRecipe(String name) {
        put(NAME, name);
    }

    // get food category image
    public ParseFile getImage() {
        return getParseFile(IMAGE);
    }

    public void setImage(ParseFile Image) {
        put(IMAGE, Image);
    }

    public static class Query extends ParseQuery<Recipe> {
        public Query() {
            super(Recipe.class);
        }
    }
}
