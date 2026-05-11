package com.krishibarirangpur.bdhelper.utils.core;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.userActivity.customer.MainActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.DashboardActivity;

public class ThemeDialogHelper {

    /**
     * Shows a BottomSheetDialog to change the application theme.
     *
     * @param activity The activity from which the dialog is shown.
     */
    public static void showBottomSheetThemeDialog(Activity activity) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
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

            ThemeHelper.setTheme(activity, mode);

            // Refresh navbar menu before recreation if the activity is MainActivity or DashboardActivity
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).refreshCurrentMenuItem();
            } else if (activity instanceof DashboardActivity) {
                ((DashboardActivity) activity).refreshCurrentMenuItem();
            }

            activity.recreate();
            bottomSheetDialog.dismiss();
        };

        if (defaultMode != null) defaultMode.setOnClickListener(themeClickListener);
        if (lightMode != null) lightMode.setOnClickListener(themeClickListener);
        if (darkMode != null) darkMode.setOnClickListener(themeClickListener);
    }
}
