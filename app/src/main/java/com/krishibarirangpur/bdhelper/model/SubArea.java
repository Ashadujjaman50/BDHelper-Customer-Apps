package com.krishibarirangpur.bdhelper.model;

public class SubArea {
    private String areaId;
    private String subAreaId;
    private String subAreaName;

    public SubArea() {
    }

    public SubArea(String areaId, String subAreaId, String subAreaName) {
        this.areaId = areaId;
        this.subAreaId = subAreaId;
        this.subAreaName = subAreaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getSubAreaId() {
        return subAreaId;
    }

    public String getSubAreaName() {
        return subAreaName;
    }
}
