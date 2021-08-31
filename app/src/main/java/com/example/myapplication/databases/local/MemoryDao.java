package com.example.myapplication.databases.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.pojo.Memory;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface MemoryDao {
    @Insert
    Completable insertMemories(List<Memory> memories);
    @Insert
    Completable insertOneMemory(Memory memory);

    @Query("select * from memories")
    Observable<List<Memory>> getAllMemories();

    @Query("delete  from memories")
    Completable deleteAllMemories();
}
