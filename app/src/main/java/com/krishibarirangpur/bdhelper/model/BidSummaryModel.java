package com.krishibarirangpur.bdhelper.model;

public class BidSummaryModel {
    private int total;
    private int success;
    private int cancel;

    public BidSummaryModel() {}

    public BidSummaryModel(int total, int success, int cancel) {
        this.total = total;
        this.success = success;
        this.cancel = cancel;
    }

    public int getTotal() { return total; }
    public int getSuccess() { return success; }
    public int getCancel() { return cancel; }
}
