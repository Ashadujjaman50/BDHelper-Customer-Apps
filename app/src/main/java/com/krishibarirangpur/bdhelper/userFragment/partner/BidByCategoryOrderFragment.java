package com.krishibarirangpur.bdhelper.userFragment.partner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.partner.OrderPartnerAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentBidByCategoryOrderBinding;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;

public class BidByCategoryOrderFragment extends Fragment {

    private FragmentBidByCategoryOrderBinding binding;
    private ArrayList<String> subCategoryIds;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OrderPartnerAdapter adapter;
    private final ArrayList<OrderModel> orderList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subCategoryIds = getArguments().getStringArrayList("subCategoryIds");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bid_by_category_order, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new OrderPartnerAdapter(requireContext(), orderList);
        binding.myCategoryRentRecyclerView.setAdapter(adapter);

        fetchFilteredOrders();

        // ল্যাম্বডার পরিবর্তে Anonymous Inner Class ব্যবহার করা হয়েছে
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OrderModel order = orderList.get(position);
                Intent intent = new Intent(getContext(), BidActivity.class);
                intent.putExtra(MyUtils.bidAction, "new");
                intent.putExtra("user_type", "partner");
                intent.putExtra(MyUtils.orderId, order.getOrderInfo().getOrderId());
                intent.putExtra(MyUtils.categoryId, order.getOrderInfo().getCategoryId());
                intent.putExtra(MyUtils.subCategoryId, order.getOrderInfo().getSubCategoryId());
                startActivity(intent);
            }

            @Override
            public void onShowItemClick(int position) {
                // প্রয়োজন না থাকলে খালি রাখুন
            }

            @Override
            public void onDeleteItemClick(int position) {
                // প্রয়োজন না থাকলে খালি রাখুন
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchFilteredOrders() {
        if (subCategoryIds == null || subCategoryIds.isEmpty()) {
            binding.noOnePostYet.setVisibility(View.VISIBLE);
            return;
        }

        long today = CommonClass.getTodayStartMillis();

        // Optimized: Only fetch orders matching partner's subCategoryIds
        Query query = db.collection("orders")
                .whereIn("orderInfo.subCategoryId", subCategoryIds)
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(today))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING);

        query.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.e("BidByCategory", "Query failed", error);
                return;
            }

            if (snapshots != null) {
                orderList.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    OrderModel order = doc.toObject(OrderModel.class);
                    if (order != null) {
                        String status = order.getOrderInfo().getStatus();
                        if ("pending".equals(status) || "process".equals(status)) {
                            orderList.add(order);
                        }
                    }
                }
                binding.noOnePostYet.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }
}