package edu.uga.cs.roommateshopping;

import java.util.List;

public class PurchaseList {
    private List<ShoppingItem> purchaseList;
    private String purchasedBy;

    public PurchaseList() {
        this.purchaseList = null;
        purchasedBy = null;
    }

    public PurchaseList(List<ShoppingItem> shoppingList, String purchasedBy) {
        this.purchaseList = shoppingList;
        this.purchasedBy = purchasedBy;
    }

    public void setPurchaseList(List<ShoppingItem> shoppingList) {
        this.purchaseList = shoppingList;
    }

    public List<ShoppingItem> getPurchaseList () {return purchaseList;}

    public void setPurchasedBy(String purchasedBy) {
        this.purchasedBy = purchasedBy;
    }
    public String getPurchasedBy () {return purchasedBy;}

}
