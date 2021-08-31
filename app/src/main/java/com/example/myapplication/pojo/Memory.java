package com.example.myapplication.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "memories")
public class Memory {
    private String tittle;
    private String description;
    private CurrentLocation location;
    private List<ImageToUpload> pictures;
    private String mainPicUrl;
    private int picturesCount;
    private String timeInMillis;
    private int memoryID;
    @PrimaryKey(autoGenerate = true)
    private int dataBaseId;

    public Memory(String tittle, String description, CurrentLocation location, List<ImageToUpload> pictures, String mainPicUrl, int picturesCount, String timeInMillis, int memoryID) {
        this.tittle = tittle;
        this.description = description;
        this.location = location;
        this.pictures = pictures;
        this.mainPicUrl = mainPicUrl;
        this.picturesCount = picturesCount;
        this.timeInMillis = timeInMillis;
        this.memoryID = memoryID;
    }

    public int getMemoryID() {
        return memoryID;
    }

    public void setMemoryID(int memoryID) {
        this.memoryID = memoryID;
    }

    public String getTittle() {
        return tittle;
    }

    public CurrentLocation getLocation() {
        return location;
    }

    public List<ImageToUpload> getPictures() {
        return pictures;
    }

    public String getMainPicUrl() {
        return mainPicUrl;
    }

    public int getPicturesCount() {
        return picturesCount;
    }

    public String getTimeInMillis() {
        return timeInMillis;
    }

    public String getDescription() {
        return description;
    }

    public Memory() {
    }

    @Override
    public String toString() {
        return "Memory{" +
                "tittle='" + tittle + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", pictures=" + pictures +
                ", mainPicUrl='" + mainPicUrl + '\'' +
                ", picturesCount=" + picturesCount +
                ", timeInMillis='" + timeInMillis + '\'' +
                ", memoryID=" + memoryID +
                ", dataBaseId=" + dataBaseId +
                '}';
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(CurrentLocation location) {
        this.location = location;
    }

    public void setPictures(List<ImageToUpload> pictures) {
        this.pictures = pictures;
    }

    public void setMainPicUrl(String mainPicUrl) {
        this.mainPicUrl = mainPicUrl;
    }

    public void setPicturesCount(int picturesCount) {
        this.picturesCount = picturesCount;
    }

    public void setTimeInMillis(String timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public int getDataBaseId() {
        return dataBaseId;
    }

    public void setDataBaseId(int dataBaseId) {
        this.dataBaseId = dataBaseId;
    }
}
