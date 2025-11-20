package com.krishibarirangpur.bdhelper.Interface;

import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);
    void onShowItemClick(int position);
    void onDeleteItemClick(int position);
}
