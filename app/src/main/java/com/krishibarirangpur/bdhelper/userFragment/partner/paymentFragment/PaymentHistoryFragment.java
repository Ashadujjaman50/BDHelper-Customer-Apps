package com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ViewPagerPaymentAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentPaymentHistoryBinding;
import com.krishibarirangpur.bdhelper.utils.FinanceCache;
import com.krishibarirangpur.bdhelper.utils.FinanceManager;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class PaymentHistoryFragment extends Fragment {
    private FragmentPaymentHistoryBinding binding;
    private String userId;
    private FinanceManager financeManager;
    double partnerEarn;


    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_history, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        userId = FirebaseAuth.getInstance().getUid();
        financeManager = new FinanceManager();

        // 🔹 Cache থেকে ডেটা দেখাও, যদি থাকে
        partnerFinanceSummaryLoad();

        FragmentManager fm = getChildFragmentManager();
        ViewPagerPaymentAdapter paymentAdapter = new ViewPagerPaymentAdapter(fm, getLifecycle());
        binding.paymentViewPager.setAdapter(paymentAdapter);
        binding.paymentTabLayout.addTab(binding.paymentTabLayout.newTab().setText(getString(R.string.paid_to_company)));
        binding.paymentTabLayout.addTab(binding.paymentTabLayout.newTab().setText(getString(R.string.withdraw)));
        binding.paymentViewPager.setUserInputEnabled(true);
        binding.paymentViewPager.setSaveEnabled(false);

        binding.paymentTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .alpha(1f)
                        .setDuration(200)
                        .start();

                setPagerFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(0.8f)
                        .setDuration(200)
                        .start();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.paymentViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.paymentTabLayout.selectTab(binding.paymentTabLayout.getTabAt(position));
            }
        });

    }

    private void setPagerFragment(int a) {
        binding.paymentViewPager.setCurrentItem(a);
    }


    private void partnerFinanceSummaryLoad() {

        if (FinanceCache.isLoaded) {
            FinanceCache.lastUpdated = System.currentTimeMillis();

            double totalEarned = FinanceCache.totalEarned;
            double partnerReceivable = FinanceCache.partnerReceivable;
            double companyReceivable = FinanceCache.companyReceivable;

            // 🔹 নেট হিসাব (কার পাওনা বেশি)
            Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
            double netAmount = result.get("netAmount");
            double owedTo = result.get("owedTo");

            // 🔹 মোট আয় দেখাও
            binding.totalAmount.setText(Replacement.ReplacementNumberInLocal(
                    requireContext(), String.valueOf(totalEarned)));

            // 🔹 কার পাওনা বেশি সেটার ভিত্তিতে টেক্সট আপডেট করো
            if (owedTo == 1.0) {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                partnerEarn = netAmount;
            } else if (owedTo == 2.0) {
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                partnerEarn = 0;


            } else {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                partnerEarn = 0;
            }

        }
        else {
            // 🔹 যদি cache লোড না থাকে, Firestore থেকে ডেটা নাও
            financeManager.getPartnerFinanceSummary(userId, (totalEarned, partnerReceivable, companyReceivable) -> {

                Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
                double netAmount = result.get("netAmount");
                double owedTo = result.get("owedTo");

                binding.totalAmount.setText(Replacement.ReplacementNumberInLocal(
                        requireContext(), String.valueOf(totalEarned)));

                if (owedTo == 1.0) {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    partnerEarn = netAmount;
                } else if (owedTo == 2.0) {
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    partnerEarn = 0;

                } else {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    partnerEarn = 0;
                }

            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (financeManager != null) financeManager.stopListening(); // 🔹 Stop realtime listener
    }

}
