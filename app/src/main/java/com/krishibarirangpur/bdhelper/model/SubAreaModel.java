package com.krishibarirangpur.bdhelper.model;

public class SubAreaModel {
    private String areaId;
    private String subAreaId;
    private String subAreaName;

    public SubAreaModel() {
    }

    public SubAreaModel(String areaId, String subAreaId, String subAreaName) {
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
