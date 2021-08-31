package com.example.myapplication.jsonConverter;

import androidx.room.TypeConverter;

import com.example.myapplication.pojo.CurrentLocation;
import com.google.gson.Gson;

public class LocationConverter {
    @TypeConverter
    public String getStringesLocation(CurrentLocation location) {
        return new Gson().toJson(location);
    }
    @TypeConverter
    public CurrentLocation getLocation(String location)
    {
        return new Gson().fromJson(location,CurrentLocation.class);
    }
}
