package com.example.myapplication.pojo;

public class Image {
    private int id;
    private String uri;
    boolean isFromEdit=false;

    public boolean isFromEdit() {
        return isFromEdit;
    }

    public void setFromEdit(boolean fromEdit) {
        isFromEdit = fromEdit;
    }

    public Image(int id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                '}';
    }
}
