package com.krishibarirangpur.bdhelper.userFragment.partner.navBarFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentMoreBinding;
import com.krishibarirangpur.bdhelper.model.BidSummaryModel;
import com.krishibarirangpur.bdhelper.sharedActivity.RatingReviewActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.ReferenceActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.PaymentActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.SettingActivity;
import com.krishibarirangpur.bdhelper.utils.CacheManager;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.FinanceCache;
import com.krishibarirangpur.bdhelper.utils.FinanceManager;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.core.LanguageDialogHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeDialogHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Map;


public class MoreFragment extends Fragment {

    public MoreFragment() {
        // Required empty public constructor
    }

    private FragmentMoreBinding binding;
    private FirebaseFirestore db;
    private String userId;
    private FinanceManager financeManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        financeManager = new FinanceManager();


        // 🔹 Partner Finance Summary Load
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
                    getContext(), String.valueOf(totalEarned)));

            // 🔹 কার পাওনা বেশি সেটার ভিত্তিতে টেক্সট আপডেট করো
            if (owedTo == 1.0) {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            } else if (owedTo == 2.0) {
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            } else {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            }

        }
        else {
            // 🔹 যদি cache লোড না থাকে, Firestore থেকে ডেটা নাও
            financeManager.getPartnerFinanceSummary(userId, (totalEarned, partnerReceivable, companyReceivable) -> {

                Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
                double netAmount = result.get("netAmount");
                double owedTo = result.get("owedTo");

                binding.totalAmount.setText(Replacement.ReplacementNumberInLocal(
                        getContext(), String.valueOf(totalEarned)));

                if (owedTo == 1.0) {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                } else if (owedTo == 2.0) {
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                } else {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                }

            });
        }

        //Load Current Partner info
        loadCurrentPartnerInfo();

        showCachedBidSummary();

        /// Show Android Version And Apps Version Name
        binding.applicationVersionTv.setText(CommonClass.showAndroidVersionAndAppVersion(requireActivity()));


        //Rating And Review
        binding.rattingTV.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), RatingReviewActivity.class);
            intent.putExtra(MyUtils.USER_TYPE,MyUtils.PARTNER);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        binding.settingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SettingActivity.class);
            intent.putExtra(MyUtils.USER_TYPE,MyUtils.PARTNER);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.paymentMethodRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PaymentActivity.class);
            intent.putExtra(MyUtils.ACTIVITY_TYPE, MyUtils.PAYMENT_METHOD_FRAG);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.paymentHistoryRL.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PaymentActivity.class);
            intent.putExtra(MyUtils.ACTIVITY_TYPE, MyUtils.PAYMENT_HISTORY_FRAG);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Theme change logic
        binding.setThemeTv.setText(" (" + ThemeHelper.getThemeName(requireActivity()) + ")"); // Current Set theme (System/Light/Dark)
        binding.themeRl.setOnClickListener(v -> ThemeDialogHelper.showBottomSheetThemeDialog(requireActivity()));

        // Language change logic
        binding.languageRL.setOnClickListener(v -> LanguageDialogHelper.showBottomSheetLanguageDialog(requireActivity()));


        //share App
        binding.shareAppLl.setOnClickListener(v -> {
            //share();
            Intent intent = new Intent(requireActivity(), ReferenceActivity.class);
            intent.putExtra(MyUtils.USER_TYPE, MyUtils.PARTNER);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        
        CommonClass.socialMediaClickToResponse(
                binding.facebookIV,
                binding.twitterIV,
                binding.instagramIV,
                binding.linkedinIV );
    }

    private void showCachedBidSummary() {
        BidSummaryModel summary = CacheManager.getInstance().getBidSummary();

        if (summary != null) {
            binding.totalBidTv.setText(
                    Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(summary.getTotal()))
            );
            binding.successBidTv.setText(
                    Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(summary.getSuccess()))
            );
            binding.cancelBidTv.setText(
                    Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(summary.getCancel()))
            );
        } else {
            binding.totalBidTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            binding.successBidTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            binding.cancelBidTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (financeManager != null) financeManager.stopListening(); // 🔹 Stop realtime listener
    }

    private void loadCurrentPartnerInfo() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        Double rating = documentSnapshot.getDouble("rating");
                        String verifyStatus = documentSnapshot.getString("verifyStatus");

                        binding.userNameTV.setText(name);
                        binding.mobileTV.setText(phone);

                        // User Rating: Show from Reviews if available, else from Database
                        CommonClass.getUserRatingInfo(MyUtils.vendorId,userId,MyUtils.CUSTOMER, (averageRating, totalReviews) -> {
                            if (totalReviews > 0) {
                                binding.rattingTV.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
                            } else {
                                if (rating != null) {
                                    binding.rattingTV.setText(String.format(Locale.getDefault(), "%.1f", rating));
                                } else {
                                    binding.rattingTV.setText(String.format(Locale.getDefault(), "%.1f", 5.0));
                                }
                            }
                        });

                        binding.statusPendingTV.setVisibility(View.GONE);
                        binding.statusVerifiedTV.setVisibility(View.GONE);
                        binding.statusNotVerifiedTV.setVisibility(View.GONE);

                        // Show only the matching one
                        if (verifyStatus != null) {
                            switch (verifyStatus.toLowerCase(Locale.getDefault())) {
                                case "pending":
                                    binding.statusPendingTV.setVisibility(View.VISIBLE);
                                    break;
                                case "verified":
                                    binding.statusVerifiedTV.setVisibility(View.VISIBLE);
                                    break;
                                case "rejected":
                                    binding.statusNotVerifiedTV.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    // Error handling
                });

        // 🔹 Load profile image
        db.collection("users")
                .document(userId)
                .collection("Document")
                .document("info")
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImage = documentSnapshot.getString("profileImage");

                        // Use these URLs as needed
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile).into(binding.userProfilePicIV);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentPartnerInfo(); // 🔁 Force reload every time Activity resumes
    }
}
