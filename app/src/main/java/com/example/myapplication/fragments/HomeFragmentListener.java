package com.example.myapplication.fragments;

import com.example.myapplication.pojo.Memory;
import com.example.myapplication.pojo.User;

import java.util.List;

public interface HomeFragmentListener {
    void onGetUserData(User user);

    void onGetAllMemories(List<Memory> memories);
}
