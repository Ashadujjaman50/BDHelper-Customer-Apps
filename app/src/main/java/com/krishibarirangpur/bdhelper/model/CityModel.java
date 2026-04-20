package com.krishibarirangpur.bdhelper.model;

public class CityModel {
    private String cityId;
    private String cityName;

    public CityModel() {
    }

    public CityModel(String cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }
}
