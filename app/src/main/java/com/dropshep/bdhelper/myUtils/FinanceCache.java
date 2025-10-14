package com.dropshep.bdhelper.myUtils;

public class FinanceCache {
    public static double totalEarned = 0;
    public static double partnerReceivable = 0;
    public static double companyReceivable = 0;
    public static boolean isLoaded = false;
    public static long lastUpdated = 0;

    public static boolean needsRefresh() {
        return !isLoaded || (System.currentTimeMillis() - lastUpdated > 2000);
    }
}

