package com.krishibarirangpur.bdhelper.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Button;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.BidCustomerAdapter;
import com.krishibarirangpur.bdhelper.adapter.BidPartnerAdapter;
import com.krishibarirangpur.bdhelper.adapter.ReviewAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentBidEquipmentBinding;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.model.ReviewModel;
import com.krishibarirangpur.bdhelper.model.ServiceModel;
import com.krishibarirangpur.bdhelper.myUtils.CommonClass;
import com.krishibarirangpur.bdhelper.myUtils.MyToast;
import com.krishibarirangpur.bdhelper.myUtils.MyUtils;
import com.krishibarirangpur.bdhelper.myUtils.PreloadingDialog;
import com.krishibarirangpur.bdhelper.myUtils.Replacement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BidEquipmentFragment extends Fragment implements BidCustomerAdapter.OnBidActionListener{

    private FragmentBidEquipmentBinding binding;

    private String currentUserId, userId, orderId, rentTime, categoryId, subCategoryId, user_type, orderStatus, vendorId;

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    ArrayList<ServiceModel> serviceModelArrayList;
    ServiceModel selectedServiceModel; // ✅ সিলেক্টেড সার্ভিস রাখবে এখানে

    ArrayList<BidModel> bidModelArrayList;
    BidPartnerAdapter bidPartnerAdapter;
    BidCustomerAdapter bidCustomerAdapter;

    LoadingDialog loadingDialog;

    PreloadingDialog preloadingDialog;


    public BidEquipmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_type = getArguments().getString("user_type");
            orderId = getArguments().getString("orderId");
            subCategoryId = getArguments().getString("subCategoryId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bid_equipment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        currentUserId = firebaseUser.getUid();

        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        preloadingDialog = new PreloadingDialog(requireContext());


        //get current Order info
        getCurrentOrderInfo();

        if (user_type.equals("partner")){
            //Load Current Partner Bid
            loadCurrentPartnerBid();

            //loadPartner ServiceInfo
            loadPartnerInfo();

            // Setup vehicle picker dialog
            setupServicePicker();

            // ✅ submit button click
            binding.bidSubmitBtn.setOnClickListener(v -> {
                if (CommonClass.validateField(binding.selectVehicleNameTv)) return;
                else if (CommonClass.validateField(binding.amountEt)) return;
                else dataUploadInDatabase(selectedServiceModel);
            });
        }
        else if (user_type.equals("customer")) {
            binding.bidRunMsgTv.setText("বিডিং চলছে");
            binding.bidMsgTv.setVisibility(View.VISIBLE);
            binding.bidRV.setVisibility(View.VISIBLE);

            //loadCurrent order bid
            loadCurrentOrderBid();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentOrderBid() {
        bidModelArrayList = new ArrayList<>();
        bidCustomerAdapter = new BidCustomerAdapter(getContext(), bidModelArrayList, this);
        binding.bidRV.setAdapter(bidCustomerAdapter);

        db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId)        // Filter by orderId
                .orderBy("bidInfo.bidAmount", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error)->{

                    if (error != null) {
                        Log.d("Firestore", "loadAllData: " + error.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        bidModelArrayList.clear(); // clear before adding

                        BidModel confirmedBid = null;

                        for (DocumentSnapshot doc : querySnapshot) {
                            BidModel bidModel = doc.toObject(BidModel.class);
                            if (bidModel != null) {
                                if ("confirmed".equalsIgnoreCase(bidModel.getBidInfo().getStatus())) {
                                    // যদি confirmed bid থাকে, শুধু সেটা রাখো
                                    confirmedBid = bidModel;
                                    break; // আর loop চালানোর দরকার নেই
                                }
                                else {
                                    // confirmed না হলে সব bid add করতে পারো, পরবর্তীতে filter করো
                                    bidModelArrayList.add(bidModel);
                                }
                            }
                        }

                        if (confirmedBid != null) {
                            bidModelArrayList.clear();
                            bidModelArrayList.add(confirmedBid);
                        }

                        bidCustomerAdapter.notifyDataSetChanged();

                        if (bidModelArrayList.isEmpty()) {
                            binding.noOneBidYet.setVisibility(View.VISIBLE);
                            binding.bidMsgTv.setVisibility(View.GONE);
                        } else {
                            binding.noOneBidYet.setVisibility(View.GONE);
                            binding.bidMsgTv.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }


    // 🔹 Handle Call Button Click
    @Override
    public void onCallClicked(BidModel bidModel) {
        //String phone = bidModel.getBidInfo().getBidId() != null ? bidModel.getOrderInfo().getOrderId() : null;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + MyUtils.HOTLINE_NUMBER));
        startActivity(intent);
    }

    // 🔹 Handle Confirm Button Click
    public void onConfirmOrderClicked(BidModel bidModel) {
        try {
            // rentTime যেহেতু millisecond string, তাই long এ convert করো
            long rentMillis = CommonClass.parseMillis(bidModel.getOrderInfo().getRentTime());
            long todayMillis = CommonClass.getTodayStartMillis();

            // 🔹 Compare করো (rentTime আজকের বা ভবিষ্যতের হলে valid)
            if (rentMillis >= todayMillis) {
                Log.d("DateCheck", "✅ Valid rent date (future or today)");

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_confirmation, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();

                Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                btnCancel.setOnClickListener(v1 -> dialog.dismiss());

                btnConfirm.setOnClickListener(v2 -> {
                    dialog.dismiss();

                    loadingDialog.show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String bidId = bidModel.getBidInfo().getBidId();
                    String orderId = bidModel.getOrderInfo().getOrderId();


                    String categoryID = bidModel.getOrderInfo().getCategoryId();
                    String finalBidAmount;
                    if (categoryID.equals(MyUtils.HARVESTER_MACHINE_ID)){
                        // HARVESTER_MACHINE_ID হলে: 1000 + 1%
                        String HarvesterAmount = CommonClass.getRoundedTenPercentValue(bidModel.getBidInfo().getBidAmount(), 1);
                        double calculatedAmount = 1000 + Double.parseDouble(HarvesterAmount);
                        finalBidAmount = String.valueOf(calculatedAmount);
                    }
                    else {
                        finalBidAmount = CommonClass.getRoundedTenPercentValue(bidModel.getBidInfo().getBidAmount(), 10);
                    }

                    // ✅ 1️⃣ bidForOrder -> status update
                    db.collection("bidForOrder")
                            .document(bidId)
                            .update("bidInfo.status", "confirmed")
                            .addOnSuccessListener(aVoid -> {

                                // ✅ 2️⃣ orders -> bidInfo update
                                Map<String, Object> bidInfoUpdate = new HashMap<>();
                                bidInfoUpdate.put("bidInfo.bidId", bidId);
                                bidInfoUpdate.put("bidInfo.vendorId", bidModel.getBidInfo().getVendorId());
                                bidInfoUpdate.put("bidInfo.vendorPrice", Double.valueOf(finalBidAmount));
                                bidInfoUpdate.put("bidInfo.bidStatus", "confirmed");
                                bidInfoUpdate.put("orderInfo.status", "confirmed");

                                db.collection("orders")
                                        .document(orderId)
                                        .update(bidInfoUpdate)
                                        .addOnSuccessListener(unused -> {
                                            loadingDialog.dismiss();
                                            MyToast.showShort(getContext(), "✅ Order confirmed successfully!");
                                            Log.d("ConfirmOrder", "Order & Bid updated successfully.");
                                            loadCurrentOrderBid();
                                            getCurrentOrderInfo();
                                        })
                                        .addOnFailureListener(e -> {
                                            loadingDialog.dismiss();
                                            Log.e("ConfirmOrder", "❌ Failed to update order: " + e.getMessage());
                                            MyToast.showShort(getContext(), "Failed to update order.");
                                        });

                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Log.e("ConfirmOrder", "❌ Failed to update bid: " + e.getMessage());
                                MyToast.showShort(getContext(), "Failed to confirm bid.");
                            });
                });


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);

            }
            else {
                Log.d("DateCheck", "❌ Invalid rent date (past)");
                MyToast.showShort(getContext(), "⚠️ Already Expired this requirement");
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DateCheck", "Error parsing rentTime: " + e.getMessage());
        }
    }


    private void getCurrentOrderInfo() {
        if (orderId == null || orderId.isEmpty()) {
            return;
        }

        // 🔹 Loading শুরু
        preloadingDialog.show();

        db.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // 🔹 Loading শেষ
                    preloadingDialog.dismiss();

                    if (documentSnapshot.exists()) {
                        OrderModel order = documentSnapshot.toObject(OrderModel.class);

                        if (order != null) {

                            // ============ Order Info ============
                            categoryId = order.getOrderInfo().getCategoryId();
                            orderStatus = order.getOrderInfo().getStatus();
                            long timestamp = order.getOrderInfo().getTimestamp();
                            userId = order.getOrderInfo().getUid();
                            vendorId= order.getBidInfo().getVendorId();

                            // ============ Route Info ============
                            String rentArea = order.getRouteInfo().getRentLocation();
                            String rentDateAndTime = order.getRouteInfo().getRentTime();

                            // ============ Specification ============
                            String quantity = order.getSpecInfo().getQuantity();
                            String capacity = order.getSpecInfo().getCapacity();
                            String duration = order.getSpecInfo().getDuration();
                            String types = order.getSpecInfo().getTypes();
                            String postDescription = order.getSpecInfo().getDesc();
                            rentTime = order.getRouteInfo().getRentTime();

                            // ✅ যদি user_type customer হয় এবং orderStatus complete/done হয়
                            if ("customer".equals(user_type) &&
                                    (orderStatus.equalsIgnoreCase("done") || orderStatus.equalsIgnoreCase("complete"))) {

                                // 🔹 রিভিউ ফর্ম লোড করো
                                loadPartnerReview();
                            }

                            // Set icon
                            int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
                            binding.postImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));

                            // Example: binding data
                            binding.orderIdTv.setText(orderId);
                            binding.postNameTv.setText(CommonClass.getSubCategoryName(subCategoryId));
                            binding.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(getContext(), rentDateAndTime));

                            binding.locationNameTv.setText(CommonClass.formatAddress(rentArea).first);
                            binding.locationArea.setText(CommonClass.formatAddress(rentArea).second);

                            binding.durationTv.setText(duration);
                            binding.quantityTv.setText(Replacement.ReplacementQtyToLocal(getContext(), quantity));
                            binding.postDescriptionTv.setText(postDescription);
                            binding.postedDateTv.setText(CommonClass.formatTime(String.valueOf(timestamp), "dd-MMM-yy  hh:mm aa"));

                            //Biding Time CountDown
                            CommonClass.startConditionalCountdown(timestamp, 3, orderStatus,
                                    binding.bidingTimeTv, binding.bidRunTimeLl);


                            binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags,0,0);
                            binding.typesTv.setText(types);
                            binding.capacityTv.setText(capacity);

                        }
                    }
                    else {
                        preloadingDialog.dismiss(); // Ensure hide if no data
                        MyToast.showShort(getContext(),"No order found for ID: " + orderId);
                    }
                })
                .addOnFailureListener(e -> {
                    preloadingDialog.dismiss(); // Hide on failure
                    MyToast.showShort(getContext(),"Error: " + e.getMessage());
                });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void loadPartnerReview() {

        binding.ratingBar.setOnRatingBarChangeListener( (ratingBar, rating, fromUser) -> {
            //Rating BAr Count
            binding.ratingTv.setText(rating+"/5");
        });

        ArrayList<ReviewModel> reviewList = new ArrayList<>();
        ReviewAdapter reviewAdapter = new ReviewAdapter(getContext(), reviewList);
        binding.reviewRv.setAdapter(reviewAdapter);

        db.collection("reviews")
                .whereEqualTo("orderId", orderId)
                .whereEqualTo("customerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    reviewList.clear();
                    if (!snapshot.isEmpty()) {
                        //MyToast.showShort(getContext(), "You have already reviewed this order.");

                        binding.reviewRvCard.setVisibility(View.VISIBLE);


                        //recycleView
                        for (var doc : snapshot.getDocuments()) {
                            ReviewModel model = doc.toObject(ReviewModel.class);
                            if (model != null) reviewList.add(model);
                        }
                    }

                    reviewAdapter.notifyDataSetChanged();

                    if (reviewList.isEmpty()) {
                        binding.reviewCard.setVisibility(View.VISIBLE);
                        binding.reviewRvCard.setVisibility(View.GONE);
                    }
                    else {
                        binding.reviewCard.setVisibility(View.GONE);
                        binding.reviewRvCard.setVisibility(View.VISIBLE);
                    }

                });


        binding.reviewSubmit.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating();
            String review = binding.customerReviewEt.getText().toString().trim();

            if (rating == 0) {
                MyToast.showShort(getContext(), "Please give a rating");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            // 🔹 Generate unique reviewId
            String reviewId = db.collection("reviews").document().getId();

            Map<String, Object> data = new HashMap<>();
            data.put("reviewId", reviewId);
            data.put("vendorId", vendorId);            // Partner ID
            data.put("customerId", currentUserId);     // Customer ID
            data.put("orderId", orderId);
            data.put("rating", rating);
            data.put("review", review);
            data.put("createdAt", System.currentTimeMillis());

            // 🔹 Save to Firestore with fixed ID
            db.collection("reviews")
                    .document(reviewId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        binding.progressBar.setVisibility(View.GONE);
                        MyToast.showShort(getContext(), "Review submitted ✅");
                        loadPartnerReview();

                        // Optional: clear input fields
                        binding.customerReviewEt.setText("");
                        binding.ratingBar.setRating(0);

                        binding.reviewCard.setVisibility(View.GONE);
                        binding.reviewRvCard.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        MyToast.showShort(getContext(), "Failed: " + e.getMessage());
                    });
        });
    }


    //Partner Part
    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentPartnerBid() {

        bidModelArrayList = new ArrayList<>();
        bidPartnerAdapter = new BidPartnerAdapter(getContext(), bidModelArrayList);

        // ✅ অবশ্যই LayoutManager সেট করতে হবে
        binding.bidRV.setAdapter(bidPartnerAdapter);

        db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId)        // Filter by orderId
                .whereEqualTo("bidInfo.vendorId", currentUserId)   // Filter by current vendor
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    bidModelArrayList.clear(); // clear before adding

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            BidModel bidModel = doc.toObject(BidModel.class);
                            if (bidModel != null) {
                                bidModelArrayList.add(bidModel);
                            }
                        }

                        bidPartnerAdapter.notifyDataSetChanged();

                        // 🔹 RecyclerView visible, hide "bottomPart" (bid input section)
                        binding.bidRV.setVisibility(View.VISIBLE);
                        binding.bottomPart.setVisibility(View.GONE);

                    } else {
                        // 🔹 কোনো bid নাই → নতুন bid create করার সুযোগ দেখাও
                        binding.bidRV.setVisibility(View.GONE);
                        binding.bottomPart.setVisibility(View.VISIBLE);
                    }

                })
                .addOnFailureListener(e -> {
                    binding.bidRV.setVisibility(View.GONE);
                    binding.bottomPart.setVisibility(View.VISIBLE);
                    Log.e("BidLoad", "❌ Error loading bid: " + e.getMessage());
                });
    }

    private void loadPartnerInfo( ) {

        serviceModelArrayList = new ArrayList<>();


        db.collection("users")
                .document(currentUserId)
                .collection("services")
                .whereEqualTo("serviceStatus", "active")
                .whereEqualTo("serviceVerified", "verified")
                .whereEqualTo("subCategoryId", subCategoryId) // trim() ব্যবহার করা হলো
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    Log.d("PartnerInfo", "✅ Query success, size: " + querySnapshots.size());

                    if (querySnapshots.isEmpty()) {
                        Log.w("PartnerInfo", "⚠️ No matching services found!");
                        //MyToast.showShort(getContext(), "কোনো verified সার্ভিস পাওয়া যায়নি!");
                        return;
                    }

                    serviceModelArrayList.clear();
                    for (DocumentSnapshot doc : querySnapshots) {
                        ServiceModel model = doc.toObject(ServiceModel.class);
                        if (model != null) {
                            serviceModelArrayList.add(model);
                            Log.d("PartnerInfo", "📄 Added: " + model.getSubCategoryName() + " | " + model.getSubCategoryId());
                        }
                    }

                    Log.d("PartnerInfo", "✅ Final List Size: " + serviceModelArrayList.size());

                })
                .addOnFailureListener(e -> {
                    Log.e("PartnerInfo", "❌ Error: " + e.getMessage());
                    //MyToast.showShort(getContext(), "Error: " + e.getMessage());
                });
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void setupServicePicker() {
        binding.selectVehicleNameTv.setOnClickListener(v -> {
            if (serviceModelArrayList.isEmpty()) {
                MyToast.showShort(getContext(), "কোনো Verified Vehicle পাওয়া যায়নি!");
                return;
            }

            String[] vehicleItems = new String[serviceModelArrayList.size()];
            for (int i = 0; i < serviceModelArrayList.size(); i++) {
                ServiceModel s = serviceModelArrayList.get(i);
                vehicleItems[i] = s.getServiceRegistrationNumber()  +" "+ s.getSubCategoryName()+ " (" +
                        s.getServiceCategoryAndYear() + ") - " +
                        s.getServiceModelNumber() ;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.custom_title_dialog, null))

                    .setItems(vehicleItems, (dialog, which) -> {
                        selectedServiceModel = serviceModelArrayList.get(which); // ✅ সিলেক্টেড সার্ভিস সেট
                        binding.selectVehicleNameTv.setText(selectedServiceModel.getServiceModelNumber()+", "+selectedServiceModel.getServiceCategoryAndYear());
                        Log.d("BidTransportFragment", "✅ Selected Vehicle: " +
                                selectedServiceModel.getServiceModelNumber());
                    })
                    .show();
        });
    }

    private void dataUploadInDatabase(ServiceModel model) {
        loadingDialog.setMessage("অর্ডার সাবমিট হচ্ছে...");
        loadingDialog.show();


        String bidAmount = binding.amountEt.getText().toString().trim();
        String modelName = model.getServiceModelNumber();
        String licenceNumber = model.getServiceRegistrationNumber();
        String modelYear = model.getServiceCategoryAndYear();

        String timestamp = ""+System.currentTimeMillis();

        Map<String, Object> bid = new HashMap<>();

        // 🔹 service Info
        Map<String, Object> serviceInfo = new HashMap<>();
        serviceInfo.put("vehicleModel", modelName);
        serviceInfo.put("vehicleRegNo", licenceNumber);
        serviceInfo.put("vehicleCatAndYear", modelYear);

        // 🔹 bid Info
        Map<String, Object> bidInfo = new HashMap<>();
        bidInfo.put("bidId", timestamp);
        bidInfo.put("status", "pending");
        bidInfo.put("bidAmount", bidAmount);
        bidInfo.put("userId", userId);
        bidInfo.put("vendorId", currentUserId);
        bidInfo.put("timestamp", timestamp);

        // 🔹 order Info
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", orderId);
        orderInfo.put("rentTime", rentTime);
        orderInfo.put("categoryId", categoryId);
        orderInfo.put("subCategoryId", subCategoryId);

        bid.put("serviceInfo", serviceInfo);
        bid.put("bidInfo", bidInfo);
        bid.put("orderInfo", orderInfo);

        db.collection("bidForOrder")
                .document(timestamp)
                .set(bid)
                .addOnSuccessListener(aVoid->{
                    //Success
                    loadingDialog.dismiss();
                    binding.bottomPart.setVisibility(View.GONE);

                    loadCurrentPartnerBid();
                })
                .addOnFailureListener(e->{
                    // Failed
                    loadingDialog.dismiss();
                    Log.d("PartnerInfo", "bidUpload: "+e.getMessage());
                    MyToast.showShort(getContext(), "❌ Error: " + e.getMessage());
                });


    }
}