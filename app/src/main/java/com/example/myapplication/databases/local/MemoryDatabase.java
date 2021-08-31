package com.example.myapplication.databases.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.myapplication.jsonConverter.ListOfUploadedImagesTypeConverter;
import com.example.myapplication.jsonConverter.LocationConverter;
import com.example.myapplication.pojo.Memory;

@Database(entities = Memory.class, version = 1)
@TypeConverters({ListOfUploadedImagesTypeConverter.class, LocationConverter.class})
public abstract class MemoryDatabase extends RoomDatabase {
    public abstract MemoryDao memoryDao();

    private static MemoryDatabase instance;

    public static MemoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MemoryDatabase.class, "MemoryAppDataBase").build();
        }
        return instance;
    }

}
