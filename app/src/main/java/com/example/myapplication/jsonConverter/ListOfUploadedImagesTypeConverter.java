package com.example.myapplication.jsonConverter;

import androidx.room.TypeConverter;

import com.example.myapplication.pojo.ImageToUpload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListOfUploadedImagesTypeConverter {
    @TypeConverter
    public List<ImageToUpload> getList(String listsOfImage) {
        Type type = new TypeToken<List<ImageToUpload>>() {
        }.getType();
        return new Gson().fromJson(listsOfImage, type);
    }

    @TypeConverter
    public String getStringedImage(List<ImageToUpload> listsOfImage) {

        return new Gson().toJson(listsOfImage);
    }

}
