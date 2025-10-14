package com.dropshep.bdhelper.model;

public class BidSummary {
    private int total;
    private int success;
    private int cancel;

    public BidSummary() {}

    public BidSummary(int total, int success, int cancel) {
        this.total = total;
        this.success = success;
        this.cancel = cancel;
    }

    public int getTotal() { return total; }
    public int getSuccess() { return success; }
    public int getCancel() { return cancel; }
}
