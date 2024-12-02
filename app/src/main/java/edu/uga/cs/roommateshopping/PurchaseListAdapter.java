package edu.uga.cs.roommateshopping;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseListAdapter extends RecyclerView.Adapter<PurchaseListAdapter.ViewHolder> {

    //  private final ArrayList<HashMap<String, String>> items;
    private final OnItemClickListener listener;
    private final PurchaseList purchaseList;

    public PurchaseListAdapter(PurchaseList purchaseList, OnItemClickListener listener) {
        this.purchaseList = purchaseList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onEdit(int position);
        void onDelete(int position);
        void onMarkAsPurchased(int position);
    }

//    public ShoppingListAdapter(ArrayList<HashMap<String, String>> items, OnItemClickListener listener) {
//        this.items = items;
//        this.listener = listener;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position >= purchaseList.getPurchaseListCount()) {
            Log.e("PurchaseListAdapter", "Invalid position: " + position);
            return;
        }

        // Get the current PurchaseList
      //  PurchaseList purchase = purchaseList.getItems(position);

        // Display the PurchaseList's details
        holder.itemName.setText(purchaseList.getListName());
      //  holder.itemQuantity.setText(String.format("$%.2f", purchase.getTotal()));

        // Set listeners for actions on the PurchaseList
        holder.editButton.setOnClickListener(v -> listener.onEdit(position));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(position));
        holder.moveToCartButton.setOnClickListener(v -> listener.onMarkAsPurchased(position)); // To show list details
    }

    @Override
    public int getItemCount() {
        return purchaseList.getItems() != null ? purchaseList.getItems().size() : 0;
    }

    public void addItem(ShoppingItem newItem) {
        purchaseList.getItems().add(newItem);  // Add to the data source
        notifyItemInserted(purchaseList.getItems().size() - 1);  // Notify adapter about the new item
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
