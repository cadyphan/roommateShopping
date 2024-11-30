package edu.uga.cs.roommateshopping;

import java.io.Serializable;

public class ShoppingItem implements Serializable {
    private String item;
    private String quantity;
    private String price;
    private String key;

    public ShoppingItem() {
        this.item = null;
        this.quantity = null;
        this.price = null;
        this.key = null;
    }

    public ShoppingItem(String item, String quantity, String price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.key = null;
    }

    public String getItem() { return item; }

    public void setItem(String item) {this.item = item;}

    public String getKey() { return key; }

    public void setKey(String key) {this.key = key;}

    public String getQuantity() { return quantity; }

    public void setQuantity(String quantity) {this.quantity = quantity;}

    public String getPrice() { return price; }

    public void setPrice(String price) {this.price = price;}
}