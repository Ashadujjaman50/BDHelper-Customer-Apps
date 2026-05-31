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
import com.krishibarirangpur.bdhelper.databinding.FragmentBidTransportBinding;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.model.ReviewModel;
import com.krishibarirangpur.bdhelper.model.ServiceModel;
import com.krishibarirangpur.bdhelper.sharedActivity.RatingReviewActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.firebase.BidMapBuilder;
import com.krishibarirangpur.bdhelper.utils.partner.BidActionManager;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerAlertDialog;
import com.krishibarirangpur.bdhelper.utils.partner.bidPositionAndCount;
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

public class BidTransportFragment extends Fragment implements BidCustomerAdapter.OnBidActionListener, BidPartnerAdapter.BidPartnerListener {

   private FragmentBidTransportBinding binding;

    private String bidAction, currentUserId, userId, orderId, rentTime, categoryId, subCategoryId, user_type, orderStatus, vendorId;
    private long orderTimestamp = 0;
    private boolean hasCurrentPartnerBidded = false;
    private ListenerRegistration orderListener;

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


    public BidTransportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_type = getArguments().getString(MyUtils.USER_TYPE);
            orderId = getArguments().getString(MyUtils.orderId);
            subCategoryId = getArguments().getString(MyUtils.subCategoryId);

