package com.dropshep.bdhelper.model;

public class Area {
    private String cityId;
    private String areaId;
    private String areaName;

    public Area() {
    }

    public Area(String cityId, String areaId, String areaName) {
        this.cityId = cityId;
        this.areaId = areaId;
        this.areaName = areaName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getAreaName() {
        return areaName;
    }
}
