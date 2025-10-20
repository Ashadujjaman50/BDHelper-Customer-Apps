package com.dropshep.bdhelper.myUtils;

import android.util.Log;

public class FinanceCache {
    public static double totalEarned = 0;
    public static double partnerReceivable = 0;
    public static double companyReceivable = 0;
    public static boolean isLoaded = false;
    public static long lastUpdated = 0;

    public static boolean needsRefresh() {
        return !isLoaded || (System.currentTimeMillis() - lastUpdated > 2000);
    }

    // 🔥 NEW: Force invalidate cache
    public static void invalidateCache() {
        isLoaded = false;
        lastUpdated = 0;
        Log.d("FinanceCache", "🔥 Cache invalidated - forced refresh required");
    }

    // Optional: Manual cache update method
    public static void updateCache(double totalEarned, double partnerReceivable, double companyReceivable) {
        FinanceCache.totalEarned = totalEarned;
        FinanceCache.partnerReceivable = partnerReceivable;
        FinanceCache.companyReceivable = companyReceivable;
        FinanceCache.lastUpdated = System.currentTimeMillis();
        FinanceCache.isLoaded = true;
        Log.d("FinanceCache", "✅ Cache manually updated");
    }

}

