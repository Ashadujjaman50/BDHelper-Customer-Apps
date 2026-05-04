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
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.firebase.BidMapBuilder;
import com.krishibarirangpur.bdhelper.utils.partner.BidActionManager;
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
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BidTransportFragment extends Fragment implements BidCustomerAdapter.OnBidActionListener, BidPartnerAdapter.BidPartnerListener {

   private FragmentBidTransportBinding binding;

    private String currentUserId, userId, orderId, rentTime, categoryId, subCategoryId, user_type, orderStatus, vendorId;
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
            user_type = getArguments().getString("user_type");
            orderId = getArguments().getString("orderId");
            subCategoryId = getArguments().getString("subCategoryId");
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

        if (user_type.equals("partner")){
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



    private void getCurrentOrderInfo() {
        if (orderId == null || orderId.isEmpty()) {
            return;
        }

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

                            // ✅ যদি user_type customer হয় এবং orderStatus complete/done হয়
                            if ("customer".equals(user_type) &&
                                    (orderStatus.equalsIgnoreCase("done") || orderStatus.equalsIgnoreCase("complete"))) {

                                // 🔹রিভিউ ফর্ম লোড করো
                                loadPartnerReview();
                            }

                            // Set icon
                            int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
                            binding.postImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));

                            // Example: binding data
                            binding.orderIdTv.setText(orderId);
                            binding.postNameTv.setText(CommonClass.getSubCategoryName(requireContext(), subCategoryId));
                            binding.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(getContext(), rentDateAndTime));

                            binding.postLoadLocation.setText(CommonClass.formatAddress(loadLocation).first);
                            binding.loadArea.setText(CommonClass.formatAddress(loadLocation).second);

                            binding.postUnLoadLocation.setText(CommonClass.formatAddress(unLoadLocation).first);
                            binding.unLoadArea.setText(CommonClass.formatAddress(unLoadLocation).second);

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

    private void refreshCountdown() {
        if (orderTimestamp == 0 || !isAdded()) return;

        CommonClass.startConditionalCountdown(orderTimestamp, 3, orderStatus,
                binding.bidingTimeTv, binding.bidRunTimeLl, binding.bottomPart);

        if ("partner".equals(user_type) && hasCurrentPartnerBidded) {
            binding.bottomPart.setVisibility(View.GONE);
        }
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
                    } else {
                        hasCurrentPartnerBidded = false;
                        binding.bidRV.setVisibility(View.GONE);
                    }
                    refreshCountdown();
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


                    //Custome Notice Send
                    String finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_TRANSPORT);
                    //sendCustomNotice(userId, currentUserId, orderId, subCategoryId, finalBidAmount, MyUtils.NOTICE_TYPE_BID);
                    // বিড সাবমিট করার পর
                    BidActionManager.sendNotice(getContext(), user_type, userId, currentUserId, orderId, subCategoryId, finalBidAmount, MyUtils.NOTICE_TYPE_BID);

                    loadCurrentPartnerBid();
                })
                .addOnFailureListener(e->{
                   // Failed
                    loadingDialog.dismiss();
                    Log.d("PartnerInfo", "bidUpload: "+e.getMessage());
                    MyToast.showShort(getContext(), "❌ Error: " + e.getMessage());
                });


    }


    private void sendCustomNotice(String userId, String currentUserId, String orderId, String subCategoryId, String bidAmount, String noticeType) {

        String sender;
        String messageForUser;
        String messageForAdmin;

        if ("partner".equals(user_type)) {
            sender = MyUtils.NOTICE_SENDER_PARTNER;

            String subCatName = CommonClass.getSubCategoryName(requireContext(), subCategoryId);
            String cleanAmount = bidAmount.replace(".0", "");
            String bnAmount = Replacement.ReplacementNumberEnToBn(cleanAmount);

            messageForUser = "একজন " + subCatName + " পার্টনার " + bnAmount + "/= বিড করেছেন।";
            messageForAdmin = messageForUser; // same message

        }
        else {
            sender = MyUtils.NOTICE_SENDER_CUSTOMER;

            messageForUser = "কাস্টমার আপনার করা বিড কনফার্ম করেছেন।";
            messageForAdmin = "অর্ডার " + orderId + " এর বিড কনফার্ম হয়েছে।";
        }

        // 🔹 Send to User
        NoticeSend.sendNotice(
                sender,
                noticeType,
                currentUserId,
                userId,
                orderId,
                messageForUser
        );

        // 🔹 Send to Admin
        NoticeSend.sendNotice(
                sender,
                noticeType,
                currentUserId,
                MyUtils.NOTICE_RECEIVER_ADMIN,
                orderId,
                messageForAdmin
        );
    }


    // 🔹 Handle Call Button Click
    @Override
    public void onCallClicked(BidModel bidModel) {
        BidActionManager.handleCall(getContext(), bidModel);
    }

    /* // 🔹 Handle Confirm Button Click
     private boolean isConfirmDialogShowing = false;

     public void onConfirmOrderClicked(BidModel bidModel) {
         try {
             long rentMillis = CommonClass.parseMillis(bidModel.getOrderInfo().getRentTime());
             long todayMillis = CommonClass.getTodayStartMillis();

             if (rentMillis >= todayMillis) {
                 if (isConfirmDialogShowing) return; // 🔹 prevent multiple dialogs
                 isConfirmDialogShowing = true;

                 AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                 View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_confirmation, null);
                 builder.setView(dialogView);

                 AlertDialog dialog = builder.create();

                 Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                 Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                 btnCancel.setOnClickListener(v1 -> {
                     dialog.dismiss();
                 });

                 btnConfirm.setOnClickListener(v2 -> {
                     dialog.dismiss();
                     //loadingDialog.show();

                     FirebaseFirestore db = FirebaseFirestore.getInstance();
                     String bidId = bidModel.getBidInfo().getBidId();
                     String orderId = bidModel.getOrderInfo().getOrderId();
                     String finalBidAmount = CommonClass.getRoundedTenPercentValue(bidModel.getBidInfo().getBidAmount(), PartnerCommissionUtils.COMMISSION_TRANSPORT);



                     db.collection("bidForOrder")
                             .document(bidId)
                             .update("bidInfo.status", "confirmed")
                             .addOnSuccessListener(aVoid -> {
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

                                             //Send Custom Notice
                                             sendCustomNotice(bidModel.getBidInfo().getVendorId(), bidModel.getBidInfo().getUserId(), orderId,
                                                     bidModel.getOrderInfo().getSubCategoryId(), finalBidAmount, MyUtils.NOTICE_TYPE_BID_CONFIRM);

                                             MyToast.showShort(getContext(), "✅ Order confirmed successfully!");
                                             getCurrentOrderInfo();
                                         })
                                         .addOnFailureListener(e -> {
                                             loadingDialog.dismiss();
                                             MyToast.showShort(getContext(), "Failed to update order.");
                                         });

                             })
                             .addOnFailureListener(e -> {
                                 loadingDialog.dismiss();
                                 MyToast.showShort(getContext(), "Failed to confirm bid.");
                             });
                 });

                 dialog.setOnDismissListener(d -> isConfirmDialogShowing = false); // 🔹 reset flag on dismiss
                 dialog.show();
                 dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                 dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                 dialog.getWindow().setGravity(Gravity.CENTER);

             } else {
                 MyToast.showShort(getContext(), "⚠️ Already Expired this requirement");
             }

         } catch (Exception e) {
             e.printStackTrace();
             Log.e("DateCheck", "Error parsing rentTime: " + e.getMessage());
         }
     }*/
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
    }

}
