package edu.uga.cs.roommateshopping;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList implements Serializable {
   private List<ShoppingItem> shoppingList;

   public ShoppingList() {
       this.shoppingList = new ArrayList<>();
   }

   public ShoppingList(List<ShoppingItem> shoppingList) {
       this.shoppingList = shoppingList;
   }

    public void setShoppingList(List<ShoppingItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public List<ShoppingItem> getShoppingList () {return shoppingList;}

    public int getShoppingListCount () {return shoppingList.size();}

    public List<ShoppingItem> getItems() { return shoppingList; }

    public void deleteShoppingItem(String refID, String listId, int position, ShoppingListAdapter adapter, Context context, ShoppingList list) {
        // Get the item to delete

        ShoppingItem itemToDelete = list.getItems().get(position);
        Log.d("DeleteItem", "Adapter: " + adapter);
        Log.d("DeleteItem", "List: " + list.getItems());

        // Firebase reference to the item
        DatabaseReference itemRef = FirebaseDatabase.getInstance()
                .getReference(refID)
                .child(listId)
                .child(itemToDelete.getKey());

        // Remove the item from Firebase
        itemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update local list and adapter
                Log.d("ShoppingList", "Removed Item from database");
           //     list.getItems().remove(position);
                Log.d("ShoppingList", "Removed Item locally");
                adapter.notifyItemRemoved(position);
                Log.d("ShoppingList", "Notified adapter");
                adapter.notifyItemRangeChanged(position, list.getItems().size()); // Ensure UI consistency
                Toast.makeText(context, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("DeleteItem", "Error removing item: " + e.getMessage());
            Toast.makeText(context, "Error removing item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });;
    }
    public void deleteShoppingItem(String listId, int position, PurchaseItemAdapter adapter, Context context, ShoppingList list) {
        // Get the item to delete
        ShoppingItem itemToDelete = list.getItems().get(position);
        Log.d("DeleteItem", "Deleting item with key: " + itemToDelete.getKey());
        Log.d("RefID: ", "RefId: " + listId);
        // Firebase reference to the item
        DatabaseReference itemRef = FirebaseDatabase.getInstance()
                .getReference("Purchases")
                .child(listId)
                .child("purchaseList")
                .child(itemToDelete.getKey());

        Log.d("DeleteItem", "Firebase Reference: " + itemRef.toString());
        Log.d("DeletePath", itemRef.toString());

        // Remove the item from Firebase
        itemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update local list and adapter
             //   list.getItems().remove(position);
                Toast.makeText(context, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
