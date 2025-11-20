package com.krishibarirangpur.bdhelper.reserve;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentVerifyPhoneBinding;
import com.krishibarirangpur.bdhelper.myUtils.MyToast;
import com.krishibarirangpur.bdhelper.myUtils.Replacement;

public class VerifyPhoneFragment extends Fragment {

    private FragmentVerifyPhoneBinding binding;

    public VerifyPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_verify_phone, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        // আবার টাইমার শুরু করুন
        startOtpCountdown();
    }

    private CountDownTimer countDownTimer;
    private static final long RESEND_INTERVAL = 30000; // 30 seconds

    private void startOtpCountdown() {

        binding.resendBtn.setVisibility(View.GONE);
        binding.timerText.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(RESEND_INTERVAL, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                @SuppressLint("DefaultLocale")
                String time = String.format("00:%02d", seconds);
                binding.timerText.setText(Replacement.NumberFormatFullTimer(time));
            }

            public void onFinish() {
                binding.timerText.setVisibility(View.GONE);
                binding.resendBtn.setVisibility(View.VISIBLE);
            }
        }.start();

        binding.resendBtn.setOnClickListener(v -> {
            // এখানে OTP resend API call করুন
            MyToast.showShort(requireActivity(),"OTP পুনরায় পাঠানো হচ্ছে...");

            // আবার টাইমার শুরু করুন
            startOtpCountdown();
        });
    }
}