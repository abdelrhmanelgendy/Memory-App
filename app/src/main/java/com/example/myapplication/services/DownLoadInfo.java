package com.example.myapplication.services;

public interface DownLoadInfo {
    void DownLoading(int progress);
    void pending();
    void Finished();
}
