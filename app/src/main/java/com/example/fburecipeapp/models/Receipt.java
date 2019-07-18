package com.example.fburecipeapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Receipt")
public class Receipt extends ParseObject {
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_ID = "objectId";
    private static final String KEY_DESCRIPTION = "description";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public static class Query extends ParseQuery<Receipt> {
        public Query(){
            super(Receipt.class);
        }


        public Query withUser(){
            include(KEY_USER);
            return this;
        }

        public Query ascending(){
            orderByAscending(KEY_CREATED_AT);
            return this;
        }

        public Query descending(){
            orderByDescending(KEY_CREATED_AT);
            return this;
        }


        public Query forCurrentUser(){
            whereEqualTo(KEY_USER, ParseUser.getCurrentUser());
            return this;
        }

        public Query whereId(String id){
            whereEqualTo(KEY_ID, id);
            return this;
        }
    }
}
