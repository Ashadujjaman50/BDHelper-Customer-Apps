package com.krishibarirangpur.bdhelper.userFragment.customer.navBarFragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krishibarirangpur.bdhelper.sharedActivity.ProfileActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.PermissionActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.PromoActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.RatingReviewActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.ReferenceActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.LogoutHelper;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.customer.AddressBookActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.MainActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentProfileBinding;
import com.krishibarirangpur.bdhelper.utils.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import android.content.Intent;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /// Show Android Version And Apps Version Name
        binding.applicationVersionTv.setText(CommonClass.showAndroidVersionAndAppVersion(requireActivity()));

        //Rating And Review
        binding.rattingTV.setOnClickListener(v -> {

            Intent intent = new Intent(requireActivity(), RatingReviewActivity.class);
            intent.putExtra("user_type", "customer");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //load Current User info
        loadCurrentUserInfo();

        //profile Activity
        binding.profileRL.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ProfileActivity.class);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //Address Book Activity
        binding.addressBookRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddressBookActivity.class);
            intent.putExtra("controlType", "addressList");
            intent.putExtra("isPicker", false);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //Promo Activity (Type of discount)
        binding.discountRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PromoActivity.class);
            intent.putExtra("discountType", "discount");
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
        });

        //Promo Activity (Type of Promo Code)
        binding.promoRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PromoActivity.class);
            intent.putExtra("discountType", "promo");
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
        });

        //permission Activity Call
        binding.permissionRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PermissionActivity.class);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //Terms And Condition
        binding.termsConditionRl.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.trems_conditions_customer_url));
            v.getContext().startActivity(intent);
        });

        //Privacy Policy
        binding.privacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.privacy_policy_url));
            v.getContext().startActivity(intent);
        });


        // Theme change logic
        binding.setThemeTv.setText(" ("+ThemeUtil.getThemeName(requireContext())+")");  //Current Set theme (System/Light/Dark)
        binding.themeRl.setOnClickListener(v -> showBottomSheetThemeDialog());

        // Language change logic
        binding.languageRL.setOnClickListener(v -> showBottomSheetLanguageDialog());

        //share App
        binding.shareAppRl.setOnClickListener(v -> {
            //share();
            ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").setChooserTitle("Share BD Helper").setText("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()).startChooser();
        });

        //Reference
        binding.referAppRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ReferenceActivity.class);
            intent.putExtra("user_type", "customer");
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.logOutRl.setOnClickListener(v -> {
            LogoutHelper.logoutUser(requireActivity());
        });

        CommonClass.socialMediaClickToResponse(
                binding.facebookIV,
                binding.twitterIV,
                binding.instagramIV,
                binding.linkedinIV);
    }

    private void loadCurrentUserInfo() {
        String  userId = firebaseAuth.getCurrentUser().getUid();
        // 🔹 Load main user document
        db.collection("users")
                .document(userId)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        Double rating = documentSnapshot.getDouble("rating");
                        String location = documentSnapshot.getString("location");
                        String district = documentSnapshot.getString("district"); // Always English

                        binding.userNameTV.setText(name);
                        // ✅ Null check & set rating
                        if (rating != null) {
                            binding.rattingTV.setText(String.valueOf((int) Math.round(rating)));
                        } else {
                            binding.rattingTV.setText("0"); // Default rating
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
        loadCurrentUserInfo(); // 🔁 Force reload every time Activity resumes

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
                requireActivity().recreate();  // recreate entire activity
                bottomSheetDialog.dismiss();
            });
        }

        if (english != null) {
            english.setOnClickListener(v -> {
                LocaleHelper.setLocale(requireContext(), "en");
                requireActivity().recreate();
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
                ((MainActivity) getActivity()).refreshCurrentMenuItem();
            }

            requireActivity().recreate();
            bottomSheetDialog.dismiss();
        };

        if (defaultMode != null) defaultMode.setOnClickListener(themeClickListener);
        if (lightMode != null) lightMode.setOnClickListener(themeClickListener);
        if (darkMode != null) darkMode.setOnClickListener(themeClickListener);
    }

}
