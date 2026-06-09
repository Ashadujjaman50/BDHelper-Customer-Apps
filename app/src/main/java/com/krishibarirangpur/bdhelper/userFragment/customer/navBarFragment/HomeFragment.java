package com.krishibarirangpur.bdhelper.userFragment.customer.navBarFragment;

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
import com.krishibarirangpur.bdhelper.sharedActivity.PromoActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentHomeBinding;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.customer.AddressActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.SubCategoryActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.TrendingCategoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.BannerSliderManager;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    //notification
    private ArrayList<ModelNotice> noticeArrayList;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views
        fetchSlides(view);

        // load new Notification Count
        loadNotificationCount();

        //notification  Activity
        binding.notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationActivity.class);
            intent.putExtra(MyUtils.USER_TYPE ,MyUtils.NOTICE_RECEIVER_CUSTOMER);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.trendingLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TrendingCategoryActivity.class);
            intent.putExtra(MyUtils.CATEGORY_TYPE, MyUtils.HOME_CATEGORY_TRENDING);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.popularLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TrendingCategoryActivity.class);
            intent.putExtra(MyUtils.CATEGORY_TYPE, MyUtils.HOME_CATEGORY_POPULAR);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.recommendLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TrendingCategoryActivity.class);
            intent.putExtra(MyUtils.CATEGORY_TYPE, MyUtils.HOME_CATEGORY_RECOMMEND);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.promoLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PromoActivity.class);
            intent.putExtra(MyUtils.DISCOUNT_TYPE, MyUtils.PROMO);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //Transport
        setOnClickListenerWithCategory(binding.transportLl,
                MyUtils.ROAD_TRANSPORT_ID,
                getString(R.string.road_transport));

        //Equipment
        setOnClickListenerWithCategory(binding.equipmentLl,
                MyUtils.EQUIPMENT_ID,
                getString(R.string.equipment));

        //Rent A Car
        setOnClickListenerWithCategory(binding.rentCarLl,
                MyUtils.RENT_A_CAR_ID,
                getString(R.string.rent_a_car));

        //Skilled Labour
        setOnClickListenerWithCategory(binding.skilledLaborerLl,
                MyUtils.SKILLED_LABOR_ID,
                getString(R.string.skilled_labor));

        //Harvester
        setAddressActivityClickListener(binding.harvesterLl,
                MyUtils.HARVESTER_MACHINE_ID,
                MyUtils.HARVESTER_MACHINE_ID,
                getString(R.string.harvester));

        //loBed
        setAddressActivityClickListener(binding.loBetLl,
                MyUtils.ROAD_TRANSPORT_ID,
                MyUtils.SUB_LOW_BED_ID,
                getString(R.string.lo_bet));

        //Home shifting
        setAddressActivityClickListener(binding.homeShiftingLl,
                MyUtils.HOME_SHIFTING_ID,
                MyUtils.HOME_SHIFTING_ID,
                getString(R.string.home_office_shifting));

        binding.shopLl.setOnClickListener(v -> {
            //Shop Coming soon
            MyToast.showShort(requireContext(),"Coming soon...");
        });

        binding.contactUsText.setOnClickListener(v -> {
            //Chat Activity
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("adminID", MyUtils.CHAT_ADMIN_ID);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void setOnClickListenerWithCategory(View view, String categoryId, String categoryName) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SubCategoryActivity.class);
            intent.putExtra(MyUtils.categoryId, categoryId);
            intent.putExtra(MyUtils.categoryName, categoryName);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setAddressActivityClickListener(View view, String categoryId, String subCategoryId, String subCategoryName) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddressActivity.class);
            intent.putExtra(MyUtils.categoryId, categoryId);
            intent.putExtra(MyUtils.subCategoryId, subCategoryId);
            intent.putExtra(MyUtils.subCategoryName, subCategoryName);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private long checkNotice = 0;

    private void loadNotificationCount() {
        noticeArrayList = new ArrayList<>();
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DatabaseReference checkNoticeRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseCollectionTable.NOTICE_CHECK)
                .child(currentUserId);

        // 🔹 প্রথমে Realtime DB থেকে checkNotice নেবে
        checkNoticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkNotice = Long.parseLong("" + dataSnapshot.child("checkNotice").getValue());

                    // 🔹 Firestore Query - Server-side filtering যোগ করা হয়েছে
                    FirebaseFirestore.getInstance()
                            .collection(FirebaseCollectionTable.NOTICE)
                            .whereGreaterThanOrEqualTo("noticeId", String.valueOf(checkNotice))
                            .get() // রিয়েলটাইম লিসেনারের বদলে get() ব্যবহার করা হয়েছে খরচ কমাতে
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots != null) {
                                    noticeArrayList.clear();

                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        String receivedUserId = doc.getString("receivedUserId");
                                        String senderType = doc.getString("senderType");

                                        if (receivedUserId != null &&
                                                (receivedUserId.equals(currentUserId) ||
                                                        receivedUserId.equals(MyUtils.NOTICE_RECEIVER_ALL) ||
                                                        receivedUserId.equals(MyUtils.NOTICE_RECEIVER_CUSTOMER))) {

                                            if (senderType != null && (senderType.equals(MyUtils.NOTICE_SENDER_ADMIN) || senderType.equals(MyUtils.NOTICE_SENDER_PARTNER))) {
                                                noticeArrayList.add(doc.toObject(ModelNotice.class));
                                            }
                                        }
                                    }

                                    // 🔹 UI আপডেট
                                    if (binding != null) {
                                        if (!noticeArrayList.isEmpty()) {
                                            binding.notificationCountTv.setText(String.valueOf(noticeArrayList.size()));
                                            binding.notificationCountTv.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.notificationCountTv.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching notices", e));
                } else {
                    binding.notificationCountTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void fetchSlides(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        BannerSliderManager manager = new BannerSliderManager();
        manager.loadImageSlider(getContext(), imageSlider, "Customer", "Home");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}