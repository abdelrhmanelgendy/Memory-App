package com.example.myapplication.adapters;

public class DownloadedImage {
    String imgUrl;
    boolean checked=false;

    public DownloadedImage(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "DownloadedImage{" +
                "imgUrl='" + imgUrl + '\'' +
                ", checked=" + checked +
                '}';
    }
}
