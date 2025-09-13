package com.dropshep.bdhelper.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchAutoCompleteResponse {
    @SerializedName("places")
    List<Places> places;

    @SerializedName("status")
    int status;


    public void setPlaces(List<Places> places) {
        this.places = places;
    }

    public List<Places> getPlaces() {
        return places;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static class Places {

        @SerializedName("id")
        int id;

        @SerializedName("longitude")
        String longitude;

        @SerializedName("latitude")
        String latitude;

        @SerializedName("address")
        String address;

        @SerializedName("address_bn")
        String address_bn;

        @SerializedName("city")
        String city;

        @SerializedName("area")
        String area;

        @SerializedName("postCode")
        int postCode;

        @SerializedName("pType")
        String pType;

        @SerializedName("subType")
        String subType;

        @SerializedName("district")
        String district;

        @SerializedName("uCode")
        String uCode;


        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        public String getLongitude() {
            return longitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public String getLatitude() {
            return latitude;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getAddress() {
            return address;
        }

        public String getAddress_bn() {
            return address_bn;
        }

        public void setAddress_bn(String address_bn) {
            this.address_bn = address_bn;
        }

        public void setCity(String city) {
            this.city = city;
        }
        public String getCity() {
            return city;
        }

        public void setArea(String area) {
            this.area = area;
        }
        public String getArea() {
            return area;
        }

        public void setPostCode(int postCode) {
            this.postCode = postCode;
        }
        public int getPostCode() {
            return postCode;
        }

        public void setPType(String pType) {
            this.pType = pType;
        }
        public String getPType() {
            return pType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }
        public String getSubType() {
            return subType;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
        public String getDistrict() {
            return district;
        }

        public void setUCode(String uCode) {
            this.uCode = uCode;
        }
        public String getUCode() {
            return uCode;
        }

    }
}
