package com.example.myapplication.jsonConverter;

import com.example.myapplication.pojo.ImageToUpload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListOfImageToUploadConverter {
    Gson gson;

    public ListOfImageToUploadConverter() {
        gson = new Gson();
    }

    public String fromListToString(List<ImageToUpload> list) {
        String stringedList = gson.toJson(list);
        return stringedList;
    }

    public List<ImageToUpload> getListFromString(String listedString) {
        Type type = new TypeToken<List<ImageToUpload>>() {
        }.getType();
        List<ImageToUpload> list = gson.fromJson(listedString, type);
        return list;

    }
}
