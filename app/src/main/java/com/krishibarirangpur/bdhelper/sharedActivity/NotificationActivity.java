package com.krishibarirangpur.bdhelper.sharedActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.shared.AdapterNotice;
import com.krishibarirangpur.bdhelper.databinding.ActivityNotificationBinding;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotificationActivity extends BaseActivity {

    private ActivityNotificationBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private AdapterNotice adapterNotice;
    private ArrayList<ModelNotice> noticeArrayList;
    private ListenerRegistration noticeListener;
    private String user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        initViews();
        setupClickListeners();
        setupSwipeToDelete();
        checkNotification();
        loadAllNotice();
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        noticeArrayList = new ArrayList<>();
        user_type = getIntent().getStringExtra(MyUtils.USER_TYPE);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        adapterNotice = new AdapterNotice(this, noticeArrayList);
        binding.noticeRv.setAdapter(adapterNotice);
    }

    private void setupClickListeners() {
        adapterNotice.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < 0 || position >= noticeArrayList.size()) return;

                ModelNotice notice = noticeArrayList.get(position);
                String noticeType = notice.getNoticeCategory();
                String orderId = notice.getOrderId();

                if (MyUtils.NOTICE_RECEIVER_PARTNER.equals(user_type)) {
                    if (MyUtils.NOTICE_TYPE_POST.equals(noticeType) || MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) {
                        getOrderIdToOrderInfo(orderId, MyUtils.NOTICE_RECEIVER_PARTNER, noticeType);
                    }
                } else if (MyUtils.NOTICE_RECEIVER_CUSTOMER.equals(user_type) && MyUtils.NOTICE_TYPE_BID.equals(noticeType)) {
                    getOrderIdToOrderInfo(orderId, MyUtils.NOTICE_RECEIVER_CUSTOMER, noticeType);
                }
            }

            @Override public void onShowItemClick(int position) {}
            @Override public void onDeleteItemClick(int position) {}
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return 0;

                ModelNotice notice = noticeArrayList.get(position);
                String noticeType = notice.getNoticeCategory();

                boolean canSwipe = (MyUtils.NOTICE_RECEIVER_PARTNER.equals(user_type) && MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) ||
                        (MyUtils.NOTICE_RECEIVER_CUSTOMER.equals(user_type) && MyUtils.NOTICE_TYPE_BID.equals(noticeType));

                return canSwipe ? super.getSwipeDirs(recyclerView, viewHolder) : 0;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                ModelNotice notice = noticeArrayList.get(position);
                String noticeId = notice.getTimestamp();

                if (noticeId == null) {
                    adapterNotice.notifyItemChanged(position);
                    return;
                }

                // UI Update
                noticeArrayList.remove(position);
                adapterNotice.notifyItemRemoved(position);

                // DB Delete
                db.collection("Notice").document(noticeId).delete()
                        .addOnSuccessListener(unused -> MyToast.showShort(NotificationActivity.this, "Clear"))
                        .addOnFailureListener(e -> {
                            noticeArrayList.add(position, notice);
                            adapterNotice.notifyItemInserted(position);
                            MyToast.showShort(NotificationActivity.this, "Failed");
                        });
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(NotificationActivity.this, R.color.gray_light))
                        .addSwipeLeftActionIcon(R.drawable.ic_clear)
                        .addSwipeLeftLabel("Clear")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(NotificationActivity.this, R.color.text_primary))
                        .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.noticeRv);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllNotice() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noticeListener = db.collection("Notice")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {

                    if (e != null) {
                        Log.e("FirestoreError", "Error loading notices", e);
                        return;
                    }

                    if (snapshots == null) return;

                    noticeArrayList.clear();

                    for (DocumentSnapshot ds : snapshots) {

                        ModelNotice notice = ds.toObject(ModelNotice.class);
                        if (notice == null) continue;

                        String receivedUserId = ds.getString("receivedUserId");
                        String senderType = ds.getString("senderType");

                        if (receivedUserId == null || senderType == null) continue;

                        // ❌ Skip if senderType == receivedUserId
                        if (senderType.equals(receivedUserId)) continue;

                        boolean isValidReceiver =
                                receivedUserId.equals(currentUserId) ||
                                        receivedUserId.equals(MyUtils.NOTICE_RECEIVER_ALL) ||
                                        receivedUserId.equals(user_type);

                        if (isValidReceiver) {
                            noticeArrayList.add(notice);
                        }
                    }

                    // UI update
                    binding.noNoticeTv.setVisibility(
                            noticeArrayList.isEmpty() ? View.VISIBLE : View.GONE
                    );

                    adapterNotice.notifyDataSetChanged();
                });
    }

    /*private void loadAllNotice() {
        if (firebaseUser == null) return;
        String currentUserId = firebaseUser.getUid();

        // 🔹 Optimized Query: fetch only relevant notifications
        noticeListener = db.collection("Notice")
                .whereIn("receivedUserId", Arrays.asList(currentUserId, MyUtils.NOTICE_RECEIVER_ALL, user_type))
                .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first is better for UX
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error loading notices", e);
                        return;
                    }
                    if (snapshots == null) return;

                    noticeArrayList.clear();
                    for (DocumentSnapshot ds : snapshots) {
                        ModelNotice notice = ds.toObject(ModelNotice.class);
                        if (notice != null) {
                            // Skip if I am the sender and receiver (redundant check if UI filters correctly)
                            if (!notice.getSenderType().equals(notice.getReceivedUserId())) {
                                noticeArrayList.add(notice);
                            }
                        }
                    }

                    binding.noNoticeTv.setVisibility(noticeArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                    adapterNotice.notifyDataSetChanged();
                });
    }*/

    private void checkNotification() {
        if (firebaseUser == null) return;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("NoticeCheck");
        userRef.child(firebaseUser.getUid()).child("checkNotice").setValue(System.currentTimeMillis());
    }

    private void getOrderIdToOrderInfo(String orderId, String userType, String noticeType) {
        if (orderId == null || orderId.isEmpty()) {
            MyToast.showShort(this, "Invalid Order ID");
            return;
        }

        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        MyToast.showShort(this, "Order not found");
                        return;
                    }

                    OrderModel orderModel = documentSnapshot.toObject(OrderModel.class);
                    if (orderModel == null || orderModel.getOrderInfo() == null) return;

                    Intent intent = new Intent(this, BidActivity.class);
                    intent.putExtra(MyUtils.USER_TYPE, userType);

                    if (MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) {
                        intent.putExtra(MyUtils.bidAction, "confirmed");
                    } else {
                        intent.putExtra(MyUtils.bidAction, "new");
                        intent.putExtra(MyUtils.orderId, orderId);
                        intent.putExtra(MyUtils.categoryId, orderModel.getOrderInfo().getCategoryId());
                        intent.putExtra(MyUtils.subCategoryId, orderModel.getOrderInfo().getSubCategoryId());
                    }

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                })
                .addOnFailureListener(e -> MyToast.showShort(this, "Failed to load order"));
    }

    @Override
    protected void onDestroy() {
        if (noticeListener != null) noticeListener.remove();
        super.onDestroy();
    }
}