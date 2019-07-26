package com.example.fburecipeapp.helpers;

import android.app.Application;

import com.example.fburecipeapp.models.FoodType;
import com.example.fburecipeapp.models.Receipt;
import com.example.fburecipeapp.models.Recipe;
import com.example.fburecipeapp.models.Recipes;
import com.example.fburecipeapp.models.User;

import com.example.fburecipeapp.models.Receipt;

import com.example.fburecipeapp.models.FoodType;

import com.example.fburecipeapp.models.TempIngredients;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        ParseObject.registerSubclass(FoodType.class);
        ParseObject.registerSubclass(Receipt.class);
        ParseUser.registerSubclass(User.class);
        ParseUser.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(TempIngredients.class);
        ParseObject.registerSubclass(Recipes.class);


        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fbu-mainapp") // should correspond to APP_ID env variable
                .clientKey("expelliarmus404")  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("https://fbu-mainapp.herokuapp.com/parse").build());
    }
}
