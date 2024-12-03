package edu.uga.cs.roommateshopping;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShoppingCartActivity extends AppCompatActivity {

    private RecyclerView shoppingCartRecyclerView;
    private ShoppingListAdapter shoppingCartAdapter;
    private ShoppingList shoppingBasket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        shoppingBasket = (ShoppingList) getIntent().getSerializableExtra("shoppingBasket");
        fetchBasketList();
        shoppingCartRecyclerView = findViewById(R.id.shoppingCartRecyclerView);
        shoppingCartAdapter = new ShoppingListAdapter(shoppingBasket, new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                showEditItemDialog(position);
            }

            @Override
            public void onDelete(int position) {
                moveToList(position);
            }

            @Override
            public void onMarkAsPurchased(int position) {
                Toast.makeText(ShoppingCartActivity.this, "Item is already in the cart.", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.checkout).setOnClickListener(v -> checkoutCart());

        shoppingCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingCartRecyclerView.setAdapter(shoppingCartAdapter);
    }

    private void showEditItemDialog(int position) {
        ShoppingItem currentItem = shoppingBasket.getItems().get(position);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);
        EditText itemPriceEditText = dialogView.findViewById(R.id.itemPriceEditText);

        itemQuantityEditText.setText(currentItem.getQuantity());
        itemPriceEditText.setText(currentItem.getPrice());

        new AlertDialog.Builder(this)
                .setTitle("Edit Item")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedQuantity = itemQuantityEditText.getText().toString().trim();
                    String updatedPrice = itemPriceEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(updatedQuantity)) {
                        Toast.makeText(this, "Quantity cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentItem.setQuantity(updatedQuantity);
                    currentItem.setPrice(updatedPrice.isEmpty() ? currentItem.getPrice() : updatedPrice);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference itemRef = database.getReference("ShoppingBasket")
                            .child("basketList")
                            .child(currentItem.getKey());
                    itemRef.setValue(currentItem).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the adapter to reflect the changes
                            shoppingCartAdapter.notifyItemChanged(position);
                            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void moveToList(int position) {
        String shoppingListID = "shoppingList";
        String basketListID = "basketList";
        ShoppingItem shoppingItem = shoppingBasket.getItems().get(position);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingList")
                .child(shoppingListID);
        DatabaseReference shoppingBasketRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingBasket")
                .child(basketListID)
                .child(shoppingItem.getKey());


        shoppingListRef.push().setValue(shoppingItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update local list and adapter
                shoppingBasket.getItems().remove(position);
                Toast.makeText(this, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        deleteItemFromCart(position);
        Toast.makeText(this, "Item moved to cart", Toast.LENGTH_SHORT).show();
    }

    private void deleteItemFromCart(int position) {
        String listID = "basketList";
        String refID = "ShoppingBasket";
        shoppingBasket.deleteShoppingItem(refID, listID, position, shoppingCartAdapter, this, shoppingBasket);
        shoppingCartAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    private void fetchBasketList() {
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingBasket").child("basketList");

        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingBasket.getItems().clear(); // Clear the current list
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        item.setKey(itemSnapshot.getKey());
                        Log.d("keys: ", item.getKey()); // Set the Firebase key
                        shoppingBasket.getItems().add(item);
                    }
                }
                shoppingCartAdapter.notifyDataSetChanged(); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShoppingCartActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkoutCart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to checkout.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that all items have a price
        for (ShoppingItem item : shoppingBasket.getItems()) {
            if (item.getPrice() == null || item.getPrice().isEmpty() || Double.parseDouble(item.getPrice()) <= 0) {
                Toast.makeText(this, "All items must have a valid price before checkout.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        DatabaseReference purchasesRef = FirebaseDatabase.getInstance().getReference("Purchases");
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("ShoppingBasket").child("basketList");

        String userEmail = currentUser.getEmail(); // Optional: User's email
        long timestamp = System.currentTimeMillis(); // Timestamp for the purchase

        if (shoppingBasket.getItems().isEmpty()) {
            Toast.makeText(this, "Cart is empty. Nothing to checkout.", Toast.LENGTH_SHORT).show();
            return;
        }

        float totalPrice = 0;
        for (ShoppingItem item : shoppingBasket.getItems()) {
            try {
                float itemPrice = Float.parseFloat(item.getPrice());
                int quantity = Integer.parseInt(item.getQuantity());
                totalPrice += (itemPrice * quantity);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Failed to calculate total price. Invalid price format.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Fetch existing purchases to determine the next purchase number
        float finalTotalPrice = (float) (totalPrice * 1.07);
        purchasesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long purchaseNumber = task.getResult().getChildrenCount() + 1;
                String purchaseName = "Purchase " + purchaseNumber;

                // Create a new PurchaseList object
                PurchaseList purchaseList = new PurchaseList(shoppingBasket.getItems(), userEmail, purchaseName, finalTotalPrice);
                purchaseList.setKey(purchasesRef.push().getKey()); // Assign a unique key

                // Store the purchase in Firebase
                purchasesRef.child(purchaseList.getKey()).setValue(purchaseList)
                        .addOnCompleteListener(purchaseTask -> {
                            if (purchaseTask.isSuccessful()) {
                                // Clear the cart from the database
                                cartRef.removeValue().addOnCompleteListener(removeTask -> {
                                    if (removeTask.isSuccessful()) {
                                        shoppingBasket.getItems().clear();
                                        shoppingCartAdapter.notifyDataSetChanged();
                                        Toast.makeText(this, "Checkout successful!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Failed to clear cart: " + removeTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(this, "Failed to save purchase: " + purchaseTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Failed to fetch purchases: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
