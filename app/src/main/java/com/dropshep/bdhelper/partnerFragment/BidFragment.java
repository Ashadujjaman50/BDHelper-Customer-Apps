package com.dropshep.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.AdapterBidDetail;
import com.dropshep.bdhelper.databinding.FragmentBidBinding;
import com.dropshep.bdhelper.model.BidModel;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.FinanceManager;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.PreloadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class BidFragment extends Fragment {

    private FragmentBidBinding binding;

    String bidAction, currentUserId, user_type;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    ArrayList<BidModel> bidModelArrayList;

    AdapterBidDetail adapterBidDetail;

    LoadingDialog loadingDialog;
    PreloadingDialog preloadingDialog;

    //Financial Leger Assign
    FinanceManager fm;

    private boolean isDialogShowing = false;

    public BidFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_type = getArguments().getString("user_type");
            bidAction = getArguments().getString(MyUtils.bidAction);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_bid, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        currentUserId = firebaseUser.getUid();

        //Financial Ledger Init
        fm = new FinanceManager();


        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        preloadingDialog = new PreloadingDialog(requireContext());
        preloadingDialog.show();

        //get all success bid
        getAllCurrentVendorBid();

        adapterBidDetail.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if ("confirmed".equalsIgnoreCase(bidModelArrayList.get(position).getBidInfo().getStatus())) {
                    if (!isDialogShowing) { // check flag
                        isDialogShowing = true;
                        showAlertDialog(position, () -> {
                            isDialogShowing = false; // reset flag when dialog dismissed
                        });
                    }
                }

            }

            @Override
            public void onShowItemClick(int position) { }

            @Override
            public void onDeleteItemClick(int position) { }
        });

    }

    private void showAlertDialog(int position, DialogDismissListener listener){

        String bidId = bidModelArrayList.get(position).getBidInfo().getBidId();
        String orderId = bidModelArrayList.get(position).getOrderInfo().getOrderId();
        String bidAmount = bidModelArrayList.get(position).getBidInfo().getBidAmount();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_confirmation, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        ImageView iconImage = dialogView.findViewById(R.id.iconImage);
        TextView titleText = dialogView.findViewById(R.id.titleText);
        TextView messageText = dialogView.findViewById(R.id.messageText);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        //dialog.setCanceledOnTouchOutside(false);

        titleText.setText(R.string.are_you_sure_alert_title);
        messageText.setText(R.string.are_you_sure_alert_msg);
        btnConfirm.setText(R.string.yes);

        dialog.setOnDismissListener(d -> {
            if (listener != null) listener.onDismiss();
        });

        iconImage.setImageResource(CommonClass.getIconForSubCategory(bidModelArrayList.get(position).getOrderInfo().getSubCategoryId()));
        // 🔹 Tint set
        iconImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.icon_color), android.graphics.PorterDuff.Mode.SRC_IN);

        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        btnConfirm.setOnClickListener(v2 -> {
            dialog.dismiss();
            loadingDialog.setMessage("আপডেট হচ্ছে ...");
            loadingDialog.show();
            if (listener != null) listener.onDismiss();

            // ✅ 1️⃣ bidForOrder -> status update
            db.collection("bidForOrder")
                    .document(bidId)
                    .update("bidInfo.status", "done")
                    .addOnSuccessListener(aVoid ->
                            db.collection("orders")
                                    .document(orderId)
                                    .update("orderInfo.status", "done")
                                    .addOnSuccessListener(unused -> {
                                        loadingDialog.dismiss();
                                        preloadingDialog.show();
                                        MyToast.showShort(getContext(), "✅ Service done!");
                                        Log.d("ConfirmOrder", "Service status updated successfully.");

                                        //Reload Bid List
                                        getAllCurrentVendorBid();

                                        //Update Financial Ledger in Partner
                                        fm.createLedgerAndUpdateBalance(bidId, orderId, currentUserId, bidAmount); // 10% compan
                                    })
                                    .addOnFailureListener(e -> {
                                        loadingDialog.dismiss();
                                        Log.e("ConfirmOrder", "❌ Failed to update order: " + e.getMessage());
                                        MyToast.showShort(getContext(), "Failed to update order.");
                                    }))
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        Log.e("ConfirmOrder", "❌ Failed to update bid: " + e.getMessage());
                        MyToast.showShort(getContext(), "Failed to confirm bid.");
                    });

        });


        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }


    // Callback interface
    interface DialogDismissListener {
        void onDismiss();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllCurrentVendorBid() {
        bidModelArrayList = new ArrayList<>();
        adapterBidDetail = new AdapterBidDetail(getContext(), bidModelArrayList);
        binding.bidRV.setAdapter(adapterBidDetail);

        List<String> statusList;
        if (!"pending".equals(bidAction)) {
            // pending না হলে confirmed + done
            statusList = Arrays.asList("confirmed", "done");
        } else {
            statusList = Collections.singletonList("pending");
        }

        long todayMillis = System.currentTimeMillis();

        // Base query
        var query = db.collection("bidForOrder")
                .whereEqualTo("bidInfo.vendorId", currentUserId);

        // যদি statusList multiple values থাকে, use whereIn()
        if (statusList.size() > 1) {
            query = query.whereIn("bidInfo.status", statusList);
        } else {
            query = query.whereEqualTo("bidInfo.status", statusList.get(0));
        }

        // 🔹 যদি bidAction == "pending", তাহলে future rentTime filter করবে
        if ("pending".equals(bidAction)) {
            query = query.whereGreaterThanOrEqualTo("orderInfo.rentTime", String.valueOf(todayMillis));
        }

        // 🔹 প্রাথমিকভাবে rentTime দিয়ে orderBy
        query = query.orderBy("orderInfo.rentTime", Query.Direction.ASCENDING);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bidModelArrayList.clear();
                    preloadingDialog.dismiss();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        binding.notBidYetTv.setVisibility(View.GONE);

                        List<BidModel> tempList = new ArrayList<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            BidModel bidModel = doc.toObject(BidModel.class);
                            if (bidModel != null) {
                                tempList.add(bidModel);
                            }
                        }

                        // 🔹 Custom Sort Logic:
                        // 1️⃣ confirmed আগে
                        // 2️⃣ done পরে
                        // 3️⃣ একই status এর মধ্যে rentTime ascending
                        tempList.sort((a, b) -> {
                            String statusA = a.getBidInfo().getStatus();
                            String statusB = b.getBidInfo().getStatus();

                            // status order: confirmed < done
                            int statusOrder = getStatusOrder(statusA) - getStatusOrder(statusB);
                            if (statusOrder != 0) return statusOrder;

                            // যদি status same হয়, তাহলে rentTime অনুযায়ী sort
                            long rentA = Long.parseLong(a.getOrderInfo().getRentTime());
                            long rentB = Long.parseLong(b.getOrderInfo().getRentTime());
                            return Long.compare(rentA, rentB);
                        });

                        bidModelArrayList.addAll(tempList);
                        adapterBidDetail.notifyDataSetChanged();
                    } else {
                        binding.notBidYetTv.setVisibility(View.VISIBLE);
                        Log.d("BidDebug", "⚠️ No matching documents found");
                    }
                })
                .addOnFailureListener(e -> {
                    preloadingDialog.dismiss();
                    binding.notBidYetTv.setVisibility(View.VISIBLE);
                    Log.e("BidDebug", "❌ Firestore Error: " + e.getMessage(), e);
                });
    }

    /**
     * Helper: status অনুযায়ী priority দেবে
     */
    private int getStatusOrder(String status) {
        switch (status) {
            case "confirmed": return 1;
            case "done": return 2;
            default: return 3; // fallback
        }
    }



}