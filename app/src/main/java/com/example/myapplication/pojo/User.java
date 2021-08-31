package com.example.myapplication.pojo;

import java.util.List;

public class User {

    private String userName;
    private String emailAddress;
    private String password;
    private String age;
    private String AccountCreatedDate;
    private String profilePicUrl;
    private String userId;
    private List<FavoriImage> favoriteImages;

    public User() {
    }

    public User(String userName, String emailAddress, String password, String age, String accountCreatedDate, String profilePicUrl, String userId, List<FavoriImage> favoriteImages) {
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.age = age;
        AccountCreatedDate = accountCreatedDate;
        this.profilePicUrl = profilePicUrl;
        this.userId = userId;
        this.favoriteImages = favoriteImages;
    }

    public List<FavoriImage> getFavoriteImages() {
        return favoriteImages;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getAge() {
        return age;
    }

    public String getAccountCreatedDate() {
        return AccountCreatedDate;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getUserId() {
        return userId;
    }
}
