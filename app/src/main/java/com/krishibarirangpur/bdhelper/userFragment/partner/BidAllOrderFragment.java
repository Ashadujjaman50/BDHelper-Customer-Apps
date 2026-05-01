package com.krishibarirangpur.bdhelper.userFragment.partner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.partner.OrderPartnerAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentBidAllOrderBinding;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.userActivity.partner.AddServiceActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class BidAllOrderFragment extends Fragment {

    private FragmentBidAllOrderBinding binding;
    private ArrayList<String> subCategoryIds;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OrderPartnerAdapter adapter;
    private final ArrayList<OrderModel> orderList = new ArrayList<>();
    private ListenerRegistration orderListener;

    public BidAllOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subCategoryIds = getArguments().getStringArrayList("subCategoryIds");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bid_all_order, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new OrderPartnerAdapter(requireContext(), orderList);
        binding.myRentRecyclerView.setAdapter(adapter);

        fetchAllOrders();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < 0 || position >= orderList.size()) return;

                OrderModel order = orderList.get(position);
                String clickedSubCategoryId = order.getOrderInfo().getSubCategoryId();

                if (subCategoryIds == null || !subCategoryIds.contains(clickedSubCategoryId)) {
                    showAlertDialog(order);
                } else {
                    Intent intent = new Intent(requireContext(), BidActivity.class);
                    intent.putExtra(MyUtils.bidAction, "new");
                    intent.putExtra(MyUtils.USER_TYPE, MyUtils.NOTICE_RECEIVER_PARTNER);
                    intent.putExtra(MyUtils.orderId, order.getOrderInfo().getOrderId());
                    intent.putExtra(MyUtils.categoryId, order.getOrderInfo().getCategoryId());
                    intent.putExtra(MyUtils.subCategoryId, clickedSubCategoryId);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override public void onShowItemClick(int position) {}
            @Override public void onDeleteItemClick(int position) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchAllOrders() {
        long todayMillis = CommonClass.getTodayStartMillis();

        // Optimized: Filter by multiple statuses and date in query
        Query query = db.collection("orders")
                .whereIn("orderInfo.status", Arrays.asList("pending", "process"))
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(todayMillis))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING);

        orderListener = query.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.e("BidAllOrder", "Snapshot error", error);
                return;
            }

            if (snapshots != null) {
                orderList.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    OrderModel order = doc.toObject(OrderModel.class);
                    if (order != null) orderList.add(order);
                }

                boolean isEmpty = orderList.isEmpty();
                binding.noOnePostYet.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                binding.myRentRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showAlertDialog(OrderModel order) {
        String categoryId = order.getOrderInfo().getCategoryId();
        String subCategoryId = order.getOrderInfo().getSubCategoryId();
        String subCategoryName = CommonClass.getSubCategoryName(requireContext(), subCategoryId);

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_no_service_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setView(view).create();
        alertDialog.show();

        ImageView vehicleIv = view.findViewById(R.id.vehicleIv);
        TextView subCategoryNameTv = view.findViewById(R.id.subCategoryNameTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView addServiceBtn = view.findViewById(R.id.addServiceBtn);
        ImageView closeBtn = view.findViewById(R.id.closeBtn);

        subCategoryNameTv.setText(subCategoryName);
        descriptionTv.setText("অ্যাপে, আপনার কোনো " + subCategoryName + " যোগ করা নেই। \n সার্ভিস যোগ করে বিড করুন।");
        addServiceBtn.setText(subCategoryName + " যোগ করুন");
        vehicleIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), CommonClass.getIconForSubCategory(subCategoryId)));

        closeBtn.setOnClickListener(v -> alertDialog.dismiss());
        addServiceBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(requireActivity(), AddServiceActivity.class);
            intent.putExtra("loadDefault", "selectAddService");
            intent.putExtra(MyUtils.categoryId, categoryId);
            intent.putExtra(MyUtils.subCategoryId, subCategoryId);
            intent.putExtra(MyUtils.subCategoryName, subCategoryName);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onDestroyView() {
        if (orderListener != null) orderListener.remove();
        super.onDestroyView();
        binding = null;
    }
}
