package com.example.myapplication.pojo;

public class ImageToUpload {
    private String url;
    private String id;
    private String memoryIId;
    private String memoryTittle;
    private boolean imageFavorite;


    @Override
    public String toString() {
        return "ImageToUpload{" +
                "url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", memoryId='" + memoryIId + '\'' +
                ", imageFavorite=" + imageFavorite +
                memoryTittle+
                '}';
    }

    public ImageToUpload(String url, String id, boolean imageFavorite,String memoryId,String memoryTittle ) {
        this.url = url;
        this.id = id;
        this.memoryIId = memoryId;
        this.imageFavorite = imageFavorite;
        this.memoryTittle=memoryTittle;
    }

    public String getMemoryId() {
        return memoryIId;
    }

    public String getMemoryIId() {
        return memoryIId;
    }

    public void setMemoryIId(String memoryIId) {
        this.memoryIId = memoryIId;
    }

    public String getMemoryTittle() {
        return memoryTittle;
    }

    public void setMemoryTittle(String memoryTittle) {
        this.memoryTittle = memoryTittle;
    }

    public void setMemoryId(String memoryId) {
        this.memoryIId = memoryId;
    }

    public boolean isImageFavorite() {
        return imageFavorite;
    }

    public void setImageFavorite(boolean imageFavorite) {
        this.imageFavorite = imageFavorite;
    }

    public ImageToUpload() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}
