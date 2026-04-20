package com.krishibarirangpur.bdhelper.utils.customer;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class GenerateOrderId {
    /** ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅
     * All Logical operation
     */

    public static void newOrderId(FirebaseFirestore db, String collectionPath,
                                  String fieldPath, String prefix,
                                  int initialDigitLength, OrderIdCallback callback) {

        db.collection(collectionPath)
                .orderBy(fieldPath, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int maxNum = 0;
                    int digitLength = initialDigitLength;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String lastId = doc.getString(fieldPath);
                        if (lastId != null && lastId.startsWith(prefix)) {
                            try {
                                String numPart = lastId.substring(prefix.length());
                                int num = Integer.parseInt(numPart);
                                if (num > maxNum) maxNum = num;

                                // Update digitLength dynamically
                                digitLength = Math.max(numPart.length(), String.valueOf(maxNum + 1).length());

                            } catch (Exception ignored) {}
                        }
                    }

                    // Build new OrderID
                    String pattern = prefix + "%0" + digitLength + "d";
                    String newOrderId = String.format(Locale.ENGLISH, pattern, maxNum + 1);
                    callback.onSuccess(newOrderId);

                })
                .addOnFailureListener(callback::onFailure);
    }


    public interface OrderIdCallback {
        void onSuccess(String orderId);
        void onFailure(Exception e);
    }
}
