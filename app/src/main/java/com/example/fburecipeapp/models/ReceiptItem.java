package com.example.fburecipeapp.models;

public class ReceiptItem {

    private int quantity;
    private String description;
    private String price;

    public ReceiptItem(String description, String price) {
        this.description = description;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

}
