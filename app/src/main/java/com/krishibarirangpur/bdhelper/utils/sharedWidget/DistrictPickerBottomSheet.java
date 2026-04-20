package com.krishibarirangpur.bdhelper.utils.sharedWidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.DistrictAdapter;

import java.util.List;

public class DistrictPickerBottomSheet {

    public interface OnItemClickListener {
        void onItemClick(String item, int position);
    }

    public static void show(
            Context context,
            String title,
            List<String> displayList,
            OnItemClickListener listener
    ) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_dialog_recycleview, null);

        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        titleTv.setText(title);

        // Adapter
        DistrictAdapter adapter = new DistrictAdapter(displayList, (item, position) -> {
            if (listener != null) listener.onItemClick(item, position);
            bottomSheetDialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);

        // BottomSheet height control
        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                int heightPx = dpToPx(context, 400);

                bottomSheet.getLayoutParams().height = heightPx;
                bottomSheet.requestLayout();

                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(heightPx);
                behavior.setDraggable(true);
                behavior.setHideable(true);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetDialog.show();
    }

    // helper
    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
