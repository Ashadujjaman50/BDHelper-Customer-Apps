package com.krishibarirangpur.bdhelper.myUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.krishibarirangpur.bdhelper.R;

public class PreloadingDialog extends Dialog {
    private final LottieAnimationView lottieView;

    public PreloadingDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_preloading, null);
        setContentView(view);

        // Transparent background (dialog box itself)
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // 🔹 Full-screen blur/dim effect
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.dimAmount = 0.7f; // 0 = no dim, 1 = full dark background
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            getWindow().setAttributes(layoutParams);
        }

        // Lottie view reference
        lottieView = view.findViewById(R.id.lottiePreloading);

        // Dialog settings
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();
        if (lottieView != null) {
            lottieView.playAnimation();
        }
    }

    @Override
    public void dismiss() {
        // 🔹 সবকিছু 1 সেকেন্ড delay দিয়ে run হবে
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (lottieView != null) {
                lottieView.cancelAnimation();
            }
            super.dismiss();
        }, 1000);
    }
}
