package com.dropshep.bdhelper.userFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.OrderAdapter;
import com.dropshep.bdhelper.databinding.FragmentAllRequirementPostBinding;
import com.dropshep.bdhelper.model.OrderModel;
import com.dropshep.bdhelper.myUtils.MyToast;
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllData() {
        orderModelArrayList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderModelArrayList);
        binding.allRentRecyclerView.setAdapter(orderAdapter);

        String currentUserId = firebaseUser.getUid(); // 🔑 current user id

        db.collection("orders")
                .whereEqualTo("orderInfo.uid", currentUserId) // ✅ শুধু current user এর order
                .addSnapshotListener((querySnapshot, error) -> {
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