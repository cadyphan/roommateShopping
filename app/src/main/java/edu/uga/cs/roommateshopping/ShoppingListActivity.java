package edu.uga.cs.roommateshopping;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView shoppingListRecyclerView;
    private RecyclerView shoppingBasketRecyclerView;

    private ShoppingListAdapter shoppingListAdapter;
    private ShoppingListAdapter shoppingBasketAdapter;

    private ArrayList<HashMap<String, String>> shoppingList;
    private ArrayList<HashMap<String, String>> shoppingBasket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        shoppingListRecyclerView = findViewById(R.id.shoppingListRecyclerView);
        shoppingBasketRecyclerView = findViewById(R.id.shoppingBasketRecyclerView);

        // Initialize shopping list and basket
        shoppingList = new ArrayList<>();
        shoppingBasket = new ArrayList<>();

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
                markItemAsPurchased(position);
            }
        });

        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingListRecyclerView.setAdapter(shoppingListAdapter);

        // Set up RecyclerView for shopping basket
        shoppingBasketAdapter = new ShoppingListAdapter(shoppingBasket, new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                Toast.makeText(ShoppingListActivity.this, "Editing items in the basket is not allowed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(int position) {
                deleteItemFromShoppingBasket(position);
            }

            @Override
            public void onMarkAsPurchased(int position) {
                Toast.makeText(ShoppingListActivity.this, "Item is already in the basket.", Toast.LENGTH_SHORT).show();
            }
        });

        shoppingBasketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingBasketRecyclerView.setAdapter(shoppingBasketAdapter);

        findViewById(R.id.addItemButton).setOnClickListener(v -> showAddItemDialog());
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText itemNameEditText = dialogView.findViewById(R.id.itemNameEditText);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Item")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, which) -> {
                    String itemName = itemNameEditText.getText().toString().trim();
                    String itemQuantity = itemQuantityEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(itemName)) {
                        Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isDuplicateItem(itemName, shoppingList)) {
                        Toast.makeText(this, "Item already exists in the shopping list.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HashMap<String, String> newItem = new HashMap<>();
                    newItem.put("name", itemName);
                    newItem.put("quantity", itemQuantity.isEmpty() ? "1" : itemQuantity);

                    shoppingList.add(newItem);
                    shoppingListAdapter.notifyItemInserted(shoppingList.size() - 1);

                    Toast.makeText(this, "Item added to shopping list", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void showEditItemDialog(int position) {
        HashMap<String, String> currentItem = shoppingList.get(position);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText itemNameEditText = dialogView.findViewById(R.id.itemNameEditText);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);

        itemNameEditText.setText(currentItem.get("name"));
        itemQuantityEditText.setText(currentItem.get("quantity"));

        new AlertDialog.Builder(this)
                .setTitle("Edit Item")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedName = itemNameEditText.getText().toString().trim();
                    String updatedQuantity = itemQuantityEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(updatedName)) {
                        Toast.makeText(this, "Item name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!currentItem.get("name").equals(updatedName) && isDuplicateItem(updatedName, shoppingList)) {
                        Toast.makeText(this, "Item already exists in the shopping list.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentItem.put("name", updatedName);
                    currentItem.put("quantity", updatedQuantity.isEmpty() ? "1" : updatedQuantity);

                    shoppingListAdapter.notifyItemChanged(position);
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItemFromShoppingList(int position) {
        shoppingList.remove(position);
        shoppingListAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Item removed from shopping list", Toast.LENGTH_SHORT).show();
    }

    private void markItemAsPurchased(int position) {
        HashMap<String, String> purchasedItem = shoppingList.get(position);

        shoppingBasket.add(purchasedItem);
        shoppingBasketAdapter.notifyItemInserted(shoppingBasket.size() - 1);

        shoppingList.remove(position);
        shoppingListAdapter.notifyItemRemoved(position);

        Toast.makeText(this, "Item moved to shopping basket", Toast.LENGTH_SHORT).show();
    }

    private void deleteItemFromShoppingBasket(int position) {
        HashMap<String, String> removedItem = shoppingBasket.get(position);

        shoppingList.add(removedItem);
        shoppingListAdapter.notifyItemInserted(shoppingList.size() - 1);

        shoppingBasket.remove(position);
        shoppingBasketAdapter.notifyItemRemoved(position);

        Toast.makeText(this, "Item moved back to shopping list", Toast.LENGTH_SHORT).show();
    }

    private boolean isDuplicateItem(String itemName, ArrayList<HashMap<String, String>> list) {
        for (HashMap<String, String> item : list) {
            if (item.get("name").equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }
}

