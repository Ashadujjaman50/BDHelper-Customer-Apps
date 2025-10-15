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

            // যদি "জন" না থাকে তাহলে যোগ করো
            if (!replace.contains("জন")) {
                replace = replace.trim() + " জন";
            }

            // যদি ভুলক্রমে "person" থাকে তাহলে সরাও
            replace = replace.replace("person", "জন");
            return replace;
        } else {
            String replace = ReplacementNumberBnToEn(number);

            // যদি "person" না থাকে তাহলে যোগ করো
            if (!replace.toLowerCase().contains("person")) {
                replace = replace.trim() + " person";
            }

            // যদি ভুলক্রমে "জন" থাকে তাহলে সরাও
            replace = replace.replace("জন", "person");
            return replace;
        }
    }


    // 🔹 Auto Local Method In Experience
    public static String ReplacementExperienceInLocal(Context context, String number){
        String lang = LocaleHelper.getLanguage(context); // en or bn

        if ("bn".equals(lang)){
            String replace = ReplacementNumberEnToBn(number);
            replace = replace.replace("Year", "বছর");
            return replace;
        }
        else {
            String replace = ReplacementNumberBnToEn(number);
            replace = replace.replace( "বছর", "Year");
            return replace;
        }
    }

    //------(QTY start)-------//
    // 🔹 English → Bangla
    public static String ReplacementQtyEnToBn(String replacement) {
        replacement = replacement.replace("10", "১০ টি");
        replacement = replacement.replace("0", "০ টি");
        replacement = replacement.replace("1", "১ টি");
        replacement = replacement.replace("2", "২ টি");
        replacement = replacement.replace("3", "৩ টি");
        replacement = replacement.replace("4", "৪ টি");
        replacement = replacement.replace("5", "৫ টি");
        replacement = replacement.replace("6", "৬ টি");
        replacement = replacement.replace("7", "৭ টি");
        replacement = replacement.replace("8", "৮ টি");
        replacement = replacement.replace("9", "৯ টি");
        return replacement;
    }

    // 🔹 Bangla → English
    public static String ReplacementQtyBnToEn(String replacement) {
        replacement = replacement.replace("১০ টি","10");
        replacement = replacement.replace("০ টি","0");
        replacement = replacement.replace("১ টি","1");
        replacement = replacement.replace("২ টি","2");
        replacement = replacement.replace("৩ টি","3");
        replacement = replacement.replace("৪ টি","4");
        replacement = replacement.replace("৫ টি","5");
        replacement = replacement.replace("৬ টি","6");
        replacement = replacement.replace("৭ টি","7");
        replacement = replacement.replace("৮ টি","8");
        replacement = replacement.replace("৯ টি","9");
        return replacement;
    }

    // 🔹 Auto Local Method
    public static String ReplacementQtyToLocal(Context context, String quantity) {
        String lang = LocaleHelper.getLanguage(context); // en or bn

        if ("bn".equals(lang)) {
            return ReplacementQtyEnToBn(quantity);
        } else {
            return ReplacementQtyBnToEn(quantity);
        }
    }
    //------(QTY End)-------//


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



}
