package com.krishibarirangpur.bdhelper.reserve;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityBackUpDataBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BackUpDataActivity extends BaseActivity {

    private ActivityBackUpDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_back_up_data);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        binding.backupRl.setOnClickListener(v -> {
            startBackup();
        });

        binding.reStoreRl.setOnClickListener(v -> {
            startRestore();
        });

    }

    private void startBackup() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Backing up data");
        progressDialog.setMessage("Fetching data from Firestore...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference realtimeDb = FirebaseDatabase.getInstance().getReference("barikoi_cache");

        db.collection("barikoi_cache").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int total = queryDocumentSnapshots.size();
                    if (total == 0) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "No data found in Firestore to backup.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressDialog.setMessage("Starting backup of " + total + " items...");
                    AtomicInteger count = new AtomicInteger(0);

                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> sanitizedData = sanitizeForRealtime(document.getData());

                        realtimeDb.child(document.getId()).setValue(sanitizedData)
                                .addOnCompleteListener(task -> {
                                    int current = count.incrementAndGet();
                                    progressDialog.setMessage("Backing up: " + current + " / " + total);

                                    if (current == total) {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, "Backup completed! " + total + " items synced.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void startRestore() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Restoring data");
        progressDialog.setMessage("Fetching data from Realtime DB...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference realtimeDb = FirebaseDatabase.getInstance().getReference("barikoi_cache");
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();

        realtimeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalCount = snapshot.getChildrenCount();
                if (totalCount == 0) {
                    progressDialog.dismiss();
                    Toast.makeText(BackUpDataActivity.this, "No data found in Realtime DB to restore.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Restoring " + totalCount + " items to Firestore...");
                AtomicInteger processedCount = new AtomicInteger(0);
                
                WriteBatch batch = firestoreDb.batch();
                int batchCounter = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String docId = child.getKey();
                    Object rawData = child.getValue();
                    
                    if (docId != null && rawData instanceof Map) {
                        // Applying schema and restoring nulls
                        Map<String, Object> restoredData = applySchemaAndRestoreNulls((Map<String, Object>) rawData);
                        batch.set(firestoreDb.collection("barikoi_cache").document(docId), restoredData);
                        batchCounter++;
                    }

                    if (batchCounter >= 100) {
                        commitBatch(batch, progressDialog, processedCount, (int) totalCount);
                        batch = firestoreDb.batch();
                        batchCounter = 0;
                    }
                }

                if (batchCounter > 0) {
                    commitBatch(batch, progressDialog, processedCount, (int) totalCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(BackUpDataActivity.this, "Realtime DB Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void commitBatch(WriteBatch batch, ProgressDialog dialog, AtomicInteger processed, int total) {
        batch.commit().addOnCompleteListener(task -> {
            int current = processed.addAndGet(100);
            if (current > total) current = total;
            dialog.setMessage("Restored: " + current + " / " + total);
            
            if (current >= total) {
                dialog.dismiss();
                Toast.makeText(BackUpDataActivity.this, "Restore completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String, Object> applySchemaAndRestoreNulls(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        
        // Root fields
        result.put("lat", data.get("lat"));
        result.put("lng", data.get("lng"));
        result.put("timestamp", data.get("timestamp"));
        
        // Place object and its schema
        Map<String, Object> place = (Map<String, Object>) data.get("place");
        if (place == null) {
            result.put("place", null);
        } else {
            Map<String, Object> placeResult = new HashMap<>();
            placeResult.put("address", getVal(place, "address"));
            placeResult.put("address_bn", getVal(place, "address_bn"));
            placeResult.put("area", getVal(place, "area"));
            placeResult.put("area_bn", getVal(place, "area_bn"));
            placeResult.put("city", getVal(place, "city"));
            placeResult.put("city_bn", getVal(place, "city_bn"));
            placeResult.put("country", getVal(place, "country"));
            placeResult.put("distance_within_meters", getVal(place, "distance_within_meters"));
            placeResult.put("district", getVal(place, "district"));
            placeResult.put("division", getVal(place, "division"));
            placeResult.put("id", getVal(place, "id"));
            placeResult.put("location_type", getVal(place, "location_type"));
            placeResult.put("pauroshova", getVal(place, "pauroshova"));
            placeResult.put("postCode", getVal(place, "postCode"));
            placeResult.put("sub_district", getVal(place, "sub_district"));
            placeResult.put("union", getVal(place, "union"));

            // address_components
            Map<String, Object> addrComp = (Map<String, Object>) place.get("address_components");
            Map<String, Object> addrCompResult = new HashMap<>();
            if (addrComp != null) {
                addrCompResult.put("house", getVal(addrComp, "house"));
                addrCompResult.put("place_name", getVal(addrComp, "place_name"));
                addrCompResult.put("road", getVal(addrComp, "road"));
            } else {
                addrCompResult.put("house", null);
                addrCompResult.put("place_name", null);
                addrCompResult.put("road", null);
            }
            placeResult.put("address_components", addrCompResult);

            // area_components
            Map<String, Object> areaComp = (Map<String, Object>) place.get("area_components");
            Map<String, Object> areaCompResult = new HashMap<>();
            if (areaComp != null) {
                areaCompResult.put("area", getVal(areaComp, "area"));
                areaCompResult.put("sub_area", getVal(areaComp, "sub_area"));
            } else {
                areaCompResult.put("area", null);
                areaCompResult.put("sub_area", null);
            }
            placeResult.put("area_components", areaCompResult);
            
            result.put("place", placeResult);
        }
        
        return result;
    }

    // Helper to return null if value is missing or empty string
    private Object getVal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null || "".equals(val)) return null;
        return val;
    }

    private Map<String, Object> sanitizeForRealtime(Map<String, Object> map) {
        if (map == null) return new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                result.put(entry.getKey(), "");
            } else if (value instanceof Map) {
                result.put(entry.getKey(), sanitizeForRealtime((Map<String, Object>) value));
            } else {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }
}
