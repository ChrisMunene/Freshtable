package com.example.fburecipeapp.models;

import com.google.android.gms.common.util.Strings;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

@ParseClassName("Recipes")
public class Recipes extends ParseObject {

    private String name = "name";
    private String image = "image";
    private static String ingredientsKey = "ingredients";
    private String allIngredients = "allIngredients";
    private String instructions = "directions";

    // Constructor
    public Recipes() {}

    // Recipe Name
    public String getName() {
        return getString(name);
    }

    public void setRecipe(String name) { put(this.name, name); }

    // Recipe Image
    public ParseFile getImage() {
        return getParseFile(image);
    }

    public void setImage(ParseFile Image) {
        put(this.image, Image);
    }

    // Recipe Ingredients Array (Key for Query)

    // Recipe All Ingredients
    public String getAllIngredients() { return getString(allIngredients); }

    public void setAllIngredients(String allIngredients) { put(this.allIngredients, allIngredients);}


    // Recipe Instructions
    public String getInstructions() { return getString(instructions); }

    public void setInstructions(String instructions) { put(this.instructions, instructions); }


    public static class Query extends ParseQuery<Recipes> {
        public Query() {
            super(Recipes.class);
        }

        public Recipes.Query withOneIngredient(ArrayList<String> ingredients){
            whereContainedIn(ingredientsKey, ingredients  );
            return this;
        }
    }
}
