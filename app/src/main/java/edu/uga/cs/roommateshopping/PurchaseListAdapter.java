package edu.uga.cs.roommateshopping;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PurchaseListAdapter extends RecyclerView.Adapter<PurchaseListAdapter.ViewHolder> {

    //  private final ArrayList<HashMap<String, String>> items;
    private final OnItemClickListener listener;
    private final List<PurchaseList> purchaseLists;

    public PurchaseListAdapter(List<PurchaseList> purchaseList, OnItemClickListener listener) {
        this.purchaseLists = purchaseList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onEdit(int position);
        void onItemClick(PurchaseList purchaseList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchase_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseList purchaseList = purchaseLists.get(position);

        // Display the PurchaseList's details
        holder.purchaseName.setText(purchaseList.getListName());
        holder.purchasedBy.setText(purchaseList.getPurchasedBy());
        holder.purchasePrice.setText(String.format("$%.2f", purchaseList.getTotal()));
        holder.purchaseDate.setText(purchaseList.getDate());
        Log.d("View", holder.toString() );

        // Set listeners for actions on the PurchaseList
        holder.editButton.setOnClickListener(v -> listener.onEdit(position));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(purchaseList));
    }

    @Override
   public int getItemCount() {
       return purchaseLists != null ? purchaseLists.size() : 0;
    }

//    public void addItem(ShoppingItem newItem) {
//        purchaseList.getItems().add(newItem);  // Add to he data source
//        notifyItemInserted(purchaseList.getItems().size() - 1);  // Notify adapter about the new item
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView purchaseName;
        public TextView purchasePrice;
        public TextView purchasedBy;
        public TextView purchaseDate;

        public ImageButton editButton;

        public ViewHolder(View view) {
            super(view);
            purchaseName = view.findViewById(R.id.purchaseName);
            purchasePrice = view.findViewById(R.id.purchasePrice);
            editButton = view.findViewById(R.id.purchaseEditItem);
            purchasedBy = view.findViewById(R.id.purchasedBy);
            purchaseDate = view.findViewById(R.id.purchaseDate);
        }
        public void bind(PurchaseList purchaseList, OnItemClickListener listener) {
            purchaseName.setText(purchaseList.getListName());
            itemView.setOnClickListener(v -> listener.onItemClick(purchaseList));
        }
    }
}
