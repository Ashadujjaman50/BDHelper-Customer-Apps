package com.dropshep.bdhelper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.dropshep.bdhelper.adapter.AdapterNotice;
import com.dropshep.bdhelper.databinding.ActivityNotificationBinding;
import com.dropshep.bdhelper.model.ModelNotice;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends BaseActivity {

    private ActivityNotificationBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    AdapterNotice adapterNotice;
    ArrayList<ModelNotice> noticeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        //init views
        noticeArrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //check Notification timestamp
        checkNotification();

        //get Current User All Notice Load
        loadAllNotice();


    }

    private ListenerRegistration noticeListener;

    private void loadAllNotice() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noticeListener = db.collection("Notice")
                .orderBy("timestamp", Query.Direction.ASCENDING) // 🆕 new → old
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error loading notices", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        noticeArrayList.clear();

                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            ModelNotice notice = ds.toObject(ModelNotice.class);
                            if (notice == null) continue;

                            String receivedUserId = ds.getString("receivedUserId");
                            String senderType = ds.getString("senderType");

                            // ✅ Filter condition
                            if ((receivedUserId != null &&
                                    (receivedUserId.equals(currentUserId) ||
                                            receivedUserId.equals("all") ||
                                            receivedUserId.equals("customer")))
                                    && (senderType != null &&
                                    (senderType.equals("admin") || senderType.equals("partner")))) {

                                noticeArrayList.add(notice);
                            }
                        }

                        // ✅ Update adapter
                        if (noticeArrayList.isEmpty()) {
                            binding.noNoticeTv.setVisibility(View.VISIBLE);
                        } else {
                            binding.noNoticeTv.setVisibility(View.GONE);
                        }

                        if (adapterNotice == null) {
                            adapterNotice = new AdapterNotice(NotificationActivity.this, noticeArrayList);
                            binding.noticeRv.setAdapter(adapterNotice);
                        }
                        else {
                            adapterNotice.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noticeListener != null) {
            noticeListener.remove(); // 🔹 Memory leak বন্ধ হবে
        }
    }


    private void checkNotification() {
        //current time to stamp
        long checkNotice = System.currentTimeMillis();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("NoticeCheck");

        HashMap<String, Object> checkMap = new HashMap<>();
        checkMap.put("checkNotice", checkNotice);

        userRef.child(firebaseUser.getUid()).updateChildren(checkMap);
    }
}