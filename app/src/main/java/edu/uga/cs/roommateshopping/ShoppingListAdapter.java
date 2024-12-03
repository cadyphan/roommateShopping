package edu.uga.cs.roommateshopping;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

  //  private final ArrayList<HashMap<String, String>> items;
    private final OnItemClickListener listener;
    private final ShoppingList shoppingList;

    public ShoppingListAdapter(ShoppingList shoppingList, OnItemClickListener listener) {
        this.shoppingList = shoppingList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onEdit(int position);
        void onDelete(int position);
        void onMarkAsPurchased(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    //    HashMap<String, String> item = items.get(position);
        if (position < 0 || position >= shoppingList.getItems().size()) {
            Log.e("ShoppingListAdapter", "Invalid position: " + position);
            return;
        }
        ShoppingItem item = shoppingList.getItems().get(position);
        holder.itemName.setText(item.getItem());
        holder.itemQuantity.setText(item.getQuantity());

        holder.editButton.setOnClickListener(v -> listener.onEdit(position));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(position));
        holder.moveToCartButton.setOnClickListener(v -> listener.onMarkAsPurchased(position));
    }

    @Override
    public int getItemCount() {
        return shoppingList.getItems() != null ? shoppingList.getItems().size() : 0;
    }

    public void addItem(ShoppingItem newItem) {
        shoppingList.getItems().add(newItem);  // Add to the data source
        notifyItemInserted(shoppingList.getItems().size() - 1);  // Notify adapter about the new item
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemQuantity;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageButton moveToCartButton;

        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.itemName);
            itemQuantity = view.findViewById(R.id.itemQuantity);
            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);
            moveToCartButton = view.findViewById(R.id.moveToCartButton);
        }
    }
}
