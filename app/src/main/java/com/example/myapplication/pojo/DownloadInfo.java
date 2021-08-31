package com.example.myapplication.pojo;

public class DownloadInfo {
    long downLoadedBytes,tottalSize;
    String status;

    public DownloadInfo(long downLoadedBytes, long tottalSize, String status) {
        this.downLoadedBytes = downLoadedBytes;
        this.tottalSize = tottalSize;
        this.status = status;
    }

    public long getDownLoadedBytes() {
        return downLoadedBytes;
    }

    public long getTottalSize() {
        return tottalSize;
    }

    public String getStatus() {
        return status;
    }
}
