package com.example.myapplication.fragments;

import com.example.myapplication.pojo.Memory;

import java.util.List;

interface MemoryEventListener {
    void onGetAllMemories(List<Memory> memoryList);
}