            // bidAction রিসিভ করা (যদি না থাকে তবে এটি null হবে)
            bidAction = getArguments().getString(MyUtils.bidAction);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bid_transport, container, false);
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

        if (MyUtils.PARTNER.equals(user_type)){
            //Rating Review Card show (Customer)
            binding.ratingReviewCard.setVisibility(View.VISIBLE);

            //Load Current Partner Bid
            loadCurrentPartnerBid();

            //loadPartner ServiceInfo
            loadPartnerInfo(subCategoryId);

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
                else if (ValidationClass.validateField(binding.amountEt)) return;
                else bidDataSubmitInDatabase(selectedServiceModel);
            });
        }
        else if (MyUtils.CUSTOMER.equals(user_type)) {
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

                            // ============ Order Info ============
                            categoryId = order.getOrderInfo().getCategoryId();
                            orderStatus = order.getOrderInfo().getStatus();
                            orderTimestamp = order.getOrderInfo().getTimestamp();
                            userId = order.getOrderInfo().getUid();
                            vendorId= order.getBidInfo().getVendorId();

                            // ============ Route Info ============
                            String loadLocation = order.getRouteInfo().getLoad();
                            String unLoadLocation = order.getRouteInfo().getUnload();
                            String rentDateAndTime = order.getRouteInfo().getRentTime();

                            // ============ Specification ============
                            String quantity = order.getSpecInfo().getQuantity();
                            String capacity = order.getSpecInfo().getCapacity();
                            String duration = order.getSpecInfo().getDuration();
                            String types = order.getSpecInfo().getTypes();
                            String postDescription = order.getSpecInfo().getDesc();
                            rentTime = order.getRouteInfo().getRentTime();

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
                                CommonClass.getUserOrderCount(targetUserId, new CommonClass.OnCountListener() {
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
                                    intent.putExtra(MyUtils.userId, targetUserId);
                                    intent.putExtra(MyUtils.USER_TYPE, MyUtils.CUSTOMER);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(0, 0);
                                });
                            }

                            // Set icon
                            int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
                            binding.postImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));

                            // Example: binding data
                            binding.orderIdTv.setText(orderId);
                            binding.postNameTv.setText(CommonClass.getSubCategoryName(requireContext(), subCategoryId));
                            binding.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(getContext(), rentDateAndTime));

                            // UIHelper update in: 06-05-2026
                            UIHelper.bindAddress(binding.postLoadLocation,   binding.loadArea,   loadLocation);
                            UIHelper.bindAddress(binding.postUnLoadLocation, binding.unLoadArea, unLoadLocation);

                            binding.durationTv.setText(duration);
                            binding.quantityTv.setText(Replacement.ReplacementQtyToLocal(getContext(), quantity));
                            binding.postDescriptionTv.setText(postDescription);
                            binding.postedDateTv.setText(CommonClass.formatTime(String.valueOf(orderTimestamp), "dd-MMM-yy  hh:mm aa"));

                            // Refresh Visibility logic
                            refreshCountdown();

                            if (subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)) {
                                binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags, 0, 0);
                                binding.typesTv.setText(capacity);
                                binding.capacityTv.setText(types);
                            } else if (categoryId.equals(MyUtils.RENT_A_CAR_ID)) {
                                if (subCategoryId.equals(MyUtils.SUB_CAR_ID)) {
                                    binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_road_route_map, 0, 0);
                                } else {
                                    binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags, 0, 0);
                                }
                                binding.typesTv.setText(types);
                                binding.capacityTv.setText(capacity);
                            } else if (subCategoryId.equals(MyUtils.SUB_CHARGER_VAN_ID)) {
                                binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags, 0, 0);
                                binding.typesTv.setText(types);
                                binding.capacityTv.setText(capacity);
                            } else {
                                binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_parcel, 0, 0);
                                binding.typesTv.setText(types);
                                binding.capacityTv.setText(capacity);
                            }

                        }
                    } else {
                        if (isAdded()) preloadingDialog.dismiss();
                        MyToast.showShort(getContext(), "No order found for ID: " + orderId);
                    }
                });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentOrderBid() {
        bidModelArrayList = new ArrayList<>();
        bidCustomerAdapter = new BidCustomerAdapter(getContext(), bidModelArrayList, "",this);
        binding.bidRV.setAdapter(bidCustomerAdapter);

        db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId) // Filter by orderId
                .orderBy("bidInfo.bidAmount", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error) -> {

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
                                String status = bidModel.getBidInfo().getStatus();
                                if ("confirmed".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status)) {
                                    // যদি confirmed বা done bid থাকে, শুধু সেটা রাখো
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

    //Partner Part
    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentPartnerBid() {
        bidModelArrayList = new ArrayList<>();
        bidPartnerAdapter = new BidPartnerAdapter(getContext(), bidModelArrayList, "");
        bidPartnerAdapter.setListener(this); // Set listener!
        binding.bidRV.setAdapter(bidPartnerAdapter);

        db.collection("bidForOrder")
                .whereEqualTo("orderInfo.orderId", orderId)
                .whereEqualTo("bidInfo.vendorId", currentUserId)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("BidLoad", "❌ Error loading bid: " + error.getMessage());
                        return;
                    }

                    bidModelArrayList.clear();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        hasCurrentPartnerBidded = true;
                        for (DocumentSnapshot doc : querySnapshot) {
                            BidModel bidModel = doc.toObject(BidModel.class);
                            if (bidModel != null) {
                                bidModelArrayList.add(bidModel);
                            }
                        }
                        bidPartnerAdapter.notifyDataSetChanged();
                        binding.bidRV.setVisibility(View.VISIBLE);

                        //call Bid Position
                        if (!bidModelArrayList.isEmpty()) {

                            BidModel model = bidModelArrayList.get(0);

                            String bidId = model.getBidInfo().getBidId();
                            String orderId = model.getOrderInfo().getOrderId();

                            binding.bidPositionRl.setVisibility(View.VISIBLE);

                            callBidPositionAndCount(bidId, orderId);
                        }
                    } else {
                        hasCurrentPartnerBidded = false;
                        binding.bidRV.setVisibility(View.GONE);
                    }
                    refreshCountdown();
                });


    }

    ListenerRegistration bidListener;
    private void callBidPositionAndCount(String bidId, String orderId) {
        // লিসেনার শুরু করতে
        bidListener = bidPositionAndCount.getBidStatsRealtime(orderId, bidId, new bidPositionAndCount.BidStatsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResult(int position, int totalBids) {
                // এখানে ডাটা আপডেট হলে সাথে সাথে কল হবে
                binding.tvTotalBids.setText(Replacement.ReplacementNumberInLocal(getContext(), ""+totalBids)+" টি" );
                binding.tvRank.setText("# "+Replacement.ReplacementNumberInLocal(getContext(), ""+position));
            }

            @Override
            public void onError(Exception e) {
                // এরর হ্যান্ডেল করুন
            }
        });


    }

    private void loadPartnerInfo(String targetSubCategoryId) {

        serviceModelArrayList = new ArrayList<>();


        db.collection("users")
                .document(currentUserId)
                .collection("services")
                .whereEqualTo("serviceStatus", "active")
                .whereEqualTo("serviceVerified", "verified")
                .whereEqualTo("subCategoryId", targetSubCategoryId.trim()) // trim() ব্যবহার করা হলো
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    //Log.d("PartnerInfo", "✅ Query success, size: " + querySnapshots.size());

                    if (querySnapshots.isEmpty()) {
                        //Log.w("PartnerInfo", "⚠️ No matching services found!");
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

                    //Log.d("PartnerInfo", "✅ Final List Size: " + serviceModelArrayList.size());

                })
                .addOnFailureListener(e -> {
                    Log.e("PartnerInfo", "❌ Error: " + e.getMessage());
                    //MyToast.showShort(getContext(), "Error: " + e.getMessage());
                });
    }

    @SuppressLint("InflateParams")
    private void setupServicePicker() {
        binding.selectVehicleNameTv.setOnClickListener(v -> {
            if (serviceModelArrayList.isEmpty()) {
                MyToast.showShort(getContext(), "কোনো Verified Vehicle পাওয়া যায়নি!");
                return;
            }

            String[] vehicleItems = new String[serviceModelArrayList.size()];
            for (int i = 0; i < serviceModelArrayList.size(); i++) {
                ServiceModel s = serviceModelArrayList.get(i);
                vehicleItems[i] = s.getServiceModelNumber() + " (" +
                        s.getServiceCategoryAndYear() + ") - " +
                        s.getServiceRegistrationNumber();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.custom_title_dialog, null))

                    .setItems(vehicleItems, (dialog, which) -> {
                        selectedServiceModel = serviceModelArrayList.get(which); // ✅ সিলেক্টেড সার্ভিস সেট
                        binding.selectVehicleNameTv.setText(selectedServiceModel.getServiceModelNumber());
                        Log.d("BidTransportFragment", "✅ Selected Vehicle: " +
                                selectedServiceModel.getServiceModelNumber());
                    })
                    .show();
        });
    }

    private void bidDataSubmitInDatabase(ServiceModel model) {
        loadingDialog.setMessage("বিড সাবমিট হচ্ছে...");
        loadingDialog.show();

        String getBidAmount = binding.amountEt.getText().toString().trim();
        String bidAmount = Replacement.ReplacementNumberBnToEn(getBidAmount);
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

        db.collection("bidForOrder")
                .document(timestamp)
                .set(bid)
                .addOnSuccessListener(aVoid->{
                    //Success
                    loadingDialog.dismiss();
                    hasCurrentPartnerBidded = true;
                    refreshCountdown();

                    //clear edit Text Field
                    binding.amountEt.setText("");
                    binding.selectVehicleNameTv.setText("");

                    // বিড সাবমিট করার পর
                    //Custome Notice Send
                    String finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_TRANSPORT);
                    BidActionManager.sendNotice(getContext(), user_type, userId, currentUserId, orderId, subCategoryId, finalBidAmount, MyUtils.NOTICE_TYPE_BID);
                    new PartnerAlertDialog.BidSummary(getContext(), bidAmount, finalBidAmount).show();

                    loadCurrentPartnerBid();
                })
                .addOnFailureListener(e->{
                   // Failed
                    loadingDialog.dismiss();
                    Log.d("PartnerInfo", "bidUpload: "+e.getMessage());
                    MyToast.showShort(getContext(), "❌ Error: " + e.getMessage());
                });


    }


    // 🔹 Handle Call Button Click
    @Override
    public void onCallClicked(BidModel bidModel) {
        BidActionManager.handleCall(getContext(), bidModel);
    }

    public void onConfirmOrderClicked(BidModel bidModel) {
        BidActionManager.confirmOrder(requireContext(), bidModel, user_type, "",
                PartnerCommissionUtils.COMMISSION_TRANSPORT, loadingDialog, () -> {
                    getCurrentOrderInfo(); // সাকসেস হলে ডেটা রিফ্রেশ
                });
    }


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
        if (bidListener != null) {
            bidListener.remove(); // লিসেনার বন্ধ করা
        }
    }


}
