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

public class ShoppingCartActivity extends AppCompatActivity {

    private RecyclerView shoppingCartRecyclerView;
    private ShoppingListAdapter shoppingCartAdapter;
    private ArrayList<HashMap<String, String>> shoppingBasket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        shoppingBasket = ShoppingListActivity.getShoppingBasket();

        shoppingCartRecyclerView = findViewById(R.id.shoppingCartRecyclerView);
        shoppingCartAdapter = new ShoppingListAdapter(shoppingBasket, new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onEdit(int position) {
                showEditItemDialog(position);
            }

            @Override
            public void onDelete(int position) {
                deleteItemFromCart(position);
            }

            @Override
            public void onMarkAsPurchased(int position) {
                Toast.makeText(ShoppingCartActivity.this, "Item is already in the cart.", Toast.LENGTH_SHORT).show();
            }
        });

        shoppingCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingCartRecyclerView.setAdapter(shoppingCartAdapter);
    }

    private void showEditItemDialog(int position) {
        HashMap<String, String> currentItem = shoppingBasket.get(position);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);
        EditText itemPriceEditText = dialogView.findViewById(R.id.itemPriceEditText);

        itemQuantityEditText.setText(currentItem.get("quantity"));
        itemPriceEditText.setText(currentItem.get("price"));

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

                    currentItem.put("quantity", updatedQuantity);
                    currentItem.put("price", updatedPrice.isEmpty() ? "0.00" : updatedPrice);

                    shoppingCartAdapter.notifyItemChanged(position);
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItemFromCart(int position) {
        shoppingBasket.remove(position);
        shoppingCartAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }
}
