package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

    private ShoppingListAdapter shoppingListAdapter;
    private ArrayList<HashMap<String, String>> shoppingList;
    private static ArrayList<HashMap<String, String>> shoppingBasket; // Shared with ShoppingCartActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        shoppingListRecyclerView = findViewById(R.id.shoppingListRecyclerView);

        // Initialize shopping list and basket
        shoppingList = new ArrayList<>();
        if (shoppingBasket == null) {
            shoppingBasket = new ArrayList<>();
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu); // Inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            // Navigate to ShoppingCartActivity
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                    HashMap<String, String> newItem = new HashMap<>();
                    newItem.put("name", itemName);
                    newItem.put("quantity", itemQuantity.isEmpty() ? "1" : itemQuantity);
                    newItem.put("price", ""); // Default price

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

    private void moveToCart(int position) {
        HashMap<String, String> item = shoppingList.get(position);
        shoppingBasket.add(item);
        shoppingList.remove(position);
        shoppingListAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Item moved to cart", Toast.LENGTH_SHORT).show();
    }

    public static ArrayList<HashMap<String, String>> getShoppingBasket() {
        return shoppingBasket;
    }
}

