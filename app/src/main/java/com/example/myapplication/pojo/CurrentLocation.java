package com.example.myapplication.pojo;

public class CurrentLocation {
    private double longitude;
    private double latitude;

    public CurrentLocation(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public CurrentLocation() {
    }

    @Override
    public String toString() {
        return "CurrentLocation{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
