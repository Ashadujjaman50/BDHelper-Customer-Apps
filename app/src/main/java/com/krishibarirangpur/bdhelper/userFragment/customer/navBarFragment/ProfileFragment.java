package com.krishibarirangpur.bdhelper.userFragment.customer.navBarFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentProfileBinding;
import com.krishibarirangpur.bdhelper.sharedActivity.PermissionActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.ProfileActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.PromoActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.RatingReviewActivity;
import com.krishibarirangpur.bdhelper.sharedActivity.ReferenceActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.AddressBookActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.authWidget.LogoutHelper;
import com.krishibarirangpur.bdhelper.utils.core.LanguageDialogHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeDialogHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.squareup.picasso.Picasso;

import java.util.Locale;

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
            intent.putExtra(MyUtils.USER_TYPE, MyUtils.CUSTOMER);
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
            intent.putExtra(MyUtils.IS_PICKER, false);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //Promo Activity (Type of discount)
        binding.discountRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PromoActivity.class);
            intent.putExtra(MyUtils.DISCOUNT_TYPE, MyUtils.DISCOUNT);
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
        });

        //Promo Activity (Type of Promo Code)
        binding.promoRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PromoActivity.class);
            intent.putExtra(MyUtils.DISCOUNT_TYPE, MyUtils.PROMO);
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
        binding.setThemeTv.setText(" ("+ ThemeHelper.getThemeName(requireContext())+")");  //Current Set theme (System/Light/Dark)
        binding.themeRl.setOnClickListener(v -> ThemeDialogHelper.showBottomSheetThemeDialog(requireActivity()));

        // Language change logic
        binding.languageRL.setOnClickListener(v -> LanguageDialogHelper.showBottomSheetLanguageDialog(requireActivity()));

        //share App
        binding.shareAppRl.setOnClickListener(v -> {
            //share();
            ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").setChooserTitle("Share BD Helper").setText("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()).startChooser();
        });

        //Reference
        binding.referAppRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ReferenceActivity.class);
            intent.putExtra(MyUtils.USER_TYPE, MyUtils.CUSTOMER);
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
                        Double rating = documentSnapshot.getDouble("rating");

                        binding.userNameTV.setText(name);

                        // User Rating: Show from Reviews if available, else from Database
                        CommonClass.getUserRatingInfo(MyUtils.customerId, userId, MyUtils.PARTNER, (averageRating, totalReviews) -> {
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

}
