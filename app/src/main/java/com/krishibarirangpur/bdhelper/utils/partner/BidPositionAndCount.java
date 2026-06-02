package com.krishibarirangpur.bdhelper.utils.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.utils.Replacement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BidPositionAndCount {

    /**
     * Interface to handle the result.
     */
    public interface BidStatsCallback {
        void onResult(int position, int totalBids);
        void onError(Exception e);
    }

    /**
     * Calculates the position and total count of bids for a specific order in real-time.
     * Returns a ListenerRegistration so the caller can stop listening when necessary.
     */
    public static ListenerRegistration getBidStatsRealtime(String orderId, String bidId, BidStatsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value != null) {
                        List<BidModel> bids = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            BidModel bid = document.toObject(BidModel.class);
                            if (bid != null) {
                                bids.add(bid);
                            }
                        }

                        int totalBids = bids.size();
                        if (totalBids == 0) {
                            callback.onResult(0, 0);
                            return;
                        }

                        // গাণিতিক সর্টিং (Ascending)
                        Collections.sort(bids, (b1, b2) -> {
                            try {
                                double amt1 = Double.parseDouble(b1.getBidInfo().getBidAmount());
                                double amt2 = Double.parseDouble(b2.getBidInfo().getBidAmount());
                                return Double.compare(amt1, amt2);
                            } catch (Exception e) {
                                return 0;
                            }
                        });

                        // আপনার বিডের পজিশন বের করা
                        int position = -1;
                        for (int i = 0; i < bids.size(); i++) {
                            if (bids.get(i).getBidInfo().getBidId().equals(bidId)) {
                                position = i + 1; // পজিশন ১ থেকে শুরু
                                break;
                            }
                        }

                        callback.onResult(position, totalBids);
                    }
                });
    }

    /**
     * Binds the bid stats to the UI components and handles localization.
     */
    public static ListenerRegistration bindBidStatsToUI(Context context, String orderId, String bidId, TextView tvTotalBids, TextView tvRank) {
        return getBidStatsRealtime(orderId, bidId, new BidStatsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResult(int position, int totalBids) {
                if (context != null) {
                    tvTotalBids.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(totalBids)) + " টি");
                    tvRank.setText("# " + Replacement.ReplacementNumberInLocal(context, String.valueOf(position)));
                }
            }

            @Override
            public void onError(Exception e) {
                // Silently handle error or log it
            }
        });
    }
    
}
