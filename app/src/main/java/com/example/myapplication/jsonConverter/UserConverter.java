package com.example.myapplication.jsonConverter;

import com.example.myapplication.pojo.User;
import com.google.gson.Gson;

public class UserConverter {
    static Gson gson = new Gson();

    public static User getUser(String userString) {
        User user = gson.fromJson(userString, User.class);
        return user;
    }

    public static String getStringedUser(User user) {
        return gson.toJson(user);
    }

}
