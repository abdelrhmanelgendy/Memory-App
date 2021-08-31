package com.example.myapplication.jsonConverter;

import com.example.myapplication.pojo.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GSONConverter {
    Gson gson;

    public GSONConverter() {
        gson = new Gson();
    }

    public String fromImagesToString(List<Image> list) {

        String s = gson.toJson(list);
        return s;

    }

    public List<Image> fromImagesToString(String json) {

        Type type = new TypeToken<List<Image>>() {
        }.getType();
        List<Image> list = gson.fromJson(json, type);
        return list;

    }

}
