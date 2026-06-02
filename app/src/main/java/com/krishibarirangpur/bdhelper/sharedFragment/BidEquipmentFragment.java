package com.krishibarirangpur.bdhelper.sharedFragment;

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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.BidCustomerAdapter;
import com.krishibarirangpur.bdhelper.adapter.partner.BidPartnerAdapter;
import com.krishibarirangpur.bdhelper.adapter.ReviewAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentBidEquipmentBinding;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.model.ReviewModel;
import com.krishibarirangpur.bdhelper.model.ServiceModel;
import com.krishibarirangpur.bdhelper.sharedActivity.RatingReviewActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.firebase.BidMapBuilder;
import com.krishibarirangpur.bdhelper.utils.partner.BidActionManager;
import com.krishibarirangpur.bdhelper.utils.partner.BidPositionAndCount;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerAlertDialog;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.NoticeSend;
import com.krishibarirangpur.bdhelper.utils.core.PreloadingDialog;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.utils.partner.DueWarningAlertDialog;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerBidEdit;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerCommissionUtils;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.UIHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BidEquipmentFragment extends Fragment implements BidCustomerAdapter.OnBidActionListener, BidPartnerAdapter.BidPartnerListener{

    private FragmentBidEquipmentBinding binding;

    private String currentUserId, userId, orderId, rentTime, categoryId, subCategoryId, user_type, landArea, orderStatus, vendorId;
    private long orderTimestamp = 0;
    private boolean hasCurrentPartnerBidded = false;
    private ListenerRegistration orderListener;
    private ListenerRegistration bidListener;

    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    ArrayList<ServiceModel> serviceModelArrayList;
    ServiceModel selectedServiceModel; // ✅ সিলেক্টেড সার্ভিস রাখবে এখানে

    ArrayList<BidModel> bidModelArrayList;
    BidPartnerAdapter bidPartnerAdapter;
    BidCustomerAdapter bidCustomerAdapter;

    LoadingDialog loadingDialog;
    PreloadingDialog preloadingDialog;
    private PartnerBidEdit partnerBidEdit;


    public BidEquipmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_type = getArguments().getString(MyUtils.USER_TYPE);
            orderId = getArguments().getString(MyUtils.orderId);
            subCategoryId = getArguments().getString(MyUtils.subCategoryId);
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
        partnerBidEdit = new PartnerBidEdit(requireContext(), db, loadingDialog);


        //get current Order info
        getCurrentOrderInfo();

        if (user_type.equals(MyUtils.PARTNER)){
            //Rating Review Card show (Customer)
            binding.ratingReviewCard.setVisibility(View.VISIBLE);

            //Load Current Partner Bid
            loadCurrentPartnerBid();

            //loadPartner ServiceInfo
            loadPartnerInfo();

            // Setup vehicle picker dialog
            setupServicePicker();

            binding.amountEt.addTextChangedListener(new TextWatcher() {
                boolean isEditing;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isEditing) return;

                    isEditing = true;

                    String original = s.toString();
                    String converted = Replacement.ReplacementNumberInLocal(getContext(), original);

                    binding.amountEt.setText(converted);
                    binding.amountEt.setSelection(converted.length());

                    isEditing = false;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // ✅ submit button click
            binding.bidSubmitBtn.setOnClickListener(v -> {
                if (ValidationClass.validateField(binding.selectVehicleNameTv)) return;
                if ((subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID)
                        || subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)) && ValidationClass.validateField(binding.inputAmountEt)) return;
                else if (ValidationClass.validateField(binding.amountEt)) return;

                // সব ঠিক থাকলে submit
                bidDataSubmitInDatabase(selectedServiceModel);
            });
        }
        else if (user_type.equals(MyUtils.CUSTOMER)) {
            //Rating Review Card show (Customer)
            binding.ratingReviewCard.setVisibility(View.GONE);
            binding.bottomPart.setVisibility(View.GONE);

            binding.bidRunMsgTv.setText("বিডিং চলছে");
            binding.bidMsgTv.setVisibility(View.VISIBLE);
            binding.bidRV.setVisibility(View.VISIBLE);

            //loadCurrent order bid
            loadCurrentOrderBid();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getCurrentOrderInfo() {
        if (orderId == null || orderId.isEmpty()) return;

        if (orderTimestamp == 0) preloadingDialog.show();

        orderListener = db.collection("orders")
                .document(orderId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (isAdded()) preloadingDialog.dismiss();

                    if (e != null) {
                        Log.e("BidTransport", "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        OrderModel order = documentSnapshot.toObject(OrderModel.class);
                        if (order != null) {
                            categoryId = order.getOrderInfo().getCategoryId();
                            orderStatus = order.getOrderInfo().getStatus();
                            userId = order.getOrderInfo().getUid();
                            vendorId = order.getBidInfo().getVendorId();
                            landArea = order.getSpecInfo().getLandArea();
                            rentTime = order.getRouteInfo().getRentTime();
                            orderTimestamp = order.getOrderInfo().getTimestamp();

                            // ✅ যদি orderStatus complete/done হয়, রিভিউ সেকশন লোড করো
                            if (orderStatus.equalsIgnoreCase("done") || orderStatus.equalsIgnoreCase("complete")) {
                                loadReviewSection();
                            }


                            String targetField;
                            String targetUserId;
                            String targetReviewer;

                            if (MyUtils.PARTNER.equals(user_type)) {

                                // এখন customer profile open করা হয়েছে
                                // তাই customer কে যেসব partner review দিয়েছে সেগুলো দেখাবে

                                targetField = "customerId";
                                targetUserId = userId;
                                targetReviewer = MyUtils.PARTNER;

                            } else {

                                // এখন partner profile open করা হয়েছে
                                // তাই partner কে যেসব customer review দিয়েছে সেগুলো দেখাবে

                                targetField = "vendorId";
                                targetUserId = vendorId;
                                targetReviewer = MyUtils.CUSTOMER;
                            }

                            CommonClass.getUserRatingInfo(
                                    targetField,
                                    targetUserId,
                                    targetReviewer,
                                    (averageRating, totalReviews) -> {

                                        binding.averageRatingTv.setText(
                                                String.format(Locale.getDefault(), "%.1f", averageRating));

                                        binding.reviewCountTv.setText(
                                                "(" + totalReviews + ")");
                                    });

                            if (MyUtils.PARTNER.equals(user_type)){
                                //Count Total Trip in orderList
                                CommonClass.getUserOrderCount(userId, new CommonClass.OnCountListener() {
                                    @Override
                                    public void onSuccess(int count) {
                                        if (isAdded() && binding != null) {
                                            // সংখ্যাটিকে লোকাল ল্যাঙ্গুয়েজে (বাংলা/ইংরেজি) রূপান্তর করে দেখানো
                                            binding.totalTripTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(count)));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        if (isAdded() && binding != null) {
                                            binding.totalTripTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                                        }
                                    }
                                });

                                binding.detailsTv.setOnClickListener(v -> {
                                    Intent intent = new Intent(getContext(), RatingReviewActivity.class);
                                    intent.putExtra(MyUtils.userId, userId);
                                    intent.putExtra(MyUtils.USER_TYPE, user_type);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(0, 0);
                                });
                            }

                            // UI setup
                            setupOrderUI(order, orderTimestamp);
                        }
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentOrderBid() {
        bidModelArrayList = new ArrayList<>();
        // 🔹 landArea পাস করা হচ্ছে অ্যাডাপ্টারে
        bidCustomerAdapter = new BidCustomerAdapter(getContext(), bidModelArrayList, landArea, this);
        binding.bidRV.setAdapter(bidCustomerAdapter);

        db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId)
                .orderBy("bidInfo.bidAmount", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error)->{

                    if (!isAdded()) return;

                    if (error != null) {
                        Log.d("Firestore", "loadAllData: " + error.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        bidModelArrayList.clear();

                        BidModel confirmedBid = null;

                        for (DocumentSnapshot doc : querySnapshot) {
                            BidModel bidModel = doc.toObject(BidModel.class);
                            if (bidModel != null) {
                                String status = bidModel.getBidInfo().getStatus();
                                if ("confirmed".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status)) {
                                    confirmedBid = bidModel;
                                    break;
                                }
                                else {
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

    @SuppressLint("SetTextI18n")
    private void setupOrderUI(OrderModel order, long timestamp) {
        if (!isAdded() || getContext() == null) return;

        String landAreaConvert = landArea != null ? Replacement.ReplacementNumberInLocal(getContext(), landArea) : getContext().getString(R.string.zero);
        if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID) || subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID)){
            binding.landAreaLL.setVisibility(View.VISIBLE);
            binding.dividerOne.setVisibility(View.VISIBLE);
            binding.landAreaTv.setText(landAreaConvert + " " + getContext().getString(R.string.acres));
        }

        int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
        binding.postImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));

        binding.orderIdTv.setText(orderId);
        binding.postNameTv.setText(CommonClass.getSubCategoryName(requireContext(), subCategoryId));
        binding.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(getContext(), order.getRouteInfo().getRentTime()));
        binding.durationTv.setText(order.getSpecInfo().getDuration());
        binding.quantityTv.setText(Replacement.ReplacementQtyToLocal(getContext(), order.getSpecInfo().getQuantity()));
        binding.postDescriptionTv.setText(order.getSpecInfo().getDesc());
        binding.postedDateTv.setText(CommonClass.formatTime(String.valueOf(timestamp), "dd-MMM-yy  hh:mm aa"));

        // UIHelper update in: 06-05-2026
        UIHelper.bindAddress(binding.locationNameTv, binding.locationArea, order.getRouteInfo().getRentLocation());

        // Refresh Visibility logic
        refreshCountdown();

        binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags, 0, 0);
        binding.typesTv.setText(order.getSpecInfo().getTypes());
        binding.capacityTv.setText(order.getSpecInfo().getCapacity());

        if (subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID) || subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)) {
            binding.warningTv.setVisibility(View.VISIBLE);
            binding.landAreaCalLL.setVisibility(View.VISIBLE);
            binding.landAreaCalTv.setText(landAreaConvert + " " + getContext().getString(R.string.acres));
            binding.amountEt.setHint(getString(R.string.total_price));
            binding.amountEt.setEnabled(false);

            binding.inputAmountEt.addTextChangedListener(new TextWatcher() {
                boolean isEditing;
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isEditing) return;
                    isEditing = true;
                    String localNumber = Replacement.ReplacementNumberInLocal(getContext(), s.toString());
                    binding.inputAmountEt.setText(localNumber);
                    binding.inputAmountEt.setSelection(localNumber.length());
                    isEditing = false;
                }
                @Override public void afterTextChanged(Editable s) {
                    calculateTotalPrice(s.toString());
                }
            });
        } else {
            binding.amountEt.addTextChangedListener(new TextWatcher() {
                boolean isEditing;
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isEditing) return;
                    isEditing = true;
                    String converted = Replacement.ReplacementNumberInLocal(getContext(), s.toString());
                    binding.amountEt.setText(converted);
                    binding.amountEt.setSelection(converted.length());
                    isEditing = false;
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void calculateTotalPrice(String inputStr) {
        if (!isAdded() || inputStr.isEmpty()) {
            binding.amountEt.setText("");
            return;
        }
        try {
            String englishInput = Replacement.ReplacementNumberBnToEn(inputStr);
            double total = Double.parseDouble(englishInput) * Double.parseDouble(landArea);
            binding.amountEt.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(total)));
        } catch (Exception e) {
            binding.amountEt.setText("");
        }
    }

    private void refreshCountdown() {
        if (orderTimestamp == 0 || !isAdded()) return;        // যদি ইউজার পার্টনার হয় এবং সে এখনও বিড না করে থাকে
        if ("partner".equals(user_type) && !hasCurrentPartnerBidded) {
            // কাউন্টডাউন মেথডকে bottomPart পাস করা হলো যাতে সময় থাকলে এটি দেখায়
            CommonClass.startConditionalCountdown(orderTimestamp, 3, orderStatus,
                    binding.bidingTimeTv, binding.bidRunTimeLl, binding.bottomPart);
        } else {
            // অন্য সব ক্ষেত্রে (কাস্টমার বা বিড করা পার্টনার) bottomPart লুকানো থাকবে
            binding.bottomPart.setVisibility(View.GONE);
            // কাউন্টডাউন মেথডকে null পাস করা হলো যাতে এটি bottomPart-কে VISIBLE করতে না পারে
            CommonClass.startConditionalCountdown(orderTimestamp, 3, orderStatus,
                    binding.bidingTimeTv, binding.bidRunTimeLl, null);
        }
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void loadReviewSection() {

        binding.ratingBar.setOnRatingBarChangeListener( (ratingBar, rating, fromUser) -> {
            //Rating BAr Count
            binding.ratingTv.setText(rating+"/5");
        });

        ArrayList<ReviewModel> reviewList = new ArrayList<>();
        ReviewAdapter reviewAdapter = new ReviewAdapter(getContext(), reviewList);
        binding.reviewRv.setAdapter(reviewAdapter);

        db.collection("reviews")
                .whereEqualTo("orderId", orderId)
                .whereEqualTo("reviewerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    reviewList.clear();
                    if (!snapshot.isEmpty()) {
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
            data.put("orderId", orderId);
            data.put("rating", rating);
            data.put("review", review);
            data.put("createdAt", System.currentTimeMillis());
            data.put("reviewerId", currentUserId);

            // সঠিকভাবে ID গুলো সেট করা
            if (MyUtils.PARTNER.equals(user_type)) {
                data.put("vendorId", currentUserId); // আমি পার্টনার
                data.put("customerId", userId);      // সে কাস্টমার
                data.put("reviewer", MyUtils.PARTNER);
            } else {
                data.put("vendorId", vendorId);      // সে পার্টনার
                data.put("customerId", currentUserId); // আমি কাস্টমার
                data.put("reviewer", MyUtils.CUSTOMER);
            }

            // 🔹 Save to Firestore with fixed ID
            db.collection("reviews")
                    .document(reviewId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        binding.progressBar.setVisibility(View.GONE);
                        MyToast.showShort(getContext(), "Review submitted ✅");
                        loadReviewSection();
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


    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentPartnerBid() {
        if (!isAdded()) return;
        
        bidModelArrayList = new ArrayList<>();
        bidPartnerAdapter = new BidPartnerAdapter(getContext(), bidModelArrayList, landArea);
        bidPartnerAdapter.setListener(this);
        binding.bidRV.setAdapter(bidPartnerAdapter);

        db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId)
                .whereEqualTo("bidInfo.vendorId", currentUserId)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (!isAdded()) return;
                    
                    if (error != null) return;
                    bidModelArrayList.clear();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        hasCurrentPartnerBidded = true;
                        for (DocumentSnapshot doc : querySnapshot) {
                            BidModel bidModel = doc.toObject(BidModel.class);
                            if (bidModel != null) bidModelArrayList.add(bidModel);
                        }
                        bidPartnerAdapter.notifyDataSetChanged();
                        binding.bidRV.setVisibility(View.VISIBLE);

                        //call Bid Position
                        if (!bidModelArrayList.isEmpty()) {

                            BidModel model = bidModelArrayList.get(0);

                            String bidId = model.getBidInfo().getBidId();
                            String orderId = model.getOrderInfo().getOrderId();

                            binding.bidPositionRl.setVisibility(View.VISIBLE);

                            if (bidListener != null) bidListener.remove();
                            bidListener = BidPositionAndCount.bindBidStatsToUI(getContext(), orderId, bidId, binding.tvTotalBids, binding.tvRank);
                        }

                    } else {
                        hasCurrentPartnerBidded = false;
                        binding.bidRV.setVisibility(View.GONE);
                    }
                    refreshCountdown();
                });
    }

    private void loadPartnerInfo() {
        if (!isAdded()) return;
        
        serviceModelArrayList = new ArrayList<>();
        db.collection("users").document(currentUserId).collection("services")
                .whereEqualTo("serviceStatus", "active")
                .whereEqualTo("serviceVerified", "verified")
                .whereEqualTo("subCategoryId", subCategoryId)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    if (!isAdded()) return;
                    
                    serviceModelArrayList.clear();
                    for (DocumentSnapshot doc : querySnapshots) {
                        ServiceModel model = doc.toObject(ServiceModel.class);
                        if (model != null) serviceModelArrayList.add(model);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setupServicePicker() {
        binding.selectVehicleNameTv.setOnClickListener(v -> {
            if (!isAdded() || serviceModelArrayList.isEmpty()) {
                MyToast.showShort(getContext(), "কোনো Verified Vehicle পাওয়া যায়নি!");
                return;
            }
            String[] vehicleItems = new String[serviceModelArrayList.size()];
            for (int i = 0; i < serviceModelArrayList.size(); i++) {
                ServiceModel s = serviceModelArrayList.get(i);
                vehicleItems[i] = s.getServiceRegistrationNumber()  +" "+
                        s.getSubCategoryName()+ " (" +
                        s.getServiceCategoryAndYear() + ") - " +
                        s.getServiceModelNumber();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.custom_title_dialog, null))

                    .setItems(vehicleItems, (dialog, which) -> {
                        selectedServiceModel = serviceModelArrayList.get(which);
                        binding.selectVehicleNameTv.setText(selectedServiceModel.getServiceModelNumber()+", "+selectedServiceModel.getServiceCategoryAndYear());
                    }).show();
        });
    }

    private void bidDataSubmitInDatabase(ServiceModel model) {
        if (!isAdded()) return;
        
        loadingDialog.setMessage("বিড সাবমিট হচ্ছে...");
        loadingDialog.show();

        String bidAmount = Replacement.ReplacementNumberBnToEn(binding.amountEt.getText().toString().trim());
        String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String, Object> bid = BidMapBuilder.createBidMap(
                model,
                timestamp,
                bidAmount,
                userId,
                currentUserId,
                orderId,
                rentTime,
                categoryId,
                subCategoryId
        );

        db.collection("bidForOrder").document(timestamp).set(bid)
                .addOnSuccessListener(aVoid->{
                    if (!isAdded()) return;
                    
                    loadingDialog.dismiss();
                    hasCurrentPartnerBidded = true;
                    refreshCountdown();

                    // বিড সাবমিট করার পর
                    //Custome Notice Send
                    String finalBidAmount = categoryId.equals(MyUtils.HARVESTER_MACHINE_ID) ? CommonClass.getRoundedCommissionValue(true, bidAmount, landArea) : CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_EQUIPMENT);
                    BidActionManager.sendNotice(getContext(), user_type, userId, currentUserId, orderId, subCategoryId, finalBidAmount, MyUtils.NOTICE_TYPE_BID);
                    new PartnerAlertDialog.BidSummary(getContext(), bidAmount, finalBidAmount).show();

                    loadCurrentPartnerBid();
                });
    }


    @Override
    public void onCallClicked(BidModel bidModel) {
        BidActionManager.handleCall(getContext(), bidModel);
    }

    public void onConfirmOrderClicked(BidModel bidModel) {
        BidActionManager.confirmOrder(requireContext(), bidModel, user_type, landArea,
                PartnerCommissionUtils.COMMISSION_EQUIPMENT, loadingDialog, () -> {
                    getCurrentOrderInfo(); // সাকসেস হলে ডেটা রিফ্রেশ
                });
    }

    @Override
    public void onEditClicked(String bidId, String orderId) {
        partnerBidEdit.startEditProcess(bidId, orderId);
    }

    @Override
    public void onDeleteClicked(String bidId, String orderId) {
        BidActionManager.deleteBid(requireContext(), bidId, loadingDialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (orderListener != null) {
            orderListener.remove();
        }
    }
}
