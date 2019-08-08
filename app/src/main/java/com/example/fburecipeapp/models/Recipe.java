package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipes")
public class Recipe extends ParseObject {

    private static String KEY_NAME = "name";
    private static String KEY_IMAGE = "image";
    private static String KEY_INGREDIENTS = "ingredients";
    private static String KEY_ALL_INGREDIENTS = "allIngredients";
    private static String KEY_INSTRUCTIONS = "directions";
    private static String KEY_CONTAINS_INGREDIENTS = "ingredients";
    private static String KEY_VIDEO_ID = "youtubeVideoId";


    // Constructor
    public Recipe() {}

    // Recipe Name
    public String getName() {
        return getString(KEY_NAME);
    }

    public void setRecipe(String name) { put(KEY_NAME, name); }

    // Recipe Image
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile Image) {
        put(KEY_IMAGE, Image);
    }

    // Recipe All Ingredients
    public String getAllIngredients() { return getString(KEY_ALL_INGREDIENTS); }
    public void setAllIngredients(String allIngredients) { put(KEY_ALL_INGREDIENTS, allIngredients);}


    // Recipe Instructions
    public String getInstructions() { return getString(KEY_INSTRUCTIONS); }
    public void setInstructions(String instructions) { put(KEY_INSTRUCTIONS, instructions); }

    // Recipe Contains
    public List<Ingredient> getContainsIngredients() { return getList(KEY_CONTAINS_INGREDIENTS); }
    public void setContainsIngredients(ArrayList<Ingredient> containsIngredients) { put(KEY_CONTAINS_INGREDIENTS, containsIngredients); }


    // Recipe videos
    public String getVideoId(){return getString(KEY_VIDEO_ID);}


    public static class Query extends ParseQuery<Recipe> {
        public Query() {
            super(Recipe.class);
        }

        public Recipe.Query withIngredients(ArrayList<Ingredient> ingredients){
            whereContainedIn(KEY_INGREDIENTS, ingredients  );
            return this;
        }

        public Query withRecipeID(String ID) {
            whereEqualTo("objectId", ID);
            return this;
        }

        public Recipe.Query withOneIngredient(Ingredient ingredient) {
            ArrayList<Ingredient> tempIngredientArrayList = new ArrayList<Ingredient>();
            tempIngredientArrayList.add(ingredient);
            whereContainedIn(KEY_INGREDIENTS, tempIngredientArrayList);
            return this;
        }

        public Query containsIngredients(){
            include(KEY_CONTAINS_INGREDIENTS);
            return this;

        }
    }
}
