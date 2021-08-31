package com.example.myapplication.pojo;

public class FavoriImage {
    private String id;
    private String url;

    public FavoriImage(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public FavoriImage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
