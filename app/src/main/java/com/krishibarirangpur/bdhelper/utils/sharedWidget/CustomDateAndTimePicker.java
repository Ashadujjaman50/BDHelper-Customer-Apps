package com.krishibarirangpur.bdhelper.utils.sharedWidget;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CustomDateAndTimePicker {

    public interface DateTimeCallback {
        void onDateTimeSelected(String displayDateTime, String returnDateTime, long millis);
    }

    public static void showDateTimePicker(Context context, int day, CustomDateAndTimePicker.DateTimeCallback callback) {
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
                else if (i < 4) formattedHour = "রাত " + Replacement.ReplacementNumberEnToBnInInteger(i) + " টা";
                else if (i < 7) formattedHour = "ভোর " + Replacement.ReplacementNumberEnToBnInInteger(i) + " টা";
                else if (i < 12) formattedHour = "সকাল " + Replacement.ReplacementNumberEnToBnInInteger(i) + " টা";
                else if (i == 12) formattedHour = "দুপুর ১২ টা";
                else if (i < 16) formattedHour = "দুপুর " + Replacement.ReplacementNumberEnToBnInInteger(i - 12) + " টা";
                else if (i < 18) formattedHour = "বিকাল " + Replacement.ReplacementNumberEnToBnInInteger(i - 12) + " টা";
                else if (i < 20) formattedHour = "সন্ধ্যা " + Replacement.ReplacementNumberEnToBnInInteger(i - 12) + " টা";
                else formattedHour = "রাত " + Replacement.ReplacementNumberEnToBnInInteger(i - 12) + " টা";

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



}
