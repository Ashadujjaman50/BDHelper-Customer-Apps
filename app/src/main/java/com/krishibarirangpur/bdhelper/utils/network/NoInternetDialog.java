package com.krishibarirangpur.bdhelper.utils.network;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.krishibarirangpur.bdhelper.R;

public class NoInternetDialog extends DialogFragment {


    private boolean doubleBackToExitPressedOnce = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false); // ❌ Prevent dismiss on outside touch
        setCancelable(false); // ❌ Prevent back press dismiss
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_no_internet, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button retryButton = view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                dismiss();
            } else {
                dismiss();
                showAgain(); // রি-শো করো
            }
        });

        return view;
    }

    private void showAgain() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(new NoInternetDialog(), "NoInternetDialog")
                .commitAllowingStateLoss();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            getDialog().setOnKeyListener((dialogInterface, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getAction() == android.view.KeyEvent.ACTION_UP) {
                    if (doubleBackToExitPressedOnce) {
                        requireActivity().finishAffinity(); // Close the app
                    } else {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                    }
                    return true; // Consume the back press
                }
                return false;
            });
        }
    }
}
