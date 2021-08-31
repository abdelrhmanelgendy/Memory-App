package com.example.myapplication.jsonConverter;

import com.example.myapplication.pojo.ImageFromStorag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ImageFromStorageGSONConverter {
    Gson gson;

    public ImageFromStorageGSONConverter(Gson gson) {
        this.gson = gson;
    }

    public String convertToJSON(ArrayList<ImageFromStorag> listOfImages) {
        String images = gson.toJson(listOfImages);
        return images;

    }

    public ArrayList<ImageFromStorag> convertToImage(String s) {
        Type type = new TypeToken<ArrayList<ImageFromStorag>>(){}.getType();
        ArrayList<ImageFromStorag> imageFromStorag = gson.fromJson(s, type);
        return imageFromStorag;


    }
}
