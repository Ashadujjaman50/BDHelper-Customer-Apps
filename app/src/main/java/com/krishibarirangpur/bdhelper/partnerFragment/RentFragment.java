package com.krishibarirangpur.bdhelper.partnerFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.NotificationActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ViewPagerServiceAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentRentBinding;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.partner.AddServiceActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Objects;

public class RentFragment extends Fragment {

    private FragmentRentBinding binding;
    //notification
    private ArrayList<ModelNotice> noticeArrayList;

    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_rent, container, false);
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

        binding.addServiceBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddServiceActivity.class);
            intent.putExtra("loadDefault", "selectCategory");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //View Fragment Courser
        FragmentManager fm = getChildFragmentManager();
        ViewPagerServiceAdapter serviceAdapter = new ViewPagerServiceAdapter(fm, getLifecycle());
        binding.rentViewPager.setAdapter(serviceAdapter);
        binding.rentTabLayout.addTab(binding.rentTabLayout.newTab().setText(getString(R.string.all_service)));
        binding.rentTabLayout.addTab(binding.rentTabLayout.newTab().setText(getString(R.string.need_document)));
        binding.rentViewPager.setUserInputEnabled(true);
        binding.rentViewPager.setSaveEnabled(false);

        binding.rentTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .alpha(1f)
                        .setDuration(200)
                        .start();

                setPagerFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(0.8f)
                        .setDuration(200)
                        .start();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: Do something on reselection
            }
        });


        binding.rentViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.rentTabLayout.selectTab(binding.rentTabLayout.getTabAt(position));
            }
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

    private void setPagerFragment(int a) {
        binding.rentViewPager.setCurrentItem(a);
    }
}