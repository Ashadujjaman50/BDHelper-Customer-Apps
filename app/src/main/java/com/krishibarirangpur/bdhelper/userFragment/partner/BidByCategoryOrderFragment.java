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
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;

public class BidByCategoryOrderFragment extends Fragment {

    private FragmentBidByCategoryOrderBinding binding;
    private ArrayList<String> subCategoryIds;
    private ArrayList<String> categoryIds;
    private ArrayList<String> sizeAndCapacities;
    //private ArrayList<String> categoryAndYears;
    
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OrderPartnerAdapter adapter;
    private final ArrayList<OrderModel> orderList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subCategoryIds = getArguments().getStringArrayList("subCategoryIds");
            categoryIds = getArguments().getStringArrayList("categoryIds");
            sizeAndCapacities = getArguments().getStringArrayList("sizeAndCapacities");
            //categoryAndYears = getArguments().getStringArrayList("categoryAndYears");
            Log.d("BidByCategoryLog", "Args: servicesCount=" + (subCategoryIds != null ? subCategoryIds.size() : 0));
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

            @Override public void onShowItemClick(int position) {}
            @Override public void onDeleteItemClick(int position) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchFilteredOrders() {
        if (subCategoryIds == null || subCategoryIds.isEmpty()) {
            binding.noOnePostYet.setVisibility(View.VISIBLE);
            return;
        }

        long today = System.currentTimeMillis();

        // Optimized: Only fetch orders matching partner's subCategoryIds
        Query query = db.collection("orders")
                .whereIn("orderInfo.subCategoryId", subCategoryIds)
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(today))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING);

        query.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.e("BidByCategoryLog", "Firestore Error: " + error.getMessage(), error);
                return;
            }

            if (snapshots != null) {
                Log.d("BidByCategoryLog", "Snapshots size: " + snapshots.size());
                orderList.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    OrderModel order = doc.toObject(OrderModel.class);
                    if (order != null) {
                        String status = order.getOrderInfo().getStatus();
                        if ("pending".equals(status) || "process".equals(status)) {
                            
                            if (isOrderMatchingAnyPartnerService(order)) {
                                orderList.add(order);
                            }
                        }
                    }
                }
                Log.d("BidByCategoryLog", "Final List size: " + orderList.size());
                binding.noOnePostYet.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isOrderMatchingAnyPartnerService(OrderModel order) {
        String orderSubCatId = order.getOrderInfo().getSubCategoryId();
        String orderCatId = order.getOrderInfo().getCategoryId();
        
        for (int i = 0; i < subCategoryIds.size(); i++) {
            String pSubId = subCategoryIds.get(i);
            String pCatId = categoryIds.get(i);
            String pSize = sizeAndCapacities.get(i);
            //String pYear = categoryAndYears.get(i);

            // ১. ক্যাটাগরি এবং সাব-ক্যাটাগরি ম্যাচিং
            if (pSubId.equals(orderSubCatId) && pCatId.equals(orderCatId)) {
                
                // ২. Road Transport এর ক্ষেত্রে স্পেসিফিকেশন চেক
                if (MyUtils.ROAD_TRANSPORT_ID.equals(pCatId) || MyUtils.HARVESTER_MACHINE_ID.equals(pCatId)) {
                    // Charger Van হলে সরাসরি ম্যাচ
                    if (MyUtils.SUB_CHARGER_VAN_ID.equals(pSubId)) {
                        return true;
                    }
                    // অন্য যানের ক্ষেত্রে সাইজ ম্যাচিং
                    String orderCapacity = order.getSpecInfo() != null ? order.getSpecInfo().getCapacity() : null;
                    if (isMetadataMatched(pSize, orderCapacity)) {
                        return true;
                    }
                } 
                // ৩. Harvester/Equipment এর ক্ষেত্রে স্পেসিফিকেশন চেক
                /*else if (MyUtils.EQUIPMENT_ID.equals(pCatId)) {
                    String orderType = order.getSpecInfo() != null ? order.getSpecInfo().getTypes() : null;
                    if (isMetadataMatched(pSize, orderType)) {
                        return true;
                    }
                }*/
                // ৪. অন্যান্য ক্যাটাগরির জন্য (যেমন: Rent a car) সরাসরি ম্যাচ
                else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMetadataMatched(String partnerValue, String orderValue) {
        if (partnerValue == null || orderValue == null) return false;
        String pNormalized = Replacement.normalizeMetadata(partnerValue);
        String oNormalized = Replacement.normalizeMetadata(orderValue);
        return pNormalized.equals(oNormalized);
    }
}
