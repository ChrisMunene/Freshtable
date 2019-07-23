package com.example.fburecipeapp.models;

import android.util.Log;

import org.parceler.Parcel;

@Parcel
public class ReceiptItem {

    private float quantity;
    private String description;
    private String price;
    private static final String TAG = "Item";

    public ReceiptItem(){

    }

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
