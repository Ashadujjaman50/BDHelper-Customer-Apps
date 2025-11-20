package com.krishibarirangpur.bdhelper.myUtils;

import com.krishibarirangpur.bdhelper.model.BidSummary;

public class CacheManager {
    private static CacheManager instance;
    private BidSummary bidSummary;

    private CacheManager() {}

    public static synchronized CacheManager getInstance() {
        if (instance == null) instance = new CacheManager();
        return instance;
    }

    public void setBidSummary(BidSummary summary) {
        this.bidSummary = summary;
    }

    public BidSummary getBidSummary() {
        return bidSummary;
    }

    public boolean hasBidSummary() {
        return bidSummary != null;
    }
}
