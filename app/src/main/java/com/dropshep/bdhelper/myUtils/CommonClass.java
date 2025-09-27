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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dropshep.bdhelper.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

    public interface DateTimeCallback {
        void onDateTimeSelected(String displayDateTime, String returnDateTime, long millis);
    }

    public static void showDateTimePicker(Context context, int day, DateTimeCallback callback) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_date_time_picker);

        NumberPicker datePicker = bottomSheetDialog.findViewById(R.id.datePicker);
        NumberPicker hourPicker = bottomSheetDialog.findViewById(R.id.hourPicker);
        ImageButton cancelBtn = bottomSheetDialog.findViewById(R.id.cancelBtn);
        TextView btnOk = bottomSheetDialog.findViewById(R.id.btnOk);

        if (datePicker == null || hourPicker == null || btnOk == null) {
            return;
        }

        String lang = LocaleHelper.getLanguage(context);
        Locale locale = lang.equals("bn") ? new Locale("bn", "BD") : Locale.ENGLISH;

        Date now = new Date();
        List<String> dates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM", locale);

        for (int i = 0; i <= day; i++) {
            Date futureDate = new Date(now.getTime() + TimeUnit.DAYS.toMillis(i));
            dates.add(dateFormat.format(futureDate));
        }

        String[] dateArray = dates.toArray(new String[0]);
        datePicker.setMinValue(0);
        datePicker.setMaxValue(dateArray.length - 1);
        datePicker.setDisplayedValues(dateArray);
        datePicker.setWrapSelectorWheel(false);

        // 🔹 hourPicker ডাটার সাথে আসল hour index রাখব
        int[] hourValues = generateHoursForDate(hourPicker, 0, now, locale);

        datePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int[] newHourValues = generateHoursForDate(hourPicker, newVal, now, locale);
            hourPicker.setTag(newHourValues); // নতুন hour array save
        });

        hourPicker.setTag(hourValues);

        cancelBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnOk.setOnClickListener(v -> {
            String selectedDate = dateArray[datePicker.getValue()];
            String selectedHourText = hourPicker.getDisplayedValues()[hourPicker.getValue()];

            int[] hourArray = (int[]) hourPicker.getTag();
            int selectedHour24 = hourArray[hourPicker.getValue()];

            // 🔹 Final human-readable (UI লোকেল)
            String displayDateTime = selectedDate + ", " + selectedHourText;

            // 🔹 Calendar বানানো (সবসময় English return এর জন্য)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_YEAR, datePicker.getValue());
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour24);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // 🔹 Return সবসময় English
            String returnDateTime = new SimpleDateFormat("dd MMMM yyyy, hh:mm aa", Locale.ENGLISH)
                    .format(calendar.getTime());

            long millis = calendar.getTimeInMillis();

            callback.onDateTimeSelected(displayDateTime, returnDateTime, millis);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // ✅ Helper method: hour list বানানো + আসল hour index return করা
    private static int[] generateHoursForDate(NumberPicker hourPicker, int dateIndex, Date now, Locale locale) {
        List<String> hours = new ArrayList<>();
        List<Integer> hourValues = new ArrayList<>();

        int startHour = 0;
        if (dateIndex == 0) {
            startHour = Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(now));
        }

        for (int i = startHour; i < 24; i++) {
            String formattedHour;

            if (locale.getLanguage().equals("bn")) {
                if (i == 0) formattedHour = "রাত ১২ টা";
                else if (i < 4) formattedHour = "রাত " + toBanglaNumber(i) + " টা";
                else if (i < 7) formattedHour = "ভোর " + toBanglaNumber(i) + " টা";
                else if (i < 12) formattedHour = "সকাল " + toBanglaNumber(i) + " টা";
                else if (i == 12) formattedHour = "দুপুর ১২ টা";
                else if (i < 16) formattedHour = "দুপুর " + toBanglaNumber(i - 12) + " টা";
                else if (i < 18) formattedHour = "বিকাল " + toBanglaNumber(i - 12) + " টা";
                else if (i < 20) formattedHour = "সন্ধ্যা " + toBanglaNumber(i - 12) + " টা";
                else formattedHour = "রাত " + toBanglaNumber(i - 12) + " টা";

            } else {
                int hour12 = i % 12;
                if (hour12 == 0) hour12 = 12;
                String ampm = i < 12 ? "AM" : "PM";
                formattedHour = hour12 + " " + ampm;
            }

            hours.add(formattedHour);
            hourValues.add(i);
        }

        String[] hourArray = hours.toArray(new String[0]);

        hourPicker.setDisplayedValues(null);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(hourArray.length - 1);
        hourPicker.setDisplayedValues(hourArray);
        hourPicker.setWrapSelectorWheel(false);

        // int array return (hour index list)
        return hourValues.stream().mapToInt(Integer::intValue).toArray();
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

    /**
     * Field validate করে error দেখাবে।
     * EditText আর TextView দুইটাই handle করবে।
     */
    public static boolean validateField(TextView field) {
        String text = field.getText().toString().trim();

        if (TextUtils.isEmpty(text)) {
            field.setBackgroundResource(R.drawable.bg_edit_text_error);

            field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    field.setBackgroundResource(R.drawable.bg_edit_text);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            return true; // invalid
        }

        return false; // valid
    }


}
