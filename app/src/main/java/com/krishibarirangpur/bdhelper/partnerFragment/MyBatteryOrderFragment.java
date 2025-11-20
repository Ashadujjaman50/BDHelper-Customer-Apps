package com.krishibarirangpur.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.BatteryOrderAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentMyBatteryOrderBinding;
import com.krishibarirangpur.bdhelper.model.BatteryOrderModel;
import com.krishibarirangpur.bdhelper.partner.ProductActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MyBatteryOrderFragment extends Fragment {

    private FragmentMyBatteryOrderBinding binding;

    private BatteryOrderAdapter orderAdapter;
    private List<BatteryOrderModel> orderList = new ArrayList<>();
    private FirebaseFirestore db;

    public MyBatteryOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_battery_order, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        binding.buttonOrder.setOnClickListener(v -> {
            ProductActivity parent = (ProductActivity) getActivity();
            parent.setPagerFragment(1);
        });

        db = FirebaseFirestore.getInstance();

        orderAdapter = new BatteryOrderAdapter(getContext(), orderList, order -> {
           //👉 Click করলে এখানে Order Details এ নেয়ার কোড লিখতে পারো
        });
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(orderAdapter);


        fetchOrdersRealtime();


    }

    private ListenerRegistration orderListener;

    @SuppressLint("NotifyDataSetChanged")
    private void fetchOrdersRealtime() {
        orderListener = db.collection("batteryOrder")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        binding.emptyOrderLayout.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);
                        return;
                    }

                    orderList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            BatteryOrderModel order = doc.toObject(BatteryOrderModel.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                    }

                    if (orderList.isEmpty()) {
                        binding.emptyOrderLayout.setVisibility(View.VISIBLE);
                        binding.recyclerView.setVisibility(View.GONE);
                    } else {
                        binding.emptyOrderLayout.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        orderAdapter.notifyDataSetChanged();
                    }
                });
    }

    // Activity/Fragment destroy হলে listener remove করা
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (orderListener != null) {
            orderListener.remove();
        }
    }

}