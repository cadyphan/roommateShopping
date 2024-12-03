package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView shoppingListRecyclerView;

    private ShoppingListAdapter shoppingListAdapter;
   // private ArrayList<HashMap<String, String>> shoppingList;
 //   private static ArrayList<HashMap<String, String>> shoppingBasket; // Shared with ShoppingCartActivity
    private ShoppingList shoppingList;
    private ShoppingList shoppingBasket;
    private ShoppingList purchaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        shoppingListRecyclerView = findViewById(R.id.shoppingListRecyclerView);

     //    Initialize shopping list and basket
        if (shoppingList == null) {
            shoppingList = new ShoppingList();
        }
        if (shoppingBasket == null) {
            shoppingBasket = new ShoppingList();
        }
//        if (purchaseList == null) {
//            purchaseList = new ShoppingList();
//        }
        fetchShoppingList();
        // Set up RecyclerView for shopping list
        shoppingListAdapter = new ShoppingListAdapter(shoppingList, new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                showEditItemDialog(position);
            }

            @Override
            public void onDelete(int position) {
                deleteItemFromShoppingList(position);
            }

            @Override
            public void onMarkAsPurchased(int position) {
                moveToCart(position);
            }
        });

        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingListRecyclerView.setAdapter(shoppingListAdapter);

        findViewById(R.id.addItemButton).setOnClickListener(v -> showAddItemDialog());
    }

    private void fetchShoppingList() {
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingList").child("shoppingList");
        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingList.getItems().clear(); // Clear the current list
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        item.setKey(itemSnapshot.getKey());
                        Log.d("Fetch keys: ", item.getKey()); // Set the Firebase key
                        shoppingList.getItems().add(item);
                    }
                }
                shoppingListAdapter.notifyDataSetChanged(); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShoppingListActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu); // Inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("ItemClicker", "Selected an Item");
        if (item.getItemId() == R.id.action_cart) {
            // Navigate to ShoppingCartActivity
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.putExtra("shoppingBasket", shoppingBasket);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.purchase_cart) {
            // Navigate to PurchaseListActivity
            Log.d("Navigation","Pressed purchases");
            Intent intent = new Intent(this, PurchaseListActivity.class);
         //   intent.putExtra("purchaseList", purchaseList);
            startActivity(intent);
            Log.d("Navigation", "Starting activity");
            return true;
        }
        if (item.getItemId() == R.id.settle_costs) {
            Intent intent = new Intent(this, SettleCostsActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.logout) {
            // Navigate to PurchaseListActivity
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class); // Adjust if needed to go to another screen
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Helper function to check if the item already exists in the local list
    private boolean isItemAlreadyInLocalList(ShoppingItem newItem) {
        for (ShoppingItem item : shoppingList.getItems()) {
            if (item.getItem().equals(newItem.getItem()) &&
                    item.getQuantity().equals(newItem.getQuantity()) &&
                    item.getPrice().equals(newItem.getPrice())) {
                return true;
            }
        }
        return false;
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText itemNameEditText = dialogView.findViewById(R.id.itemNameEditText);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);
        EditText itemPriceEditText = dialogView.findViewById(R.id.itemPriceEditText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Item")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, which) -> {
                    String itemName = itemNameEditText.getText().toString().trim();
                    String itemQuantity = itemQuantityEditText.getText().toString().trim();
                    String itemPrice = itemPriceEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(itemName)) {
                        Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    HashMap<String, String> newItem = new HashMap<>();
//                    newItem.put("name", itemName);
//                    newItem.put("quantity", itemQuantity.isEmpty() ? "1" : itemQuantity);
//                    newItem.put("price", ""); // Default price

//                    shoppingList.add(newItem);

                    ShoppingItem newShoppingItem = new ShoppingItem(itemName, itemQuantity, itemPrice.isEmpty() ? "" : itemPrice);
                    // Check if the item already exists locally
                    if (isItemAlreadyInLocalList(newShoppingItem)) {
                        Toast.makeText(this, "Item already exists in the list", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference shoppingListRef = db.getReference("ShoppingList");

                    shoppingListRef.child("shoppingList")
                            .orderByChild("itemName") // Assuming itemName is unique and used for comparison
                            .equalTo(itemName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Item already exists in Firebase
                                        Toast.makeText(getApplicationContext(), "Item already exists in Firebase", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Proceed to add item to Firebase and local list
                                        String key = shoppingListRef.child("shoppingList").push().getKey();
                                        newShoppingItem.setKey(key);

                                        assert key != null;
                                        shoppingListRef.child("shoppingList").child(key).setValue(newShoppingItem)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        // Only update the local list and adapter after Firebase operation
                                                        shoppingList.getItems().add(newShoppingItem);
                                                  //      shoppingListAdapter.notifyItemInserted(shoppingList.getItems().size() - 1);
                                                        Toast.makeText(getApplicationContext(), "Item added to shopping list", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Handle failure
                                                        Toast.makeText(getApplicationContext(), "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle possible error in Firebase query
                                    Toast.makeText(getApplicationContext(), "Failed to check Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void showEditItemDialog(int position) {
        ShoppingItem currentItem = shoppingList.getItems().get(position);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText itemNameEditText = dialogView.findViewById(R.id.itemNameEditText);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);
        EditText itemPriceEditText = dialogView.findViewById(R.id.itemPriceEditText);

        itemNameEditText.setText(currentItem.getItem());
        itemQuantityEditText.setText(currentItem.getQuantity());

        new AlertDialog.Builder(this)
                .setTitle("Edit Item")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedName = itemNameEditText.getText().toString().trim();
                    String updatedQuantity = itemQuantityEditText.getText().toString().trim();
                    String updatedPrice = itemPriceEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(updatedName)) {
                        Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentItem.setItem(updatedName);
                    currentItem.setQuantity(updatedQuantity.isEmpty() ? "1" : updatedQuantity);
                    currentItem.setPrice(updatedPrice.isEmpty() ? "0" : updatedPrice);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference itemRef = database.getReference("ShoppingList")
                            .child("shoppingList")
                            .child(currentItem.getKey());
                    itemRef.setValue(currentItem).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the adapter to reflect the changes
                            shoppingListAdapter.notifyItemChanged(position);
                            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItemFromShoppingList(int position) {
        String listID = "shoppingList";
        String refID = "ShoppingList";
        shoppingList.deleteShoppingItem(refID, listID, position, shoppingListAdapter, this, shoppingList);
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    private void moveToCart(int position) {
        String shoppingListID = "shoppingList";
        String basketListID = "basketList";
        ShoppingItem shoppingItem = shoppingList.getItems().get(position);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingList")
                .child(shoppingListID)
                .child(shoppingItem.getKey());
        DatabaseReference shoppingBasketRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingBasket")
                .child(basketListID);

        shoppingBasketRef.push().setValue(shoppingItem);
        deleteItemFromShoppingList(position);
        Toast.makeText(this, "Item moved to cart", Toast.LENGTH_SHORT).show();
    }
}

