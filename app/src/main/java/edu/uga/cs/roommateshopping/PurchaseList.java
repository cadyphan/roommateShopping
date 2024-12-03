package edu.uga.cs.roommateshopping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseList implements Serializable {
    private List<ShoppingItem> purchaseList;
    private String purchasedBy;
    private String name;
    private String key;
    private float total;

    public PurchaseList() {
        this.purchaseList = new ArrayList<>();
        this.purchasedBy = null;
        this.name = null;
        this.key = null;
        this.total = 0;
    }

    public PurchaseList(List<ShoppingItem> shoppingList, String purchasedBy, String name, float total) {
        this.purchaseList = shoppingList;
        this.purchasedBy = purchasedBy;
        this.name = name;
        this.key = null;
        this.total = total;
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

    public float getTotal () {return total;}

    public void setTotal (float total) { this.total = total;}
    
    public int getPurchaseListCount () {return purchaseList.size();}

}
