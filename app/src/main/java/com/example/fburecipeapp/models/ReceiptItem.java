package com.example.fburecipeapp.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ReceiptItem")
public class ReceiptItem extends ParseObject {

    private float quantity;
    private String description;
    private String price;
    private static final String TAG = "Item";

    public ReceiptItem(String description, String price) {
        this.description = description;
        this.price = price;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public void print(){
        String item = String.format("%s %s", description, price);
        Log.d(TAG,item);
    }

}
