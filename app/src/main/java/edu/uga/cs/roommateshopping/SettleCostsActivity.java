package edu.uga.cs.roommateshopping;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SettleCostsActivity extends AppCompatActivity {

    private TextView totalPurchasesValue;
    private TextView moneySpentValue;
    private TextView averageSpentValue;
    private TextView differencesValue;
    private Button clearPurchasesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_costs);

        totalPurchasesValue = findViewById(R.id.totalPurchasesValue);
        moneySpentValue = findViewById(R.id.moneySpentValue);
        averageSpentValue = findViewById(R.id.averageSpentValue);
        differencesValue = findViewById(R.id.differencesValue);
        clearPurchasesButton = findViewById(R.id.clearPurchasesButton);

        calculateAndDisplayCosts();

        clearPurchasesButton.setOnClickListener(v -> {
            clearAllPurchases();
            saveSettledCosts();
        });
    }

    private void calculateAndDisplayCosts() {
        DatabaseReference purchasesRef = FirebaseDatabase.getInstance().getReference("Purchases");

        purchasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(SettleCostsActivity.this, "No purchases found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                float total = 0f;
                Map<String, Float> roommateSpending = new HashMap<>();
                int roommateCount;

                // Iterate over all purchases
                for (DataSnapshot purchaseSnapshot : snapshot.getChildren()) {
                    PurchaseList purchase = purchaseSnapshot.getValue(PurchaseList.class);
                    if (purchase != null && purchase.getPurchaseList() != null) {
                        total += purchase.getTotal(); // Add to total purchases
                        String roommate = purchase.getPurchasedBy();

                        // Update spending for each roommate
                        roommateSpending.put(roommate, roommateSpending.getOrDefault(roommate, 0f) + purchase.getTotal());
                    }
                }

                // Calculate the number of roommates and the average spending
                roommateCount = roommateSpending.size();
                float average = total / roommateCount;

                // Display total and average
                totalPurchasesValue.setText(String.format("$%.2f", total));
                averageSpentValue.setText(String.format("$%.2f", average));

                // Display roommate spending and differences
                StringBuilder spendingDetails = new StringBuilder();
                StringBuilder differencesDetails = new StringBuilder();
                for (Map.Entry<String, Float> entry : roommateSpending.entrySet()) {
                    String roommate = entry.getKey();
                    float spent = entry.getValue();
                    float difference = spent - average;

                    spendingDetails.append(String.format("%s: $%.2f\n", roommate, spent));
                    differencesDetails.append(String.format("%s: $%.2f\n", roommate, difference));
                }

                moneySpentValue.setText(spendingDetails.toString());
                differencesValue.setText(differencesDetails.toString());

                // Save the settled costs to Firebase
                saveSettledCostsToFirebase(total, average, spendingDetails.toString(), differencesDetails.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettleCostsActivity.this, "Failed to calculate costs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSettledCostsToFirebase(float total, float average, String roommateSpending, String differences) {
        DatabaseReference settledCostsRef = FirebaseDatabase.getInstance().getReference("SettledCosts");

        Map<String, String> settledData = new HashMap<>();
        settledData.put("total", String.format("$%.2f", total));
        settledData.put("average", String.format("$%.2f", average));
        settledData.put("roommateSpending", roommateSpending);
        settledData.put("differences", differences);

        settledCostsRef.setValue(settledData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SettleCostsActivity.this, "Costs saved to Firebase!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettleCostsActivity.this, "Failed to save costs: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAllPurchases() {
        DatabaseReference purchasesRef = FirebaseDatabase.getInstance().getReference("Purchases");

        purchasesRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "All purchases cleared!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to clear purchases: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSettledCosts() {
        DatabaseReference settledCostsRef = FirebaseDatabase.getInstance().getReference("SettledCosts");

        settledCostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SettleCostsActivity.this, "Settled costs saved and purchases cleared.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettleCostsActivity.this, "No data to save.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettleCostsActivity.this, "Failed to fetch settled costs: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

