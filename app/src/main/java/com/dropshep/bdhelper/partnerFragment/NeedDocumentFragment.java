package com.dropshep.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.ServiceAdapter;
import com.dropshep.bdhelper.databinding.FragmentNeedDocumentBinding;
import com.dropshep.bdhelper.model.ServiceModel;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.partner.ServiceDocumentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NeedDocumentFragment extends Fragment {

    private FragmentNeedDocumentBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    ArrayList<ServiceModel> serviceModelArrayList;
    ServiceAdapter adapter;

    public NeedDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_need_document, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        /// Load array List
        serviceModelArrayList = new ArrayList<>();
        adapter =new ServiceAdapter(getContext(), serviceModelArrayList);

        //loadService Data
        loadServiceData();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(requireActivity(), ServiceDocumentActivity.class);
                intent.putExtra("serviceId", serviceModelArrayList.get(position).getServiceId());
                intent.putExtra(MyUtils.subCategoryId, serviceModelArrayList.get(position).getSubCategoryId());
                intent.putExtra(MyUtils.subCategoryName, serviceModelArrayList.get(position).getSubCategoryName());
                requireActivity().startActivity(intent);
            }

            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });
    }

    private ListenerRegistration listenerRegistration;
    @SuppressLint("NotifyDataSetChanged")
    private void loadServiceData() {
        String userId = firebaseUser.getUid();
        binding.allRentServiceRv.setAdapter(adapter);

        listenerRegistration = db.collection("users")
                .document(userId)
                .collection("services")
                .orderBy("serviceId", Query.Direction.ASCENDING)
                .addSnapshotListener((queryServiceSnapshots, error) -> {
                    if (error != null) {
                        MyToast.showShort(getContext(), error.getMessage());
                        return;
                    }

                    if (queryServiceSnapshots != null){
                        serviceModelArrayList.clear();
                        for (DocumentSnapshot doc: queryServiceSnapshots){
                            ServiceModel serviceModel = doc.toObject(ServiceModel.class);
                            if (serviceModel != null && serviceModel.getServiceVerified().equals("pending")){
                                serviceModelArrayList.add(serviceModel);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (serviceModelArrayList.isEmpty()) {
                            binding.notBidYetTv.setVisibility(View.VISIBLE);
                            binding.allRentServiceRv.setVisibility(View.GONE);
                        } else {
                            binding.notBidYetTv.setVisibility(View.GONE);
                            binding.allRentServiceRv.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove(); // 🔹 Firestore Listener remove করতে হবে
        }
    }
}