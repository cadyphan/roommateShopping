package edu.uga.cs.roommateshopping;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseItemAdapter extends RecyclerView.Adapter<PurchaseItemAdapter.ViewHolder> {
    private final ShoppingList purchaseItems;
    private final OnItemClickListener listener;

    public PurchaseItemAdapter(ShoppingList purchaseItems, OnItemClickListener listener) {
        this.purchaseItems = purchaseItems;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onMove(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem item = purchaseItems.getItems().get(position);
        Log.d("Bind item", item.getQuantity());
        holder.itemName.setText(item.getItem());
        holder.itemQuantity.setText(item.getQuantity());
        holder.itemMove.setOnClickListener(v -> listener.onMove(position));

      //  holder.itemMove.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return purchaseItems.getShoppingListCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemQuantity;
        private ImageButton itemMove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.purchaseItemName); // Adjust to your layout
            itemQuantity = itemView.findViewById(R.id.purchaseItemQuantity);
            itemMove = itemView.findViewById(R.id.purchaseItemMove);
        }
    }
}
