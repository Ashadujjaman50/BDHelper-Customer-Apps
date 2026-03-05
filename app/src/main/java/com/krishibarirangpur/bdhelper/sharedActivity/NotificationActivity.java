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
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotificationActivity extends BaseActivity {

    private ActivityNotificationBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    AdapterNotice adapterNotice;
    ArrayList<ModelNotice> noticeArrayList;

    String user_type;

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

        user_type = getIntent().getStringExtra(MyUtils.USER_TYPE);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        // set adapter
        adapterNotice = new AdapterNotice(this, noticeArrayList);
        binding.noticeRv.setAdapter(adapterNotice);

        //check Notification timestamp
        checkNotification();

        //get Current User All Notice Load
        loadAllNotice();

        //Click Post And Bid Notice to Goto Target Location
        adapterNotice.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position < 0 || position >= noticeArrayList.size()) return;

                ModelNotice notice = noticeArrayList.get(position);

                String noticeType = notice.getNoticeCategory();
                String orderId = notice.getOrderId();

                // 🔹 Partner logic
                if (MyUtils.NOTICE_RECEIVER_PARTNER.equals(user_type)) {

                    if (MyUtils.NOTICE_TYPE_POST.equals(noticeType) ||
                            MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) {

                        getOrderIdToOrderInfo(orderId,
                                MyUtils.NOTICE_RECEIVER_PARTNER,
                                noticeType);
                    }
                    return;
                }

                // 🔹 Customer logic
                if (MyUtils.NOTICE_RECEIVER_CUSTOMER.equals(user_type) &&
                        MyUtils.NOTICE_TYPE_BID.equals(noticeType)) {

                    getOrderIdToOrderInfo(orderId,
                            MyUtils.NOTICE_RECEIVER_CUSTOMER,
                            MyUtils.NOTICE_TYPE_BID);
                }
            }


            @Override
            public void onShowItemClick(int position) {}

            @Override
            public void onDeleteItemClick(int position) {}
        });

        //Clear Notice In Individual User
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT ) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public int getSwipeDirs(@NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder) {

                        int position = viewHolder.getAdapterPosition();
                        if (position == RecyclerView.NO_POSITION) return 0;

                        ModelNotice notice = noticeArrayList.get(position);
                        String noticeType = notice.getNoticeCategory();

                        // 🔹 Partner logic → swipe allow
                        if (MyUtils.NOTICE_RECEIVER_PARTNER.equals(user_type)) {
                            if (MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) {
                                return super.getSwipeDirs(recyclerView, viewHolder);
                            }
                        }

                        // 🔹 Customer logic → swipe allow
                        if (MyUtils.NOTICE_RECEIVER_CUSTOMER.equals(user_type)) {
                            if (MyUtils.NOTICE_TYPE_BID.equals(noticeType)) {
                                return super.getSwipeDirs(recyclerView, viewHolder);
                            }
                        }

                        // ❌ Otherwise swipe disable
                        return 0;
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

                        // 🔹 UI থেকে আগে remove
                        noticeArrayList.remove(position);
                        adapterNotice.notifyItemRemoved(position);

                        // 🔹 Firestore থেকে delete
                        FirebaseFirestore.getInstance()
                                .collection("Notice")
                                .document(noticeId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    MyToast.showShort(
                                            NotificationActivity.this,
                                            noticeId
                                    );
                                    Log.e("NoticeDelete", "Delete Success");
                                })
                                .addOnFailureListener(e -> {

                                    // ❌ failure হলে item আবার ফিরিয়ে দাও
                                    noticeArrayList.add(position, notice);
                                    adapterNotice.notifyItemInserted(position);

                                    MyToast.showShort(
                                            NotificationActivity.this,
                                            noticeId
                                    );

                                    Log.e("NoticeDelete", "Delete failed", e);
                                });
                    }


                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {

                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(NotificationActivity.this, R.color.gray_light))
                                .addSwipeLeftActionIcon(R.drawable.ic_clear)
                                .addSwipeLeftLabel("Clear")
                                .setSwipeLeftLabelColor(ContextCompat.getColor(NotificationActivity.this, R.color.text_primary))
                                .create()
                                .decorate();

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                                actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.noticeRv);


    }

    private ListenerRegistration noticeListener;

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllNotice() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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


    //Goto Order And Bid Activity
    private void getOrderIdToOrderInfo(String orderId, String user_type, String noticeType) {

        if (orderId == null || orderId.isEmpty()) {
            MyToast.showShort(this, "Invalid Order ID");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("orders")
                .document(orderId) // ⚠️ ধরে নিচ্ছি orderId = documentId
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        MyToast.showShort(this, "Order not found");
                        return;
                    }

                    OrderModel orderModel = documentSnapshot.toObject(OrderModel.class);
                    if (orderModel == null || orderModel.getOrderInfo() == null) {
                        MyToast.showShort(this, "Order info missing");
                        return;
                    }

                    String categoryId = orderModel.getOrderInfo().getCategoryId();
                    String subCategoryId = orderModel.getOrderInfo().getSubCategoryId();

                    Intent intent = new Intent(this, BidActivity.class);
                    intent.putExtra(MyUtils.USER_TYPE, user_type);

                    if (MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) {
                        intent.putExtra(MyUtils.bidAction, "confirmed");
                    }
                    else {
                        intent.putExtra(MyUtils.bidAction, "new");
                        intent.putExtra(MyUtils.orderId, orderId);
                        intent.putExtra(MyUtils.categoryId, categoryId);
                        intent.putExtra(MyUtils.subCategoryId, subCategoryId);
                    }

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                })
                .addOnFailureListener(e -> {
                    MyToast.showShort(this, "Failed to load order");
                    Log.e("OrderLoadError", e.getMessage());
                });
    }



}