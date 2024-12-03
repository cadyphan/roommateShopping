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

    private void moveToList(int position) {
        String shoppingListID = "shoppingList";
        ShoppingItem shoppingItem = purchaseItems.getItems().get(position);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingList")
                .child(shoppingListID);
        DatabaseReference purchaseListRef = FirebaseDatabase.getInstance()
                .getReference("Purchases")
                .child(purchaseListKey)
                .child(shoppingItem.getKey());

        shoppingListRef.push().setValue(shoppingItem)
            .addOnSuccessListener(aVoid -> {
                // Delete the item from the purchase list after adding successfully
                deleteItemFromCart(position);
                Toast.makeText(this, "Item moved to cart", Toast.LENGTH_SHORT).show();
            })
                .addOnFailureListener(e -> {
                    Log.e("PurchaseItemsActivity", "Failed to add item to shopping list: " + e.getMessage());
                    Toast.makeText(this, "Failed to move item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        Toast.makeText(this, "Item moved to cart", Toast.LENGTH_SHORT).show();
    }

    private void deleteItemFromCart(int position) {
        String listID = purchaseListKey;
        purchaseItems.deleteShoppingItem(listID, position, purchaseItemsAdapter, this, purchaseItems);
        purchaseItemsAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }
}
