package com.krishibarirangpur.bdhelper.api;
import com.google.gson.annotations.SerializedName;

public class BarikoiResponse {
    @SerializedName("place")
    private Place place;
    @SerializedName("status")
    private int status;

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class Place {
        @SerializedName("id")
        private int id;
        @SerializedName("distance_within_meters")
        private double distance_within_meters;
        @SerializedName("address")
        private String address;
        @SerializedName("area")
        private String area;

        @SerializedName("city")
        private String city;
        @SerializedName("post_code")
        private String postCode;
        @SerializedName("address_bn")
        private String address_bn;
        @SerializedName("area_bn")
        private String area_bn;
        @SerializedName("city_bn")
        private String city_bn;
        @SerializedName("country")
        private String country;
        @SerializedName("division")
        private String division;
        @SerializedName("district")
        private String district;
        @SerializedName("sub_district")
        private String sub_district;
        @SerializedName("pauroshova")
        private String pauroshova;
        @SerializedName("union")
        private String union;
        @SerializedName("location_type")
        private String location_type;
        @SerializedName("address_components")
        private AddressComponents address_components;
        @SerializedName("area_components")
        private AreaComponents area_components;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getDistance_within_meters() {
            return distance_within_meters;
        }

        public void setDistance_within_meters(double distance_within_meters) {
            this.distance_within_meters = distance_within_meters;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostCode() {
            return postCode;
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }

        public String getAddress_bn() {
            return address_bn;
        }

        public void setAddress_bn(String address_bn) {
            this.address_bn = address_bn;
        }

        public String getArea_bn() {
            return area_bn;
        }

        public void setArea_bn(String area_bn) {
            this.area_bn = area_bn;
        }

        public String getCity_bn() {
            return city_bn;
        }

        public void setCity_bn(String city_bn) {
            this.city_bn = city_bn;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getDivision() {
            return division;
        }

        public void setDivision(String division) {
            this.division = division;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getSub_district() {
            return sub_district;
        }

        public void setSub_district(String sub_district) {
            this.sub_district = sub_district;
        }

        public String getPauroshova() {
            return pauroshova;
        }

        public void setPauroshova(String pauroshova) {
            this.pauroshova = pauroshova;
        }

        public String getUnion() {
            return union;
        }

        public void setUnion(String union) {
            this.union = union;
        }

        public String getLocation_type() {
            return location_type;
        }

        public void setLocation_type(String location_type) {
            this.location_type = location_type;
        }

        public AddressComponents getAddress_components() {
            return address_components;
        }

        public void setAddress_components(AddressComponents address_components) {
            this.address_components = address_components;
        }

        public AreaComponents getArea_components() {
            return area_components;
        }

        public void setArea_components(AreaComponents area_components) {
            this.area_components = area_components;
        }
    }

    public static class AddressComponents {
        private String place_name;
        private String house;
        private String road;

        public String getPlace_name() {
            return place_name;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }

        public String getHouse() {
            return house;
        }

        public void setHouse(String house) {
            this.house = house;
        }

        public String getRoad() {
            return road;
        }

        public void setRoad(String road) {
            this.road = road;
        }
    }

    public static class AreaComponents {
        private String area;
        private String sub_area;

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getSub_area() {
            return sub_area;
        }

        public void setSub_area(String sub_area) {
            this.sub_area = sub_area;
        }
    }

}
