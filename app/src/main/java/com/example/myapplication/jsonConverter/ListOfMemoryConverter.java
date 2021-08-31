package com.example.myapplication.jsonConverter;

import com.example.myapplication.pojo.Image;
import com.example.myapplication.pojo.Memory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListOfMemoryConverter {
    Gson gson;

    public ListOfMemoryConverter() {
        gson = new Gson();
    }

    public String fromMemoriesToString(List<Memory> list) {

        String s = gson.toJson(list);
        return s;

    }

    public List<Memory> fromStringToMemoryList(String json) {

        Type type = new TypeToken<List<Memory>>() {
        }.getType();
        List<Memory> list = gson.fromJson(json, type);
        return list;

    }

}
