package com.krishibarirangpur.bdhelper.utils.authWidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.authentication.LoginActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

public class UserAction {

    public static void blockAccountCheck(Context context) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        new MaterialAlertDialogBuilder(context)
                .setTitle("অ্যাকাউন্ট ব্লক")
                .setMessage("আপনার অ্যাকাউন্টটি সাময়িকভাবে ব্লক করা হয়েছে। অনুগ্রহ করে সাপোর্টে যোগাযোগ করুন।")
                .setCancelable(false)
                .setPositiveButton("ঠিক আছে", (dialog, which) -> {

                    firebaseAuth.signOut();

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    context.startActivity(intent);

                    // যদি context Activity হয় তাহলে finish করবে
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                })
                .setNeutralButton("সাপোর্টে কল করুন", (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + MyUtils.HOTLINE_NUMBER));
                    context.startActivity(callIntent);
                })
                .show();
    }
}
