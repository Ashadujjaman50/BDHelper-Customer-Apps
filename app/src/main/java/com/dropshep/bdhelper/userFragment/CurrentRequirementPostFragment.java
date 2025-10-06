package com.dropshep.bdhelper.userFragment;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
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
import com.dropshep.bdhelper.databinding.FragmentCurrentRequirementPostBinding;
import com.dropshep.bdhelper.model.OrderModel;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class CurrentRequirementPostFragment extends Fragment {

    private FragmentCurrentRequirementPostBinding binding;

    OrderAdapter orderAdapter;
    ArrayList<OrderModel> orderModelArrayList;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    public CurrentRequirementPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_current_requirement_post, container, false);
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
        binding.myRentRecyclerView.setAdapter(orderAdapter);

        String currentUserId = firebaseUser.getUid(); // 🔑 current user id


        binding.noOneBidYet.setVisibility(View.VISIBLE);

        db.collection("orders")
                .whereEqualTo("orderInfo.uid", currentUserId)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.d("Firestore", "loadAllData: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        orderModelArrayList.clear();

                        long startOfToday = CommonClass.getStartOfTodayMillis();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            if (order == null) continue;

                            try {
                                // rentTime কে Long এ কনভার্ট করা
                                Long rentTime = Long.valueOf(order.getRouteInfo().getRentTime());

                                // ✅ শর্ত: আজকের তারিখ বা পরবর্তী, এবং status "Complete" না
                                if (rentTime != null
                                        && rentTime >= startOfToday
                                        && !"Complete".equalsIgnoreCase(order.getOrderInfo().getStatus())) {
                                    orderModelArrayList.add(order);
                                }

                            } catch (Exception e) {
                                Log.d("Firestore", "Parse rentTime failed: " + e.getMessage());
                            }
                        }
                        orderAdapter.notifyDataSetChanged();

                        // ✅ data আছে কিনা চেক
                        if (orderModelArrayList.isEmpty()) {
                            binding.noOneBidYet.setVisibility(View.VISIBLE);
                            binding.myRentRecyclerView.setVisibility(View.GONE);
                        } else {
                            binding.noOneBidYet.setVisibility(View.GONE);
                            binding.myRentRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }


}