package com.krishibarirangpur.bdhelper.userFragment;

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
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.krishibarirangpur.bdhelper.ChatActivity;
import com.krishibarirangpur.bdhelper.NotificationActivity;
import com.krishibarirangpur.bdhelper.PromoActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentHomeBinding;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.model.SlideImage;
import com.krishibarirangpur.bdhelper.myUtils.MyToast;
import com.krishibarirangpur.bdhelper.myUtils.MyUtils;
import com.krishibarirangpur.bdhelper.user.AddressActivity;
import com.krishibarirangpur.bdhelper.user.SubCategoryActivity;
import com.krishibarirangpur.bdhelper.user.TrendingCategoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    //notification
    private ArrayList<ModelNotice> noticeArrayList;
    ArrayList<SlideImage> slideImageArrayList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // load new Notification Count
        loadNotificationCount();

        //notification  Activity
        binding.notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.trendingLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TrendingCategoryActivity.class);
            intent.putExtra("categoryType", "Trending");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.popularLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TrendingCategoryActivity.class);
            intent.putExtra("categoryType", "Popular");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.recommendLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TrendingCategoryActivity.class);
            intent.putExtra("categoryType", "Recommend");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.promoLl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PromoActivity.class);
            intent.putExtra("discountType", "promo");
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
            MyToast.showShort(requireActivity(),"Coming soon...");
        });

        binding.contactUsText.setOnClickListener(v -> {
            //Chat Activity
            Intent intent = new Intent(requireActivity(), ChatActivity.class);
            intent.putExtra("adminID", MyUtils.CHAT_ADMIN_ID);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        fetchSlides(view);

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
                                                    receivedUserId.equals("all") ||
                                                    receivedUserId.equals("customer"))) {
                                                assert senderType != null;
                                                if (senderType.equals("admin") || senderType.equals("vendor")) {

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
        ImageSlider imageSlider;
        imageSlider = view.findViewById(R.id.image_slider);
        slideImageArrayList = new ArrayList<>();
        List<SlideModel> imageList = new ArrayList<>();


        db.collection("slides")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    slideImageArrayList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        SlideImage slide = doc.toObject(SlideImage.class);
                        if (slide != null && slide.getSlideType().equals("customerHome")) {
                            slideImageArrayList.add(slide);
                        }
                    }

                    // slideList এখন Firestore এর সব slide রাখছে
                    Log.d("Firestore", "Total slides: " + imageList.size());
                    for (SlideImage s : slideImageArrayList) {
                        imageList.add(new SlideModel(s.getSlideImage(), ScaleTypes.FIT));
                        Log.d("Firestore", "Slide: " + s.getSlideDescription() + " | " + s.getSlideImage());
                    }
                    imageSlider.startSliding(2000);
                    imageSlider.setImageList(imageList);

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching slides", e);
                });



    }




}