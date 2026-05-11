package com.krishibarirangpur.bdhelper.utils.core;

import android.app.Activity;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;

public class LanguageDialogHelper {

    /**
     * Shows a BottomSheetDialog to change the application language.
     *
     * @param activity The activity from which the dialog is shown.
     */
    public static void showBottomSheetLanguageDialog(Activity activity) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet_language_layout);

        TextView bangla = bottomSheetDialog.findViewById(R.id.banglaTV);
        TextView english = bottomSheetDialog.findViewById(R.id.englishTV);

        bottomSheetDialog.show();

        if (bangla != null) {
            bangla.setOnClickListener(v -> {
                LocaleHelper.setLocale(activity, "bn");
                activity.recreate(); // recreate entire activity to apply changes
                bottomSheetDialog.dismiss();
            });
        }

        if (english != null) {
            english.setOnClickListener(v -> {
                LocaleHelper.setLocale(activity, "en");
                activity.recreate();
                bottomSheetDialog.dismiss();
            });
        }
    }
}
