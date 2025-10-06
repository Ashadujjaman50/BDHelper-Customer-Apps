package com.dropshep.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.BidPartnerAdapter;
import com.dropshep.bdhelper.databinding.FragmentBidSkilledLaborBinding;
import com.dropshep.bdhelper.model.BidModel;
import com.dropshep.bdhelper.model.OrderModel;
import com.dropshep.bdhelper.model.ServiceModel;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.Replacement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BidSkilledLaborFragment extends Fragment {

    private FragmentBidSkilledLaborBinding binding;

    private String currentUserId, userId, orderId, rentTime, categoryId, subCategoryId;

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    ArrayList<ServiceModel> serviceModelArrayList;
    ServiceModel selectedServiceModel; // ✅ সিলেক্টেড সার্ভিস রাখবে এখানে

    ArrayList<BidModel> bidModelArrayList;
    BidPartnerAdapter bidPartnerAdapter;

    LoadingDialog loadingDialog;

    public BidSkilledLaborFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            subCategoryId = getArguments().getString("subCategoryId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bid_skilled_labor, container, false);
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

        //get current Order info
        getCurrentOrderInfo();

        //Load Current Vendor Bid
        loadCurrentVendorBid();

        //loadPartner ServiceInfo
        loadPartnerInfo();

        // Setup Service picker dialog
        setupServicePicker();

        // ✅ submit button click
        binding.bidSubmitBtn.setOnClickListener(v -> {
            if (CommonClass.validateField(binding.selectVehicleNameTv)) return;
            else if (CommonClass.validateField(binding.amountEt)) return;
            else dataUploadInDatabase(selectedServiceModel);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCurrentVendorBid() {

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
                vehicleItems[i] = s.getServiceRegistrationNumber()  +" "+ s.getSubCategoryName()+ " (" +
                        s.getServiceCategoryAndYear() + ") - " +
                        s.getServiceModelNumber() ;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.custom_title_dialog, null))

                    .setItems(vehicleItems, (dialog, which) -> {
                        selectedServiceModel = serviceModelArrayList.get(which); // ✅ সিলেক্টেড সার্ভিস সেট
                        binding.selectVehicleNameTv.setText(selectedServiceModel.getServiceRegistrationNumber());
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

                    loadCurrentVendorBid();
                })
                .addOnFailureListener(e->{
                    // Failed
                    loadingDialog.dismiss();
                    Log.d("PartnerInfo", "bidUpload: "+e.getMessage());
                    MyToast.showShort(getContext(), "❌ Error: " + e.getMessage());
                });


    }

    @SuppressLint("SetTextI18n")
    private void getCurrentOrderInfo() {
        if (orderId == null || orderId.isEmpty()) {
            return;
        }

        db.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        OrderModel order = documentSnapshot.toObject(OrderModel.class);

                        if (order != null) {

                            // ============ Order Info ============
                            categoryId = order.getOrderInfo().getCategoryId();
                            String orderStatus = order.getOrderInfo().getStatus();
                            long timestamp = order.getOrderInfo().getTimestamp();
                            userId = order.getOrderInfo().getUid();

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


                            // Set icon
                            int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
                            binding.postImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));

                            // Example: binding data
                            binding.orderIdTv.setText(orderId);
                            binding.postNameTv.setText(CommonClass.getSubCategoryName(subCategoryId));
                            binding.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(getContext(), rentDateAndTime));

                            binding.locationNameTv.setText(CommonClass.formatAddress(rentArea).first);
                            binding.locationArea.setText(CommonClass.formatAddress(rentArea).second);


                            binding.quantityTv.setText(Replacement.ReplacementPersonInLocal(getContext(), quantity));
                            binding.postDescriptionTv.setText(postDescription);
                            binding.postedDateTv.setText(formatTime(String.valueOf(timestamp), "dd-MMM-yy  hh:mm aa"));


                            binding.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags,0,0);
                            binding.typesTv.setText(types);

                            binding.capacityTv.setText(Replacement.ReplacementExperienceInLocal(getContext(),capacity)+" "+getString(R.string.experience));

                            if (subCategoryId.equals(MyUtils.SUB_DRIVER_ID)){
                                binding.capacityTv.setVisibility(View.VISIBLE);
                                binding.dividerOne.setVisibility(View.VISIBLE);
                            }


                        }
                    }
                    else {
                        MyToast.showShort(getContext(),"No order found for ID: " + orderId);
                    }
                })
                .addOnFailureListener(e -> {
                    MyToast.showShort(getContext(),"Error: " + e.getMessage());
                });
    }

    // 🔹 Format timestamp
    private String formatTime(String timeMillis, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(Long.parseLong(timeMillis));
            String formatted = DateFormat.format(pattern, calendar).toString();
            return formatted.replace("AM", "am").replace("PM", "pm");
        } catch (Exception e) {
            return "";
        }
    }

}