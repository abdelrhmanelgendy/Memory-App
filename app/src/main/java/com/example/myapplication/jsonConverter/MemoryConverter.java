package com.example.myapplication.jsonConverter;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.example.myapplication.pojo.Memory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MemoryConverter {
    Gson gson;

    public MemoryConverter() {
        this.gson = new Gson();
    }
    public String convertToGson(Memory memory)
    {
        String s = gson.toJson(memory);
        return s;
    }
    public Memory ConvertToMemory(String json)
    {
        Memory memory = gson.fromJson(json, Memory.class);
        return memory;
    }
}
