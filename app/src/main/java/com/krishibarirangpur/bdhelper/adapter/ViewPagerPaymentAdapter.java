package com.krishibarirangpur.bdhelper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment.PaymentPaidToCompanyFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment.PaymentWithdrawFragment;

public class ViewPagerPaymentAdapter extends FragmentStateAdapter {
    public ViewPagerPaymentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerPaymentAdapter(FragmentManager fm, Lifecycle lifecycle){
        super(fm, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PaymentPaidToCompanyFragment();
        }
        return new PaymentWithdrawFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
