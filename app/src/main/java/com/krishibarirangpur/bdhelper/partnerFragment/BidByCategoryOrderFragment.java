package com.krishibarirangpur.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.OrderPartnerAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentBidByCategoryOrderBinding;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.MyToast;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.partner.AddServiceActivity;
import com.krishibarirangpur.bdhelper.partner.BidActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class BidByCategoryOrderFragment extends Fragment {

    private FragmentBidByCategoryOrderBinding binding;

    private ArrayList<String> subCategoryIds;

    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    OrderPartnerAdapter orderPartnerAdapter;
    ArrayList<OrderModel> orderModelArrayList;

    public BidByCategoryOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // এখানে arguments থেকে subCategoryIds গুলো get করব
        if (getArguments() != null) {
            subCategoryIds = getArguments().getStringArrayList("subCategoryIds");
            Log.d("BidByCategory", "Got Unique IDs: " + subCategoryIds);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bid_by_category_order, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init view
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        orderModelArrayList = new ArrayList<>();
        orderPartnerAdapter = new OrderPartnerAdapter(requireContext(), orderModelArrayList);
        binding.myCategoryRentRecyclerView.setAdapter(orderPartnerAdapter);

        //
        getAllOrderPost();

        orderPartnerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedSubCategoryId = orderModelArrayList.get(position)
                        .getOrderInfo()
                        .getSubCategoryId();

                Log.d("OrderClick", "Clicked SubCategory ID: " + clickedSubCategoryId);

                // চেক করো subCategoryIds লিস্টে আছে কিনা
                if (subCategoryIds == null || !subCategoryIds.contains(clickedSubCategoryId)) {
                    // না থাকলে alert দেখাও
                    showAlertDialog(position);
                }
                else {
                    Intent intent = new Intent(getContext(), BidActivity.class);
                    intent.putExtra(MyUtils.bidAction,"new");
                    intent.putExtra("user_type", "partner");
                    intent.putExtra(MyUtils.orderId, orderModelArrayList.get(position).getOrderInfo().getOrderId());
                    intent.putExtra(MyUtils.categoryId, orderModelArrayList.get(position).getOrderInfo().getCategoryId());
                    intent.putExtra(MyUtils.subCategoryId, clickedSubCategoryId);
                    requireActivity().startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onShowItemClick(int position) { }

            @Override
            public void onDeleteItemClick(int position) { }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllOrderPost() {

        if (subCategoryIds == null || subCategoryIds.isEmpty()) {
            MyToast.showShort(getContext(), "❌ No SubCategory IDs found");
            return;
        }

        long todayMillis = CommonClass.getTodayStartMillis(); // আজকের 00:00:00 সময় থেকে millis

        // Firestore limitation: whereIn max 10 values per query
        db.collection("orders")
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(todayMillis))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING) // rentTime অনুযায়ী সাজাও
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.d("Firestore", "Error: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        orderModelArrayList.clear();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            if (order != null && (order.getOrderInfo().getStatus().equals("pending")  || order.getOrderInfo().getStatus().equals("process"))) {
                                orderModelArrayList.add(order);
                            }
                        }
                        // empty হলে UI দেখাও
                        if (orderModelArrayList.isEmpty()) {
                            binding.noOnePostYet.setVisibility(View.VISIBLE);
                        } else {
                            binding.noOnePostYet.setVisibility(View.GONE);
                        }
                        orderPartnerAdapter.notifyDataSetChanged();
                        Log.d("Firestore", "Loaded: " + orderModelArrayList.size() + " orders");
                    } else {
                        binding.noOnePostYet.setVisibility(View.VISIBLE);
                        Log.d("Firestore", "No matching orders found");
                    }
                });

    }


    @SuppressLint("SetTextI18n")
    private void showAlertDialog(int position) {

        String categoryId = orderModelArrayList.get(position).getOrderInfo().getCategoryId();
        String subCategoryId = orderModelArrayList.get(position).getOrderInfo().getSubCategoryId();

        AlertDialog.Builder builder =new AlertDialog.Builder(requireContext());
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_no_service_dialog,null);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ImageView vehicleIv = view.findViewById(R.id.vehicleIv);
        TextView subCategoryNameTv = view.findViewById(R.id.subCategoryNameTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView addServiceBtn = view.findViewById(R.id.addServiceBtn);
        ImageView closeBtn = view.findViewById(R.id.closeBtn);

        //setData
        subCategoryNameTv.setText(CommonClass.getSubCategoryName(requireContext(), subCategoryId));
        descriptionTv.setText("অ্যাপে, আপনার কোনো "+ subCategoryNameTv.getText().toString() +" যোগ করা নেই। \n সার্ভিস যোগ করে বিড করুন।");
        addServiceBtn.setText(subCategoryNameTv.getText().toString()+ " যোগ করুন");

        // Set icon
        int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
        vehicleIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));

        closeBtn.setOnClickListener(v -> alertDialog.dismiss());

        addServiceBtn.setOnClickListener(v -> {

            Intent intent = new Intent(requireActivity(), AddServiceActivity.class);
            intent.putExtra("loadDefault", "selectAddService"); // 🔹 এটিই গুরুত্বপূর্ণ
            intent.putExtra(MyUtils.categoryId, categoryId);
            intent.putExtra(MyUtils.subCategoryId, subCategoryId);
            intent.putExtra(MyUtils.subCategoryName, subCategoryNameTv.getText().toString());
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.CENTER);

    }


}