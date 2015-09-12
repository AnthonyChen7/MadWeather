package com.example.madweather;

/**
 * A location class that contains the lat and lng as well as the name.
 */
public class Location {

    private String lat;
    private String lng;
    private String name;

    public Location(String lat, String lng, String name){
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getName() { return this.name; }
}
