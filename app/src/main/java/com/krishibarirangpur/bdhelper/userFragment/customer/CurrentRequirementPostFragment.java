package com.krishibarirangpur.bdhelper.userFragment.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.OrderAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentCurrentRequirementPostBinding;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;

import java.util.ArrayList;


public class CurrentRequirementPostFragment extends Fragment {

    private FragmentCurrentRequirementPostBinding binding;

    OrderAdapter orderAdapter;
    ArrayList<OrderModel> orderModelArrayList;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    // Pagination variables
    private DocumentSnapshot lastVisible;
    private boolean isLastItemReached = false;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 10;

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

        orderModelArrayList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderModelArrayList);
        binding.myRentRecyclerView.setAdapter(orderAdapter);

        loadAllData();

        binding.myRentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == orderModelArrayList.size() - 1) {
                    if (!isLoading && !isLastItemReached) {
                        loadMoreData();
                    }
                }
            }
        });

        orderAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String subCategoryId = orderModelArrayList.get(position)
                        .getOrderInfo().getSubCategoryId();

                Log.d("OrderClick", "Clicked SubCategory ID: " + subCategoryId);

                Intent intent = new Intent(getContext(), BidActivity.class);
                intent.putExtra(MyUtils.bidAction,"new");
                intent.putExtra(MyUtils.USER_TYPE, MyUtils.NOTICE_RECEIVER_CUSTOMER);
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
        if (isLoading) return;
        isLoading = true;
        isLastItemReached = false;

        String currentUserId = firebaseUser.getUid();
        long startOfToday = CommonClass.getStartOfTodayMillis();

        binding.loading.setVisibility(View.VISIBLE);
        binding.noOneBidYet.setVisibility(View.GONE);

        db.collection(FirebaseCollectionTable.ORDERS)
                .whereEqualTo("orderInfo.uid", currentUserId)
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(startOfToday))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    binding.loading.setVisibility(View.GONE);
                    isLoading = false;

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        orderModelArrayList.clear();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            if (order == null) continue;

                            // Status check locally (or you can use whereNotIn if supported/appropriate)
                            if (!"Complete".equalsIgnoreCase(order.getOrderInfo().getStatus())) {
                                orderModelArrayList.add(order);
                            }
                        }
                        orderAdapter.notifyDataSetChanged();

                        lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                        if (querySnapshot.size() < PAGE_SIZE) {
                            isLastItemReached = true;
                        }

                        if (orderModelArrayList.isEmpty() && isLastItemReached) {
                            binding.noOneBidYet.setVisibility(View.VISIBLE);
                        } else {
                            binding.noOneBidYet.setVisibility(View.GONE);
                            binding.myRentRecyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        isLastItemReached = true;
                        binding.noOneBidYet.setVisibility(View.VISIBLE);
                        binding.myRentRecyclerView.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    isLoading = false;
                    Log.d("Firestore", "loadAllData: " + e.getMessage());
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMoreData() {
        if (isLoading || isLastItemReached) return;

        isLoading = true;
        binding.loading.setVisibility(View.VISIBLE);
        long startOfToday = CommonClass.getStartOfTodayMillis();

        db.collection(FirebaseCollectionTable.ORDERS)
                .whereEqualTo("orderInfo.uid", firebaseUser.getUid())
                .whereGreaterThanOrEqualTo("routeInfo.rentTime", String.valueOf(startOfToday))
                .orderBy("routeInfo.rentTime", Query.Direction.ASCENDING)
                .startAfter(lastVisible)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    binding.loading.setVisibility(View.GONE);
                    isLoading = false;

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            if (order == null) continue;

                            if (!"Complete".equalsIgnoreCase(order.getOrderInfo().getStatus())) {
                                orderModelArrayList.add(order);
                            }
                        }
                        orderAdapter.notifyDataSetChanged();

                        lastVisible = querySnapshot.getDocuments().get(querySnapshot.size() - 1);
                        if (querySnapshot.size() < PAGE_SIZE) {
                            isLastItemReached = true;
                        }
                    } else {
                        isLastItemReached = true;
                    }
                })
                .addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    isLoading = false;
                    Log.d("Firestore", "loadMoreData: " + e.getMessage());
                });
    }



}