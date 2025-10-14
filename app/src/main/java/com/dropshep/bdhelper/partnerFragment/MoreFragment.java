package com.dropshep.bdhelper.partnerFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.RatingReviewActivity;
import com.dropshep.bdhelper.ReferenceActivity;
import com.dropshep.bdhelper.databinding.FragmentMoreBinding;
import com.dropshep.bdhelper.model.BidSummary;
import com.dropshep.bdhelper.myUtils.CacheManager;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.FinanceCache;
import com.dropshep.bdhelper.myUtils.FinanceManager;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.Replacement;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partner.DashboardActivity;
import com.dropshep.bdhelper.partner.PaymentActivity;
import com.dropshep.bdhelper.partner.SettingActivity;
import com.dropshep.bdhelper.user.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Map;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String userId;
    FinanceManager financeManager;

    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_more, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        // init
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        showCachedBidSummary();
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

        /// Show Android Version And Apps Version Name
        binding.applicationVersionTv.setText(CommonClass.showAndroidVersionAndAppVersion(requireActivity()));


        //Rating And Review
        binding.rattingTV.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), RatingReviewActivity.class);
            intent.putExtra("user_type","partner");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        binding.settingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SettingActivity.class);
            intent.putExtra("user_type","partner");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.paymentMethodRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PaymentActivity.class);
            intent.putExtra("type","payment_method");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.paymentHistoryRL.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PaymentActivity.class);
            intent.putExtra("type","payment_history");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Theme change logic
        binding.setThemeTv.setText(" (" + ThemeUtil.getThemeName(requireActivity()) + ")"); // Current Set theme (System/Light/Dark)
        binding.themeRl.setOnClickListener(v -> showBottomSheetThemeDialog());

        // Language change logic
        binding.languageRL.setOnClickListener(v -> showBottomSheetLanguageDialog());


        //share App
        binding.shareAppLl.setOnClickListener(v -> {
            //share();
            Intent intent = new Intent(requireActivity(), ReferenceActivity.class);
            intent.putExtra("user_type", "partner");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });



    }

    private void showCachedBidSummary() {
        BidSummary summary = CacheManager.getInstance().getBidSummary();

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
                        // ✅ Null check & set rating
                        if (rating != null) {
                            binding.rattingTV.setText(String.valueOf((int) Math.round(rating)));
                        } else {
                            binding.rattingTV.setText("0"); // Default rating
                        }

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

    private void showBottomSheetLanguageDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet_language_layout);

        TextView bangla = bottomSheetDialog.findViewById(R.id.banglaTV);
        TextView english = bottomSheetDialog.findViewById(R.id.englishTV);

        bottomSheetDialog.show();

        if (bangla != null) {
            bangla.setOnClickListener(v -> {
                LocaleHelper.setLocale(requireContext(), "bn");
                requireActivity().recreate(); // Recreate current activity
                bottomSheetDialog.dismiss();
            });
        }

        if (english != null) {
            english.setOnClickListener(v -> {
                LocaleHelper.setLocale(requireContext(), "en");
                requireActivity().recreate(); // Recreate current activity
                bottomSheetDialog.dismiss();
            });
        }
    }

    private void showBottomSheetThemeDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet_theme_layout);

        TextView defaultMode = bottomSheetDialog.findViewById(R.id.defaultTV);
        TextView lightMode = bottomSheetDialog.findViewById(R.id.lightTV);
        TextView darkMode = bottomSheetDialog.findViewById(R.id.darkTV);

        bottomSheetDialog.show();

        View.OnClickListener themeClickListener = v -> {
            int mode;
            if (v.getId() == R.id.defaultTV) {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            } else if (v.getId() == R.id.lightTV) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            }

            ThemeUtil.setTheme(requireContext(), mode);

            // Refresh navbar menu before recreation
            if (getActivity() instanceof MainActivity) {
                ((DashboardActivity) getActivity()).refreshCurrentMenuItem();
            }

            requireActivity().recreate();
            bottomSheetDialog.dismiss();
        };

        if (defaultMode != null) defaultMode.setOnClickListener(themeClickListener);
        if (lightMode != null) lightMode.setOnClickListener(themeClickListener);
        if (darkMode != null) darkMode.setOnClickListener(themeClickListener);
    }

}