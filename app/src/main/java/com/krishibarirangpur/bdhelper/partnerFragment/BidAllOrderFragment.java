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
import com.krishibarirangpur.bdhelper.databinding.FragmentBidAllOrderBinding;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.partner.AddServiceActivity;
import com.krishibarirangpur.bdhelper.partner.BidActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class BidAllOrderFragment extends Fragment {

    private FragmentBidAllOrderBinding binding;

    private ArrayList<String> subCategoryIds;

    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    OrderPartnerAdapter orderPartnerAdapter;
    ArrayList<OrderModel> orderModelArrayList;
    private long today;

    public BidAllOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // এখানে arguments থেকে subCategoryIds গুলো get করব
        if (getArguments() != null) {
            subCategoryIds = getArguments().getStringArrayList("subCategoryIds");
            Log.d("BidAllCategory", "Got Unique IDs: " + subCategoryIds);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bid_all_order, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //inti views
        firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        orderModelArrayList = new ArrayList<>();
        orderPartnerAdapter = new OrderPartnerAdapter(getContext(), orderModelArrayList);
        binding.myRentRecyclerView.setAdapter(orderPartnerAdapter);


        //load all Order
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
                    intent.putExtra(MyUtils.USER_TYPE, MyUtils.NOTICE_RECEIVER_PARTNER);
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
        long todayMillis = CommonClass.getTodayStartMillis(); // আজকের 00:00:00 সময় থেকে millis

        db.collection("orders")
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(todayMillis))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING) // rentTime অনুযায়ী সাজাও
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        MyToast.showShort(getContext(), "❌ Error: " + error.getMessage());
                        Log.d("Firestore", "loadAllData: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        orderModelArrayList.clear();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);

                            if (order != null) {
                                String status = order.getOrderInfo().getStatus();

                                // ✅ শুধু pending বা process এবং ভবিষ্যতের তারিখের order নেবে
                                if ("pending".equals(status) || "process".equals(status)) {
                                    orderModelArrayList.add(order);
                                }
                            }
                        }

                        if (orderModelArrayList.isEmpty()){
                            binding.noOnePostYet.setVisibility(View.VISIBLE);
                            binding.myRentRecyclerView.setVisibility(View.GONE);
                        }
                        else {
                            binding.noOnePostYet.setVisibility(View.GONE);
                            binding.myRentRecyclerView.setVisibility(View.VISIBLE);
                        }

                        orderPartnerAdapter.notifyDataSetChanged();

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