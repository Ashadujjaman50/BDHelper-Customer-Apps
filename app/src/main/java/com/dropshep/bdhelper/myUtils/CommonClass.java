package com.dropshep.bdhelper.myUtils;

import static com.google.common.io.Resources.getResource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dropshep.bdhelper.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CommonClass {

    public static String showAndroidVersionAndAppVersion(Activity activity) {
        String versionText = null;
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            String versionName = packageInfo.versionName;

            // Android Version
            int sdkInt = Build.VERSION.SDK_INT;
            String androidVersion = Build.VERSION.RELEASE;  // eg. 33 = Android 13

            versionText = "Android: " + androidVersion + ", App version: " + versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  versionText;
    }

    public static void showDateTimePicker(Context context, int day, TextView targetTextView) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_date_time_picker);

        NumberPicker datePicker = bottomSheetDialog.findViewById(R.id.datePicker);
        NumberPicker hourPicker = bottomSheetDialog.findViewById(R.id.hourPicker);
        ImageButton cancelBtn = bottomSheetDialog.findViewById(R.id.cancelBtn);
        TextView btnOk = bottomSheetDialog.findViewById(R.id.btnOk);

        if (datePicker == null || hourPicker == null || btnOk == null) {
            return; // যদি layout inflate না হয়
        }

        // 🔹 SharedPreferences থেকে language লোড করো
        String lang = LocaleHelper.getLanguage(context);

        Locale locale = lang.equals("bn") ? new Locale("bn", "BD") : Locale.ENGLISH;

        Date now = new Date();
        List<String> dates = new ArrayList<>();

        // ✅ তারিখ লিস্ট বানানো (আজ + পরবর্তী ৩ দিন)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", locale);
        dates.clear();

        for (int i = 0; i <= day; i++) {
            Date futureDate = new Date(now.getTime() + TimeUnit.DAYS.toMillis(i));
            dates.add(dateFormat.format(futureDate));
        }

        String[] dateArray = dates.toArray(new String[0]);

        datePicker.setMinValue(0);
        datePicker.setMaxValue(dateArray.length - 1);
        datePicker.setDisplayedValues(dateArray);
        datePicker.setWrapSelectorWheel(false);

        // প্রথমে বর্তমান দিনের জন্য টাইম জেনারেট করা
        generateHoursForDate(hourPicker, 0, now, locale);

        // ✅ Date change করলে time slot update হবে
        datePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            generateHoursForDate(hourPicker, newVal, now, locale);
        });

        cancelBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnOk.setOnClickListener(v -> {
            String selectedDate = dateArray[datePicker.getValue()];
            String selectedHour = hourPicker.getDisplayedValues()[hourPicker.getValue()];

            // Final date+time string
            String finalDateTime = selectedDate + ", " + selectedHour;

            targetTextView.setText(finalDateTime);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // ✅ Helper method: তারিখ অনুযায়ী time list বানানো
    private static void generateHoursForDate(NumberPicker hourPicker, int dateIndex, Date now, Locale locale) {
        List<String> hours = new ArrayList<>();

        int startHour = 0;
        if (dateIndex == 0) {
            startHour = Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(now));
        }

        for (int i = startHour; i < 24; i++) {
            String formattedHour;

            if (locale.getLanguage().equals("bn")) { // বাংলা লোকেল
                if (i == 0) formattedHour = "রাত ১২ টা";
                else if (i < 4) formattedHour = "রাত " + toBanglaNumber(i) + " টা";
                else if (i < 7) formattedHour = "ভোর " + toBanglaNumber(i) + " টা";
                else if (i < 12) formattedHour = "সকাল " + toBanglaNumber(i) + " টা";
                else if (i == 12) formattedHour = "দুপুর ১২ টা";
                else if (i < 16) formattedHour = "দুপুর " + toBanglaNumber(i - 12) + " টা";
                else if (i < 18) formattedHour = "বিকাল " + toBanglaNumber(i - 12) + " টা";
                else if (i < 20) formattedHour = "সন্ধ্যা " + toBanglaNumber(i - 12) + " টা";
                else formattedHour = "রাত " + toBanglaNumber(i - 12) + " টা";

            } else { // ইংরেজি লোকেল
                int hour12 = i % 12;
                if (hour12 == 0) hour12 = 12;
                String ampm = i < 12 ? "AM" : "PM";
                formattedHour = hour12 + " " + ampm;
            }

            hours.add(formattedHour);
        }

        String[] hourArray = hours.toArray(new String[0]);

        // পুরনো values remove
        hourPicker.setDisplayedValues(null);

        // Min/Max set করা
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(hourArray.length - 1);

        // নতুন values assign করা
        hourPicker.setDisplayedValues(hourArray);
        hourPicker.setWrapSelectorWheel(false);
    }

    public static String toBanglaNumber(int number) {
        char[] banglaDigits = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};
        String numStr = String.valueOf(number);
        StringBuilder banglaNum = new StringBuilder();
        for (char c : numStr.toCharArray()) {
            if (Character.isDigit(c)) {
                banglaNum.append(banglaDigits[c - '0']);
            } else {
                banglaNum.append(c);
            }
        }
        return banglaNum.toString();
    }


    public static String generateReferralCode() {
        String lettersAndDigits = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String digits = "0123456789";

        StringBuilder code = new StringBuilder("BDH");

        Random random = new Random();

        // ১ম character after BDH: must be a number
        code.append(digits.charAt(random.nextInt(digits.length())));

        // যেহেতু BDH (3) + digit (1) = 4 হয়ে গেছে
        // তাই বাকি 4 character generate করতে হবে
        for (int i = 0; i < 4; i++) {
            code.append(lettersAndDigits.charAt(random.nextInt(lettersAndDigits.length())));
        }

        return code.toString(); // মোট length 8 হবে
    }






}
