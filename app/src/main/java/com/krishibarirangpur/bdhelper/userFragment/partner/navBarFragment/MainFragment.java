package com.krishibarirangpur.bdhelper.userFragment.partner.navBarFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.krishibarirangpur.bdhelper.sharedActivity.ChatActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.NotificationActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentMainBinding;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.OrderRentActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.ProductActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.krishibarirangpur.bdhelper.utils.bothWidget.BannerSliderManager;

import java.util.ArrayList;
import java.util.Objects;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    //notification
    private ArrayList<ModelNotice> noticeArrayList;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views

        fetchSlides(view);

        // load new Notification Count
        loadNotificationCount();

        binding.allOrderBidLl.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OrderRentActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //notification  Activity
        binding.notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationActivity.class);
            intent.putExtra(MyUtils.USER_TYPE ,MyUtils.NOTICE_RECEIVER_PARTNER);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //success All Bid
        binding.successBidLL.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BidActivity.class);
            intent.putExtra(MyUtils.bidAction,"confirmed");
            intent.putExtra(MyUtils.USER_TYPE, "partner");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //pending All Bid
        binding.pendingBidLL.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BidActivity.class);
            intent.putExtra(MyUtils.bidAction,"pending");
            intent.putExtra(MyUtils.USER_TYPE, "partner");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        binding.contactUsText.setOnClickListener(v -> {
            //Chat Activity
            Intent intent = new Intent(requireActivity(), ChatActivity.class);
            intent.putExtra("adminID", MyUtils.CHAT_ADMIN_ID);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //battery
        binding.batteryLl.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("productType", "battery");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.jobCircularLl.setOnClickListener(v -> MyToast.showShort(getContext(), "শীঘ্রই আসছে..."));

    }

    private ListenerRegistration noticeListener;
    private long checkNotice = 0;

    private void loadNotificationCount() {
        noticeArrayList = new ArrayList<>();
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference checkNoticeRef = FirebaseDatabase.getInstance()
                .getReference("NoticeCheck")
                .child(currentUserId);

        // 🔹 প্রথমে Realtime DB থেকে checkNotice নেবে
        checkNoticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkNotice = Long.parseLong("" + dataSnapshot.child("checkNotice").getValue());

                    // 🔹 Firestore Listener দিয়ে রিয়েলটাইম Notice শুনবে
                    if (noticeListener != null) {
                        noticeListener.remove();
                    }

                    noticeListener = FirebaseFirestore.getInstance()
                            .collection("Notice")
                            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                                if (e != null) {
                                    Log.e("FirestoreError", "Error fetching notices", e);
                                    return;
                                }

                                if (queryDocumentSnapshots != null) {
                                    noticeArrayList.clear();

                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                                        String receivedUserId = doc.getString("receivedUserId");
                                        String senderType = doc.getString("senderType");
                                        long noticeId = Long.parseLong(Objects.requireNonNull(doc.getString("noticeId")));

                                        if (noticeId >= checkNotice) {
                                            ModelNotice modelNotice = doc.toObject(ModelNotice.class);
                                            assert receivedUserId != null;
                                            if ((receivedUserId.equals(currentUserId) ||
                                                    receivedUserId.equals(MyUtils.NOTICE_RECEIVER_ALL) ||
                                                    receivedUserId.equals(MyUtils.NOTICE_RECEIVER_PARTNER))) {
                                                assert senderType != null;
                                                if (senderType.equals(MyUtils.NOTICE_SENDER_ADMIN) || senderType.equals(MyUtils.NOTICE_SENDER_CUSTOMER)) {

                                                    noticeArrayList.add(modelNotice);
                                                }
                                            }
                                        }
                                    }

                                    // 🔹 UI আপডেট
                                    if (!noticeArrayList.isEmpty()) {
                                        binding.notificationCountTv.setText(String.valueOf(noticeArrayList.size()));
                                        binding.notificationCountTv.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.notificationCountTv.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    binding.notificationCountTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (noticeListener != null) {
            noticeListener.remove(); // 🔹 Firestore Listener remove করতে হবে
        }
    }


    private void fetchSlides(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        BannerSliderManager manager = new BannerSliderManager();
        manager.loadImageSlider(imageSlider, "Partner", "Home");
    }

}