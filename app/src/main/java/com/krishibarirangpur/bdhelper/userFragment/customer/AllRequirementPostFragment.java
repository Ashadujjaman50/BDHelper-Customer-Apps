package com.krishibarirangpur.bdhelper.userFragment.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.OrderAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentAllRequirementPostBinding;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class AllRequirementPostFragment extends Fragment {

    private FragmentAllRequirementPostBinding binding;
    OrderAdapter orderAdapter;
    ArrayList<OrderModel> orderModelArrayList;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    public AllRequirementPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_requirement_post, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loadAllData();

        orderAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String subCategoryId = orderModelArrayList.get(position)
                        .getOrderInfo().getSubCategoryId();

                Log.d("OrderClick", "Clicked SubCategory ID: " + subCategoryId);

                Intent intent = new Intent(getContext(), BidActivity.class);
                intent.putExtra(MyUtils.bidAction,"new");
                intent.putExtra("user_type", "customer");
                intent.putExtra(MyUtils.orderId, orderModelArrayList.get(position).getOrderInfo().getOrderId());
                intent.putExtra(MyUtils.categoryId, orderModelArrayList.get(position).getOrderInfo().getCategoryId());
                intent.putExtra(MyUtils.subCategoryId, subCategoryId);
                requireActivity().startActivity(intent);
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }

            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllData() {
        orderModelArrayList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderModelArrayList);
        binding.allRentRecyclerView.setAdapter(orderAdapter);

        String currentUserId = firebaseUser.getUid(); // 🔑 current user id

        // 🔹 Loading শুরু
        binding.loading.setVisibility(View.VISIBLE);
        binding.noOneBidYet.setVisibility(View.GONE);
        binding.allRentRecyclerView.setVisibility(View.GONE);

        db.collection(FirebaseCollectionTable.ORDERS)
                .whereEqualTo("orderInfo.uid", currentUserId) // ✅ শুধু current user এর order
                .addSnapshotListener((querySnapshot, error) -> {
                    // 🔹 Loading শেষ
                    binding.loading.setVisibility(View.GONE);

                    if (error != null) {
                        MyToast.showShort(getContext(), "❌ Error: " + error.getMessage());
                        Log.d("Firestore", "loadAllData: "+error.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        orderModelArrayList.clear();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            if (order != null) {
                                orderModelArrayList.add(order);
                            }
                        }
                        orderAdapter.notifyDataSetChanged();

                        // ✅ data আছে কিনা চেক
                        if (orderModelArrayList.isEmpty()) {
                            binding.noOneBidYet.setVisibility(View.VISIBLE);
                            binding.allRentRecyclerView.setVisibility(View.GONE);
                        } else {
                            binding.noOneBidYet.setVisibility(View.GONE);
                            binding.allRentRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }
}