package com.krishibarirangpur.bdhelper.utils;

import android.content.Context;

import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.Collections;
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

        Collections.addAll(uniqueParts, parts);

        return String.join(", ", uniqueParts);
    }


    public static boolean cityCheck( String cityName){

        boolean cityCh = false;

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
                break;
        }

        return cityCh;
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

    // 🔹 Bangla → English
    public static String ReplacementNumberBnToEn(String replacement) {
        if (replacement == null) return "";

        String[] bn = {"০","১","২","৩","৪","৫","৬","৭","৮","৯"};
        String[] en = {"0","1","2","3","4","5","6","7","8","9"};

        for (int i = 0; i < 10; i++) {
            replacement = replacement.replace(bn[i], en[i]);
        }

        replacement = replacement.replace("১০","10"); // বিশেষ ১০ এর জন্য
        return replacement;
    }

    // 🔹 English → Bangla
    public static String ReplacementNumberEnToBn(String replacement) {
        if (replacement == null) return "";

        String[] en = {"0","1","2","3","4","5","6","7","8","9"};
        String[] bn = {"০","১","২","৩","৪","৫","৬","৭","৮","৯"};

        for (int i = 0; i < 10; i++) {
            replacement = replacement.replace(en[i], bn[i]);
        }

        replacement = replacement.replace("10","১০"); // বিশেষ ১০ এর জন্য
        return replacement;
    }

    // 🔹 Auto Local Method In Number With String
    public static String ReplacementNumberInLocal(Context context, String number){
        String lang = LocaleHelper.getLanguage(context); // en or bn

        if ("bn".equals(lang)){
            return ReplacementNumberEnToBn(number);
        }
        else {
            return ReplacementNumberBnToEn(number);
        }
    }

    // 🔹 Replacement Number En To Bn With Int
    public static String ReplacementNumberEnToBnInInteger(int number){
        String num = String.valueOf(number);

        return ReplacementNumberEnToBn(num);
    }

    // 🔹 Auto Local Method In Person
    public static String ReplacementPersonInLocal(Context context, String number) {
        String lang = LocaleHelper.getLanguage(context); // "en" or "bn"

        if ("bn".equals(lang)) {
            String replace = ReplacementNumberEnToBn(number);
            // যদি number-এ আগেই "person" থাকে, তাকে বাংলা "জন"-এ convert
            if (replace.toLowerCase().contains("person")) {
                // ignore case
                return replace.replaceAll("(?i)person", "জন");
            }
            else if (!replace.contains("জন")) {
                // যদি "জন" না থাকে, default হিসেবে যোগ করো
                return replace.trim() + " জন";
            }
            else {
                // যদি number-এ already "জন" থাকে, 그대로 রাখো
                return replace;
            }
        }
        else {
            String replace = ReplacementNumberBnToEn(number);
            // English case
            if (replace.contains("জন")) {
                // "জন" কে "person"-এ convert
                return replace.replace("জন", "person");
            } else if (!replace.toLowerCase().contains("person")) {
                // যদি "person" না থাকে, default হিসেবে যোগ করো
                return replace.trim() + " person";
            } else {
                // যদি replace-এ already "person" থাকে, 그대로 রাখো
                return replace;
            }
        }
    }


    // 🔹 Auto Local Method In Experience
    public static String ReplacementExperienceInLocal(Context context, String number){
        if (number == null) return "";
        String lang = LocaleHelper.getLanguage(context); // en or bn

        if ("bn".equals(lang)){
            String replace = ReplacementNumberEnToBn(number);
            if (replace.toLowerCase().contains("year")) {
                return replace.replaceAll("(?i)year", "বছর");
            } else if (!replace.contains("বছর")) {
                return replace.trim() + " বছর";
            } else {
                return replace;
            }
        }
        else {
            String replace = ReplacementNumberBnToEn(number);
            if (replace.contains("বছর")) {
                return replace.replace("বছর", "Year");
            } else if (!replace.toLowerCase().contains("year")) {
                return replace.trim() + " Year";
            } else {
                return replace;
            }
        }
    }


    // 🔹 Auto Local Method In Qty
    public static String ReplacementQtyToLocal(Context context, String quantity) {
        String lang = LocaleHelper.getLanguage(context); // en or bn

        String clean = quantity.replace("টি", "").trim();
        if ("bn".equals(lang)) {
            // আগে যেকোনো "টি" remove করো

            // number convert
            String num = ReplacementNumberEnToBn(clean);

            // একবারই " টি" যোগ করো
            return num + " টি";
        } else {
            // BN → EN (আগে "টি" remove)
            return ReplacementNumberBnToEn(clean);
        }
    }


    // 🔹 Dynamic minutes in local
    public static String getLocalMinutes(Context context, String quantity) {
        String lang = LocaleHelper.getLanguage(context);

        // শুধু digit বের করো (যেমন "5 minutes" → "5")
        String digits = quantity.replaceAll("\\D+", ""); // non-digit remove

        if (digits.isEmpty()) {
            digits = "0";
        }

        if ("bn".equals(lang)) {
            return ReplacementNumberEnToBn(digits) + " মিনিট";
        } else {
            return digits + " minutes";
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

    public static String convertVehicleRegByLocale(Context context, String vehicleRegNo) {
        if (vehicleRegNo == null || vehicleRegNo.isEmpty()) return "";

        // 🔹 Locale detect করা
        String lang = LocaleHelper.getLanguage(context);

        // 🔹 English হলে return করবে
        if ("en".equals(lang)) {
            return vehicleRegNo;
        }

        // 🔹 বাংলা হলে নিচের মতো convert করবে
        String result = vehicleRegNo;

        // 🔹 Step 1: Metro অংশ বাংলা করা
        for (int i = 0; i < MyUtils.METRO_LIST_ENG.length; i++) {
            String eng =  MyUtils.METRO_LIST_ENG[i];
            String ban =  MyUtils.METRO_LIST_BAN[i];
            if (result.toLowerCase().contains(eng.toLowerCase())) {
                result = result.replaceAll("(?i)" + eng, ban);
                break;
            }
        }

        // 🔹 Step 2: Serial অংশ বাংলা করা
        for (int i = 0; i <  MyUtils.SERIAL_ENG.length; i++) {
            String eng =  MyUtils.SERIAL_ENG[i];
            String ban =  MyUtils.SERIAL_BAN[i];
            if (result.toLowerCase().contains(eng.toLowerCase())) {
                result = result.replaceAll("(?i)" + eng, ban);
                break;
            }
        }

        // 🔹 Step 3: সংখ্যা বাংলা করা
        result = result
                .replace("0", "০")
                .replace("1", "১")
                .replace("2", "২")
                .replace("3", "৩")
                .replace("4", "৪")
                .replace("5", "৫")
                .replace("6", "৬")
                .replace("7", "৭")
                .replace("8", "৮")
                .replace("9", "৯");

        return result;
    }

    /**
     * ডাটা ম্যাচিং এর জন্য স্ট্রিং নরমালাইজ করে (বাংলা সংখ্যা -> ইংরেজি এবং লোয়ারকেস)
     */
    public static String normalizeMetadata(String input) {
        if (input == null) return "";

        // ১. বাংলা সংখ্যাকে ইংরেজিতে রূপান্তর
        String result = ReplacementNumberBnToEn(input);

        // ২. সাধারণ বাংলা শব্দগুলোকে ইংরেজিতে রূপান্তর এবং লোয়ারকেস
        result = result.toLowerCase()
                .replace("ফিট", "feet")
                .replace("টন", "ton")
                .replace("বছর", "year")
                .replace("জন", "person")
                .replace("টি", "")
                .replace(" ", ""); // স্পেস রিমুভ করছি নিখুঁতভাবে ম্যাচ করার জন্য

        return result.trim();
    }


}
