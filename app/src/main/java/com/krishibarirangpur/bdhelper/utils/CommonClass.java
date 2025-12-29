package com.krishibarirangpur.bdhelper.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.OrderModel;
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

    public static void socialMediaClickToResponse(ImageView facebookIV, ImageView twitterIV,
                                                  ImageView instagramIV, ImageView linkedinIV) {
        setClick(facebookIV, MyUtils.facebook_page_url);
        setClick(twitterIV, MyUtils.twitter_x_url);
        setClick(instagramIV, MyUtils.instagram_url);
        setClick(linkedinIV, MyUtils.linkedIn_url);
    }

    private static void setClick(ImageView view, String url) {
        if (url != null && !url.isEmpty()) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(intent);
            });
        } else {
            view.setOnClickListener(null);  // disable click
        }
    }

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

    //Helper subCategory Id to String subCategory Name
    public static String getSubCategoryName(Context context, String subCategoryId) {
        int resId;

        switch (subCategoryId) {
            case MyUtils.SUB_TRUCK_ID: resId = R.string.truck; break;
            case MyUtils.SUB_PICKUP_ID: resId = R.string.pickup; break;
            case MyUtils.SUB_COVERED_VAN_ID: resId = R.string.coveredvan; break;
            case MyUtils.SUB_TRAILER_ID: resId = R.string.trailer; break;
            case MyUtils.SUB_LOW_BED_ID: resId = R.string.low_bed; break;
            case MyUtils.SUB_FREEZER_VAN_ID: resId = R.string.freezervan; break;
            case MyUtils.SUB_DUMP_TRUCK_ID: resId = R.string.dump_truck; break;
            case MyUtils.SUB_CHARGER_VAN_ID: resId = R.string.charger_van; break;

            case MyUtils.SUB_CAR_ID: resId = R.string.car; break;
            case MyUtils.SUB_MICROBUS_ID: resId = R.string.microbus; break;
            case MyUtils.SUB_AMBULANCE_ID: resId = R.string.ambulance; break;

            case MyUtils.HOME_SHIFTING_ID: resId = R.string.home_office_shifting; break;

            case MyUtils.SUB_EXCAVATOR_ID: resId = R.string.excavator; break;
            case MyUtils.SUB_RICE_TRANSPLANTER_ID: resId = R.string.rice_transplanter; break;
            case MyUtils.SUB_TRACTOR_ID: resId = R.string.tractor; break;
            case MyUtils.HARVESTER_MACHINE_ID: resId = R.string.harvester; break;

            case MyUtils.SUB_DRIVER_ID: resId = R.string.driver; break;
            case MyUtils.SUB_MECHANIC_ID: resId = R.string.mechanic; break;
            case MyUtils.SUB_ELECTRICIAN_ID: resId = R.string.electrician; break;
            case MyUtils.SUB_STOVE_TECHNICIAN_ID: resId = R.string.stove_mechanic; break;
            case MyUtils.SUB_PLUMBER_ID: resId = R.string.plumber; break;

            default: resId = R.string.app_name;
        }

        return context.getString(resId);
    }


    // Helper method to get correct drawable for subCategory
    public static int getIconForSubCategory(String subCategoryId) {
        switch (subCategoryId) {
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

    /**
     * Convert a millisecond String to long
     */
    public static long parseMillis(String millisString) {
        if (millisString == null || millisString.isEmpty()) return 0;
        try {
            return Long.parseLong(millisString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get today's start time in milliseconds (00:00:00)
     */
    public static long getTodayStartMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // 🔹 Format timestamp to Date Formate dd/MM/YYYY HH:mm:ss aa
    public static String formatTime(String timeMillis, String pattern) {
        try {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(Long.parseLong(timeMillis));
            String formatted = DateFormat.format(pattern, calendar).toString();
            return formatted.replace("AM", "am").replace("PM", "pm");
        } catch (Exception e) {
            return "";
        }
    }


    //10% Commission added to replacement
    //When commission 10%
    public static String getRoundedTenPercentValue(String amountStr, double percent) {
        try {
            double amount = Double.parseDouble(amountStr);

            // 🔹 নির্দিষ্ট percent অনুযায়ী বাড়ানো
            double addPercent = amount * (percent / 100.0);

            // 🔹 addPercent যদি 100-এর কম হয়
            if (addPercent < 100) {
                if (addPercent < 30){
                    return String.valueOf(amount+20);
                }
                else {
                    return String.valueOf(amount+addPercent);
                }
            }

            double increased = amount + addPercent;

            // 🔹 শেষ দুই ডিজিট বের করো
            long roundedValue = (long) increased;
            long lastTwoDigits = roundedValue % 100;

            // 🔹 nearest 100 বা 50 অনুযায়ী রাউন্ড করো
            if (lastTwoDigits < 50) {
                roundedValue = roundedValue - lastTwoDigits; // নিচে round
            } else {
                roundedValue = roundedValue + (100 - lastTwoDigits); // উপরে round
            }

            // 🔹 Local number format এ রিটার্ন করো
            return String.valueOf(roundedValue);

        }
        catch (Exception e) {
            e.printStackTrace();
            return amountStr;
        }
    }


    /** ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅
     * Data base Related Function and Logic Start
     * All Logical operation
     */

    // 🔑 OrderID জেনারেটর
    public interface OrderIdCallback {
        void onSuccess(String orderId);
        void onFailure(Exception e);
    }

    public static void generateOrderId(FirebaseFirestore db, String collectionPath,
                                       String fieldPath, String prefix,
                                       int initialDigitLength, OrderIdCallback callback) {

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



    /**
     * Starts a countdown from a given timestamp + additional hours and updates the given TextView.
     *
     * @param timestamp      The original timestamp in milliseconds
     * @param hoursToAdd     Hours to add to the timestamp
     * @param countdownTv    TextView to update with countdown
     */
    private static CountDownTimer countDownTimer;
    @SuppressLint("SetTextI18n")
    public static void startConditionalCountdown(long timestamp, int hoursToAdd, String orderStatus,
                                                 TextView countdownTv, View countdownLayout) {

        // Cancel previous timer if running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if ("pending".equalsIgnoreCase(orderStatus) || "process".equalsIgnoreCase(orderStatus)) {
            long countdownEndMillis = timestamp + (long) hoursToAdd * 60 * 60 * 1000;
            long currentTimeMillis = System.currentTimeMillis();
            long millisUntilFinished = countdownEndMillis - currentTimeMillis;

            if (millisUntilFinished > 0) {
                countdownLayout.setVisibility(View.VISIBLE);

                countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000 % 60;
                        long minutes = millisUntilFinished / (1000 * 60) % 60;
                        long hours = millisUntilFinished / (1000 * 60 * 60);

                        @SuppressLint("DefaultLocale")
                        String timeLeft = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        countdownTv.setText(timeLeft);
                    }


                    @Override
                    public void onFinish() {
                        countdownTv.setText("00:00:00");
                        // 5 সেকেন্ড delay দিয়ে hide
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            countdownLayout.setVisibility(View.GONE);
                        }, 3000); // 3000 milliseconds = 3 seconds
                    }
                }.start();
            } else {
                // Time already expired
                countdownTv.setText("00:00:00");
                countdownLayout.setVisibility(View.GONE); // hide layout when countdown ends
            }

        } else {
            // Stop countdown and hide layout
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            countdownLayout.setVisibility(View.GONE);
        }
    }



    public static void getOrderInfoById(String orderId, FirestoreOrderCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<OrderModel> orderList = new ArrayList<>();

        db.collection("orders")
                .whereEqualTo("orderInfo.orderId", orderId) // nested field match
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            OrderModel orderModel = doc.toObject(OrderModel.class);
                            if (orderModel != null) {
                                orderList.add(orderModel);
                            }
                        }
                        callback.onSuccess(orderList);
                    } else {
                        callback.onFailure(new Exception("No order found for ID: " + orderId));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }


    // Callback interface
    public interface FirestoreOrderCallback {
        void onSuccess(ArrayList<OrderModel> orderList);
        void onFailure(Exception e);
    }



}
