package com.dropshep.bdhelper.myUtils;

import android.content.Context;

import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Locale;

public class Replacement {

    public static String checkAddress(String address, String sub_district, String district){

        address = address.replace(sub_district, "");
        address = address.replace(district, "");

        return address;
    }

    public static String checkString(String checkString){

        checkString = checkString.replace(", , ,", ",");
        checkString = checkString.replace(", ,", ",");
        checkString = checkString.replace(",,", ",");

        return checkString;
    }

    public static String removeDuplicateAddressParts(String address) {
        String[] parts = address.split("\\s*,\\s*");  // Comma দিয়ে স্প্লিট করে ফেলছি
        LinkedHashSet<String> uniqueParts = new LinkedHashSet<>();

        for (String part : parts) {
            uniqueParts.add(part);  // ডুপ্লিকেট হলে add হবে না
        }

        return String.join(", ", uniqueParts);
    }

    public static String cleanAddress(String address) {
        // Step 1: Remove redundant commas
        address = address.replace(", , ,", ",");
        address = address.replace(", ,", ",");
        address = address.replace(",,", ",");

        // Step 2: Remove duplicate parts
        String[] parts = address.split("\\s*,\\s*");  // Split by comma
        LinkedHashSet<String> uniqueParts = new LinkedHashSet<>();

        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                uniqueParts.add(part.trim());  // Maintain order, avoid duplicates
            }
        }

        // Step 3: Join back into a cleaned string
        return String.join(", ", uniqueParts);
    }

    public static boolean cityCheck(boolean cityCh, String cityName){

        cityCh = false;

        switch (cityName){
            case "Dhaka":
            case "ঢাকা":
            case "Chattogram":
            case "চট্টগ্রাম":
            case "Rajshahi":
            case "রাজশাহী":
            case "Rangpur":
            case "রংপুর":
            case "Khulna":
            case "খুলনা":
            case "Barishal":
            case "বরিশাল":
            case "Sylhet":
            case "সিলেট": {
                cityCh = true;
            }
            break;
            default:
                cityCh = false;
                break;
        }

        return cityCh;
    }


    public static String NumberFormatInBangla(double number) {
        try {

            // ইংরেজি সংখ্যাকে বাংলাদেশি লোকেল ফরম্যাটে নিয়ে আসা
            NumberFormat formatter = NumberFormat.getInstance(new Locale("bn", "BD"));
            //formatter.setMinimumFractionDigits(2);
            //formatter.setMaximumFractionDigits(2);

            return formatter.format(number);
        } catch (Exception e) {
            e.printStackTrace();
            return "০";
        }
    }

    public static String NumberFormatFullTimer(String value) {
        if (Locale.getDefault().getLanguage().equals("bn")) {
            char[] banglaDigits = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};
            StringBuilder result = new StringBuilder();

            for (char c : value.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    result.append(banglaDigits[c - '0']);
                } else {
                    result.append(c); // যেমন ':', '.', space ইত্যাদি
                }
            }
            return result.toString();
        } else {
            return value;
        }
    }

    public static String getLocalizedDistrict(Context context, String districtEng) {
        if (districtEng == null) return "";

        String lang = LocaleHelper.getLanguage(context);
        if (!"bn".equals(lang)) return districtEng;

        String[] engList = MyUtils.DISTRICT_ENG;
        String[] banList = MyUtils.DISTRICT_BAN;

        for (int i = 0; i < engList.length; i++) {
            if (engList[i].equalsIgnoreCase(districtEng)) {
                return banList[i];
            }
        }

        return districtEng; // fallback
    }

    public static String getLocalizedMFS(Context context, String mfs){
        if (mfs == null) return "";

        String lang = LocaleHelper.getLanguage(context);
        if (!"bn".equals(lang)) return mfs;

        String[] engList = MyUtils.MFS_LIST_ENG;
        String[] banList = MyUtils.MFS_LIST_BAN;

        for (int i = 0; i < engList.length; i++) {
            if (engList[i].equalsIgnoreCase(mfs)) {
                return banList[i];
            }
        }

        return mfs; // fallback
    }

}
