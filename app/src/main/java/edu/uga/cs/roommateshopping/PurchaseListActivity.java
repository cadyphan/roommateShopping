package edu.uga.cs.roommateshopping;

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

public class PurchaseListActivity extends AppCompatActivity {
    private RecyclerView purchaseListRecyclerView;
    private ShoppingListAdapter purchaseListAdapter;
    private ShoppingList purchaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        purchaseList = (ShoppingList) getIntent().getSerializableExtra("purchaseList");
        fetchBasketList();
        purchaseListRecyclerView = findViewById(R.id.shoppingCartRecyclerView);
        purchaseListAdapter = new ShoppingListAdapter(purchaseList, new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                showEditItemDialog(position);
            }

            @Override
            public void onDelete(int position) {
            //    deleteItemFromCart(position);
            }

            @Override
            public void onMarkAsPurchased(int position) {
                Toast.makeText(PurchaseListActivity.this, "Item is already in the cart.", Toast.LENGTH_SHORT).show();
            }
        });

        purchaseListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        purchaseListRecyclerView.setAdapter(purchaseListAdapter);
    }

    private void showEditItemDialog(int position) {
        ShoppingItem currentItem = purchaseList.getItems().get(position);

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
                    currentItem.setPrice(updatedPrice.isEmpty() ? "0.00" : updatedPrice);

                    purchaseListAdapter.notifyItemChanged(position);
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
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

    private void fetchBasketList() {
        DatabaseReference shoppingListRef = FirebaseDatabase.getInstance()
                .getReference("ShoppingBasket").child("basketList");

        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchaseList.getItems().clear(); // Clear the current list
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        item.setKey(itemSnapshot.getKey());
                        Log.d("keys: ", item.getKey()); // Set the Firebase key
                        purchaseList.getItems().add(item);
                    }
                }
                purchaseListAdapter.notifyDataSetChanged(); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PurchaseListActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
