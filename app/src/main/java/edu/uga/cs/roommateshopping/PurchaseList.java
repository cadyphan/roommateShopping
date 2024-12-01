package edu.uga.cs.roommateshopping;

import java.util.List;

public class PurchaseList {
    private List<ShoppingItem> purchaseList;
    private String purchasedBy;
    private String name;
    private String key;
    public PurchaseList() {
        this.purchaseList = null;
        this.purchasedBy = null;
        this.name = null;
        this.key = null;
    }

    public PurchaseList(List<ShoppingItem> shoppingList, String purchasedBy, String name) {
        this.purchaseList = shoppingList;
        this.purchasedBy = purchasedBy;
        this.name = name;
        this.key = null;
    }

    public void setPurchaseList(List<ShoppingItem> shoppingList) {
        this.purchaseList = shoppingList;
    }

    public List<ShoppingItem> getPurchaseList () {return purchaseList;}

    public void setPurchasedBy(String purchasedBy) {
        this.purchasedBy = purchasedBy;
    }

    public String getPurchasedBy () {return purchasedBy;}

    public String getListName () {return name;}

    public void setListName (String name) { this.name = name;}

    public String getKey () {return key;}

    public void setKey (String key) { this.key = key;}


}
