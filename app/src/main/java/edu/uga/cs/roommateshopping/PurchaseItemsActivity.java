package edu.uga.cs.roommateshopping;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchaseItemsActivity extends AppCompatActivity {
    private RecyclerView purchaseItemsRecyclerView;
    private PurchaseItemAdapter purchaseItemsAdapter;
    private ShoppingList purchaseItems = new ShoppingList();
    private String purchaseListKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_items);
        // Get the purchase list key from the intent
        purchaseListKey = getIntent().getStringExtra("purchaseListKey");
        if (purchaseListKey != null) {
            fetchPurchaseItems(purchaseListKey);
        }
        purchaseItemsRecyclerView = findViewById(R.id.purchaseItemRecyclerView);
        purchaseItemsAdapter = new PurchaseItemAdapter(purchaseItems, new PurchaseItemAdapter.OnItemClickListener() {
            @Override
            public void onMove(int position) {
                moveToList(position);
            }
        });
        purchaseItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        purchaseItemsRecyclerView.setAdapter(purchaseItemsAdapter);
    }

    private void fetchPurchaseItems(String purchaseListKey) {
        Log.d("FetchPurchaseItems", "Fetching");
        DatabaseReference purchaseItemsRef = FirebaseDatabase.getInstance()
                .getReference("Purchases")
                .child(purchaseListKey)
                .child("purchaseList");

        purchaseItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseItems.getItems().clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        purchaseItems.getItems().add(item);
                    }
                }
                purchaseItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PurchaseItemsActivity.this, "Failed to load items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteItemFromCart(int position) {
        if (position < 0 || position >= purchaseItems.getItems().size()) {
            Toast.makeText(this, "Invalid item position", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the item and key for deletion
        ShoppingItem shoppingItem = purchaseItems.getItems().get(position);
        String itemKey = shoppingItem.getKey();

        if (itemKey == null || itemKey.isEmpty()) {
            Toast.makeText(this, "Item key is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase reference to delete the item
        DatabaseReference itemRef = FirebaseDatabase.getInstance()
                .getReference("Purchases")
                .child(purchaseListKey)
                .child("purchaseList")
                .child(String.valueOf(position));

        itemRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove the item locally and notify the adapter
                    Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("PurchaseItemsActivity", "Failed to remove item: " + e.getMessage());
                    Toast.makeText(this, "Failed to remove item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        DatabaseReference itemsAfter = FirebaseDatabase.getInstance()
                .getReference("Purchases")
                .child(purchaseListKey)
                .child("purchaseList");

        itemsAfter.get().addOnCompleteListener(task -> {
            float totalPrice = 0;
            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                try {
                    ShoppingItem item = snapshot.getValue(ShoppingItem.class); // Deserialize snapshot into ShoppingItem

                    float itemPrice = Float.parseFloat(item.getPrice());
                    int quantity = Integer.parseInt(item.getQuantity());
                    totalPrice += (itemPrice * quantity);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Failed to calculate total price. Invalid price format.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            float finalTotalPrice = (float) (totalPrice * 1.07);

            DatabaseReference totalRef = FirebaseDatabase.getInstance()
                    .getReference("Purchases")
                    .child(purchaseListKey)
                    .child("total");
            totalRef.setValue(finalTotalPrice)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Total price updated!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(aVoid -> {
                        Toast.makeText(this, "Failed to update total price.", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void moveToList(int position) {
        if (position < 0 || position >= purchaseItems.getItems().size()) {
            Toast.makeText(this, "Invalid item position", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the item to move
        ShoppingItem shoppingItem = purchaseItems.getItems().get(position);
        String itemKey = shoppingItem.getKey();

        if (itemKey == null || itemKey.isEmpty()) {
            Toast.makeText(this, "Item key is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        String shoppingListID = "shoppingList";

        // Firebase reference for the target list
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingList")
                .child(shoppingListID);

        shoppingListRef.push().setValue(shoppingItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PurchaseItemsActivity:", "shoppinglist pushed and trying to delete");

                    // After successfully adding to the shopping list, delete from the purchase list
                    deleteItemFromCart(position);
                    Toast.makeText(this, "Item moved to shopping list", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("PurchaseItemsActivity", "Failed to add item to shopping list: " + e.getMessage());
                    Toast.makeText(this, "Failed to move item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
