package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class PurchaseListActivity extends AppCompatActivity {
    private RecyclerView purchaseListRecyclerView;
    private PurchaseListAdapter purchaseListAdapter;
    private List<PurchaseList> purchaseLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PursedActivity", "Creating Purchase Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        purchaseLists = new ArrayList<>();
        fetchPurchaseList();
        purchaseListRecyclerView = findViewById(R.id.purchaseListRecycler);
        purchaseListAdapter = new PurchaseListAdapter(purchaseLists, new PurchaseListAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                showEditItemDialog(position);
            }

            @Override
            public void onItemClick(PurchaseList purchaseList) {
                navigateToPurchaseItems(purchaseList);
            }
        });

        purchaseListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        purchaseListRecyclerView.setAdapter(purchaseListAdapter);
    }


    private void showEditItemDialog(int position) {
        PurchaseList currentPurchaseList = purchaseLists.get(position);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_price, null);
        EditText totalPriceEditText = dialogView.findViewById(R.id.editPriceEditText);

        totalPriceEditText.setText(String.format("%.2f", currentPurchaseList.getTotal()));
     //   float currentPrice = currentItem.getTotal();

        new AlertDialog.Builder(this)
                .setTitle("Edit Total Price")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedPriceStr = totalPriceEditText.getText().toString().trim();

                    if (!updatedPriceStr.isEmpty()) {
                        try {
                            // Parse the updated price and update the PurchaseList
                            float updatedPrice = Float.parseFloat(updatedPriceStr);
                            currentPurchaseList.setTotal(updatedPrice);

                            // Update the database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference purchaseRef = database.getReference("Purchases")
                                    .child(currentPurchaseList.getKey()); // Use the unique key of the PurchaseList

                            purchaseRef.setValue(currentPurchaseList).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Notify the adapter about the change
                                    purchaseListAdapter.notifyItemChanged(position);
                                    Toast.makeText(this, "Total price updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to update total price: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Price cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

//    private void moveToList(int position) {
//        String shoppingListID = "shoppingList";
//        String basketListID = "basketList";
//        ShoppingItem shoppingItem = shoppingBasket.getItems().get(position);
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
//                .getReference("ShoppingList")
//                .child(shoppingListID);
//        DatabaseReference shoppingBasketRef = FirebaseDatabase.getInstance()
//                .getReference("ShoppingBasket")
//                .child(basketListID)
//                .child(shoppingItem.getKey());
//
//        shoppingListRef.push().setValue(shoppingItem);
//        //  shoppingBasketRef.push().setValue(shoppingItem);
//        deleteItemFromCart(position);
//        //      shoppingCartAdapter.notifyItemRemoved(position);
//        Toast.makeText(this, "Item moved to cart", Toast.LENGTH_SHORT).show();
//    }
//
//    private void deleteItemFromCart(int position) {
//        String listID = "basketList";
//        String refID = "ShoppingBasket";
//        shoppingBasket.deleteShoppingItem(refID, listID, position, shoppingCartAdapter, this, shoppingBasket);
//        shoppingCartAdapter.notifyItemRemoved(position);
//        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
//    }

    private void fetchPurchaseList() {
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance().getReference("Purchases");

        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            purchaseLists.clear();
            for (DataSnapshot purchaseSnapshot : snapshot.getChildren()) {
                    PurchaseList purchaseList = purchaseSnapshot.getValue(PurchaseList.class);
                    if (purchaseList != null) {
                        purchaseList.setKey(purchaseSnapshot.getKey()); // Set the Firebase key
                        Log.d("PurchaseList", "Loaded: " + purchaseList.getListName() + " | Key: " + purchaseList.getKey());
                        purchaseLists.add(purchaseList); // Add to the local list
                    }
                }
                Log.d("PurchaseListSize", "Size: " + purchaseLists.size());
                purchaseListAdapter.notifyDataSetChanged(); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PurchaseListActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToPurchaseItems(PurchaseList selectedPurchaseList) {
        Intent intent = new Intent(PurchaseListActivity.this, PurchaseItemsActivity.class);
        intent.putExtra("purchaseListKey", selectedPurchaseList.getKey()); // Pass the key of the selected list
        startActivity(intent);
    }
}
