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
        Log.d("DeleteItem", "Deleting item with key: " + itemToDelete.getKey());

        // Firebase reference to the item
        DatabaseReference itemRef = FirebaseDatabase.getInstance()
                .getReference(refID)
                .child(listId)
                .child(itemToDelete.getKey());

        Log.d("DeleteItem", "Firebase Reference: " + itemRef.toString());

        // Remove the item from Firebase
        itemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update local list and adapter
                list.getItems().remove(position);
         //       Log.d("Item removed: ", removed);
       //         adapter.notifyItemRemoved(position);
        //        adapter.notifyItemRangeChanged(position, list.getItems().size()-1);
                Toast.makeText(context, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
