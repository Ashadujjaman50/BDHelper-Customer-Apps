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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.shared.AdapterNotice;
import com.krishibarirangpur.bdhelper.databinding.ActivityNotificationBinding;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.PreloadingDialog;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotificationActivity extends BaseActivity {

    private ActivityNotificationBinding binding;
    private FirebaseFirestore db;
    private String currentUserId;
    private AdapterNotice adapterNotice;
    private ArrayList<ModelNotice> noticeArrayList;
    private ListenerRegistration noticeListener;
    private String userType;
    private PreloadingDialog preloadingDialog;

    // ১. স্ট্যাটিক লিস্ট ডিক্লেয়ার করুন (অ্যাপ সেশনে ডাটা ধরে রাখার জন্য)
    public static ArrayList<ModelNotice> cachedNoticeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = (firebaseUser != null) ? firebaseUser.getUid() : "";

        userType = getIntent().getStringExtra(MyUtils.USER_TYPE);
        noticeArrayList = new ArrayList<>();

        // ২. যদি ক্যাশে ডাটা থাকে, তবে শুরুতে সেটি লোড করুন (ইনস্ট্যান্ট লোডিং)
        if (!cachedNoticeList.isEmpty()) {
            noticeArrayList.addAll(cachedNoticeList);
        }
        // --- এই লাইনটি যোগ করুন ---
        binding.noNoticeTv.setVisibility(noticeArrayList.isEmpty() ? View.VISIBLE : View.GONE);
        // ------------------------

        preloadingDialog = new PreloadingDialog(this);

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

                // Simplify logic
                // শুধুমাত্র প্রয়োজনীয় ক্যাটাগরিগুলো ফিল্টার করছি
                List<String> clickableTypes = Arrays.asList(
                        MyUtils.NOTICE_TYPE_POST,
                        MyUtils.NOTICE_TYPE_BID,
                        MyUtils.NOTICE_TYPE_BID_CONFIRM
                );

                if (clickableTypes.contains(noticeType)) {
                    getOrderIdToOrderInfo(orderId, noticeType);
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

                // logic optimization
                boolean canSwipe = (MyUtils.NOTICE_RECEIVER_PARTNER.equals(userType) && MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeType)) ||
                        (MyUtils.NOTICE_RECEIVER_CUSTOMER.equals(userType) && MyUtils.NOTICE_TYPE_BID.equals(noticeType));

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

                // UI Update (Optimistic)
                noticeArrayList.remove(position);
                adapterNotice.notifyItemRemoved(position);

                // DB Delete
                db.collection("Notice").document(noticeId).delete()
                        .addOnFailureListener(e -> {
                            noticeArrayList.add(position, notice);
                            adapterNotice.notifyItemInserted(position);
                            MyToast.showShort(NotificationActivity.this, "Failed to delete");
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

    private void loadAllNotice() {
        // শুরুতে ১০ দিনের জন্য কল করা হচ্ছে
        fetchNoticesWithLimit(5);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchNoticesWithLimit(int days) {
        binding.noNoticeTv.setVisibility(View.GONE);
        if (currentUserId.isEmpty()) return;

        // ৩. ডায়ালগ তখনই দেখাবে যদি লিস্ট একদম খালি থাকে (অর্থাৎ প্রথমবার ওপেন করলে)
        if (days == 5 && preloadingDialog != null && noticeArrayList.isEmpty()) {
            preloadingDialog.show();
        }

        if (noticeListener != null) {
            noticeListener.remove();
        }

        long millis = (long) days * 24 * 60 * 60 * 1000;
        String cutoffTimestamp = String.valueOf(System.currentTimeMillis() - millis);
        List<String> validReceivers = Arrays.asList(currentUserId, MyUtils.NOTICE_RECEIVER_ALL, userType);

        noticeListener = db.collection("Notice")
                .whereIn("receivedUserId", validReceivers)
                .whereGreaterThanOrEqualTo("timestamp", cutoffTimestamp)
                .orderBy("timestamp", Query.Direction.ASCENDING) // ৪. ASCENDING করা হয়েছে যাতে নতুনগুলো আগে আসে
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error loading notices", e);
                        if (preloadingDialog != null) preloadingDialog.dismiss();
                        return;
                    }

                    if (snapshots == null) {
                        if (preloadingDialog != null) preloadingDialog.dismiss();
                        return;
                    }

                    List<ModelNotice> tempList = new ArrayList<>();
                    for (DocumentSnapshot ds : snapshots) {
                        ModelNotice notice = ds.toObject(ModelNotice.class);
                        if (notice == null) continue;
                        if (notice.getSenderType() != null && notice.getSenderType().equals(notice.getReceivedUserId())) continue;
                        tempList.add(notice);
                    }

                    if (tempList.size() < 10 && days < 20) {
                        fetchNoticesWithLimit(days + 5);
                    } else {
                        if (preloadingDialog != null) {
                            preloadingDialog.dismiss();
                        }

                        cachedNoticeList.clear();
                        cachedNoticeList.addAll(tempList);

                        noticeArrayList.clear();
                        noticeArrayList.addAll(tempList);

                        // এখানে আপডেট হচ্ছে
                        binding.noNoticeTv.setVisibility(noticeArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                        adapterNotice.notifyDataSetChanged();
                    }
                });
    }

    private void checkNotification() {
        if (currentUserId.isEmpty()) return;
        FirebaseDatabase.getInstance().getReference("NoticeCheck")
                .child(currentUserId)
                .child("checkNotice")
                .setValue(System.currentTimeMillis());
    }

    private void getOrderIdToOrderInfo(String orderId, String noticeType) {
        if (orderId == null || orderId.isEmpty()) return;

        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
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
                });
    }

    @Override
    protected void onDestroy() {
        if (noticeListener != null) {
            noticeListener.remove();
        }
        super.onDestroy();
    }
}