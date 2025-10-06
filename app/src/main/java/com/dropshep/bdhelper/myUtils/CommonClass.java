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
import android.util.Log;
import android.util.Pair;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dropshep.bdhelper.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    public static int getSubCategoryName(String subCategoryId) {
        switch (subCategoryId) {
            case MyUtils.SUB_TRUCK_ID: return R.string.truck;
            case MyUtils.SUB_PICKUP_ID: return R.string.pickup;
            case MyUtils.SUB_COVERED_VAN_ID: return R.string.coveredvan;
            case MyUtils.SUB_TRAILER_ID: return R.string.trailer;
            case MyUtils.SUB_LOW_BED_ID: return R.string.low_bed;
            case MyUtils.SUB_FREEZER_VAN_ID: return R.string.freezervan;
            case MyUtils.SUB_DUMP_TRUCK_ID: return R.string.dump_truck;
            case MyUtils.SUB_CHARGER_VAN_ID: return R.string.charger_van;

            case MyUtils.SUB_CAR_ID: return R.string.car;
            case MyUtils.SUB_MICROBUS_ID: return R.string.microbus;
            case MyUtils.SUB_AMBULANCE_ID: return R.string.ambulance;

            case MyUtils.HOME_SHIFTING_ID: return R.string.home_office_shifting;

            case MyUtils.SUB_EXCAVATOR_ID: return R.string.excavator;
            case MyUtils.SUB_RICE_TRANSPLANTER_ID: return R.string.rice_transplanter;
            case MyUtils.SUB_TRACTOR_ID: return R.string.tractor;
            case MyUtils.HARVESTER_MACHINE_ID: return R.string.harvester;

            case MyUtils.SUB_DRIVER_ID: return R.string.driver;
            case MyUtils.SUB_MECHANIC_ID: return R.string.mechanic;
            case MyUtils.SUB_ELECTRICIAN_ID: return R.string.electrician;
            case MyUtils.SUB_STOVE_TECHNICIAN_ID: return R.string.stove_mechanic;
            case MyUtils.SUB_PLUMBER_ID: return R.string.plumber;

            default: return R.string.app_name;
        }
    }

    // Helper method to get correct drawable for subCategory
    public static int getIconForSubCategory(String subCatId) {
        switch (subCatId) {
            case MyUtils.SUB_TRUCK_ID: return R.drawable.ic_truck;
            case MyUtils.SUB_PICKUP_ID: return R.drawable.ic_pickup;
            case MyUtils.SUB_COVERED_VAN_ID: return R.drawable.ic_covered_van;
            case MyUtils.SUB_TRAILER_ID: return R.drawable.ic_trailer;
            case MyUtils.SUB_LOW_BED_ID: return R.drawable.ic_low_bed;
            case MyUtils.SUB_FREEZER_VAN_ID: return R.drawable.ic_freezer_van;
            case MyUtils.SUB_DUMP_TRUCK_ID: return R.drawable.ic_dump_truck;
            case MyUtils.SUB_CHARGER_VAN_ID: return R.drawable.ic_charger_van;

            case MyUtils.SUB_CAR_ID: return R.drawable.ic_car;
            case MyUtils.SUB_MICROBUS_ID: return R.drawable.ic_microbus;
            case MyUtils.SUB_AMBULANCE_ID: return R.drawable.ic_ambulance;

            case MyUtils.HOME_SHIFTING_ID: return R.drawable.ic_home_shift;

            case MyUtils.SUB_EXCAVATOR_ID: return R.drawable.ic_excavator;
            case MyUtils.SUB_RICE_TRANSPLANTER_ID: return R.drawable.ic_rice_transplanter;
            case MyUtils.SUB_TRACTOR_ID: return R.drawable.ic_tractor;
            case MyUtils.HARVESTER_MACHINE_ID: return R.drawable.ic_harvester;

            case MyUtils.SUB_DRIVER_ID: return R.drawable.ic_driver;
            case MyUtils.SUB_MECHANIC_ID: return R.drawable.ic_mechanic;
            case MyUtils.SUB_ELECTRICIAN_ID: return R.drawable.ic_electrician;
            case MyUtils.SUB_STOVE_TECHNICIAN_ID: return R.drawable.ic_stove_technician;
            case MyUtils.SUB_PLUMBER_ID: return R.drawable.ic_plumbing;

            default: return R.drawable.ic_trending; // একটা ডিফল্ট আইকন রাখো
        }
    }


    public static long parseDateStringToMillis(String dateStr) {
        if (dateStr == null) return 0L;
        String s = dateStr.trim();
        if (s.isEmpty()) return 0L;

        // যদি পুরোটা সংখ্যা হয় (epoch)
        if (s.matches("^\\d+$")) {
            try {
                long v = Long.parseLong(s);
                // seconds (10 digits) or small number -> convert to ms
                // threshold 100_000_000_000L (1e11) chosen to distinguish seconds vs ms safely
                if (v < 100_000_000_000L) {
                    return v * 1000L;
                } else {
                    return v;
                }
            } catch (NumberFormatException ignored) { /* fallback to pattern parsing */ }
        }

        // চেষ্টা করব কিছু সাধারণ date patterns দিয়ে
        String[] patterns = new String[] {
                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "dd/MM/yyyy HH:mm",
                "dd-MM-yyyy HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String p : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.getDefault());
                sdf.setLenient(false);
                Date d = sdf.parse(s);
                if (d != null) return d.getTime();
            } catch (ParseException ignored) { }
        }

        // parse না হলে 0 ফেরত দিন (বা আপনি চাইলে -1)
        //Log.d("Status", "parseDateStringToMillis: unable to parse '" + dateStr + "'");
        return 0L;
    }

    // আজকের দিনের শুরু (midnight 00:00:00) এর time in millis রিটার্ন করবে
    public static long getStartOfTodayMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // 🔑 OrderID জেনারেটর
    public interface OrderIdCallback {
        void onSuccess(String orderId);
        void onFailure(Exception e);
    }

    public static void generateOrderId(FirebaseFirestore db,
                                       String collectionPath,
                                       String fieldPath,
                                       String prefix,
                                       int initialDigitLength,
                                       OrderIdCallback callback) {

        db.collection(collectionPath)
                .orderBy(fieldPath, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int maxNum = 0;
                    int digitLength = initialDigitLength;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String lastId = doc.getString(fieldPath);
                        if (lastId != null && lastId.startsWith(prefix)) {
                            try {
                                String numPart = lastId.substring(prefix.length());
                                int num = Integer.parseInt(numPart);
                                if (num > maxNum) maxNum = num;

                                // Update digitLength dynamically
                                digitLength = Math.max(numPart.length(), String.valueOf(maxNum + 1).length());

                            } catch (Exception ignored) {}
                        }
                    }

                    // Build new OrderID
                    String pattern = prefix + "%0" + digitLength + "d";
                    String newOrderId = String.format(Locale.ENGLISH, pattern, maxNum + 1);
                    callback.onSuccess(newOrderId);

                })
                .addOnFailureListener(callback::onFailure);
    }



    //Partner App Helper
    public static String millisToTimeWithLocal(Context context, String millis) {
        long timestamp = Long.parseLong(millis);

        // Context থেকে language বের করি
        String lang = LocaleHelper.getLanguage(context); // "bn" বা "en"
        Locale locale = lang.equals("bn") ? new Locale("bn", "BD") : Locale.ENGLISH;

        Date date = new Date(timestamp);
        Calendar cal = Calendar.getInstance(locale);
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH); // 0-based
        int year = cal.get(Calendar.YEAR);
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        // Month নাম গুলো
        String[] monthsBn = {"জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
                "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"};
        String[] monthsEn = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        if (lang.equals("bn")) {
            // ===== বাংলা =====
            String dayBn = Replacement.ReplacementNumberEnToBnInInteger(day);
            String monthBn = monthsBn[month];

            String formattedHour;
            if (hour24 == 0) formattedHour = "রাত ১২ টা";
            else if (hour24 < 4) formattedHour = "রাত " + Replacement.ReplacementNumberEnToBnInInteger(hour24) + " টা";
            else if (hour24 < 7) formattedHour = "ভোর " + Replacement.ReplacementNumberEnToBnInInteger(hour24) + " টা";
            else if (hour24 < 12) formattedHour = "সকাল " + Replacement.ReplacementNumberEnToBnInInteger(hour24) + " টা";
            else if (hour24 == 12) formattedHour = "দুপুর ১২ টা";
            else if (hour24 < 16) formattedHour = "দুপুর " + Replacement.ReplacementNumberEnToBnInInteger(hour24 - 12) + " টা";
            else if (hour24 < 18) formattedHour = "বিকাল " + Replacement.ReplacementNumberEnToBnInInteger(hour24 - 12) + " টা";
            else if (hour24 < 20) formattedHour = "সন্ধ্যা " + Replacement.ReplacementNumberEnToBnInInteger(hour24 - 12) + " টা";
            else formattedHour = "রাত " + Replacement.ReplacementNumberEnToBnInInteger(hour24 - 12) + " টা";

            return dayBn + " " + monthBn + ",  " + formattedHour;

        }
        else {
            // ===== ইংরেজি =====
            String monthEn = monthsEn[month];
            int hour12 = hour24 % 12;
            if (hour12 == 0) hour12 = 12;
            String ampm = (hour24 < 12) ? "am" : "pm";

            return String.format(Locale.ENGLISH, "%02d %s, %02d:%02d %s",
                    day, monthEn, hour12, minute, ampm);
        }
    }


    public static Pair<String, String> formatAddress(String text) {
        if (text == null || text.isEmpty()) return new Pair<>("", "");

        String[] parts = text.split(",");

        if (parts.length == 1) {
            // কোন কমা নেই, সবটুকু প্রথম লাইনে
            return new Pair<>(text.trim(), "");
        }

        StringBuilder firstLine = new StringBuilder();
        StringBuilder secondLine = new StringBuilder();

        // মোট কমা count দেখে ভাগ করব
        int mid = parts.length / 2; // প্রায় দুই ভাগে ভাগ

        for (int i = 0; i < parts.length; i++) {
            if (i < mid) {
                if (firstLine.length() > 0) firstLine.append(", ");
                firstLine.append(parts[i].trim());
            } else {
                if (secondLine.length() > 0) secondLine.append(", ");
                secondLine.append(parts[i].trim());
            }
        }

        return new Pair<>(firstLine.toString(), secondLine.toString());
    }




}
