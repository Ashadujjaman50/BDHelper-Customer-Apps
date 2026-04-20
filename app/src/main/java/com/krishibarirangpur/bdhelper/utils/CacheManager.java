package com.krishibarirangpur.bdhelper.utils;

import com.krishibarirangpur.bdhelper.model.BidSummaryModel;

public class CacheManager {
    private static CacheManager instance;
    private BidSummaryModel bidSummary;

    private CacheManager() {}

    public static synchronized CacheManager getInstance() {
        if (instance == null) instance = new CacheManager();
        return instance;
    }

    public void setBidSummary(BidSummaryModel summary) {
        this.bidSummary = summary;
    }

    public BidSummaryModel getBidSummary() {
        return bidSummary;
    }

    public boolean hasBidSummary() {
        return bidSummary != null;
    }
}
