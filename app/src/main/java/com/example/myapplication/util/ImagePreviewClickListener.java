package com.example.myapplication.util;

import android.view.View;

import com.example.myapplication.adapters.DownloadedImage;

import java.util.List;

public interface ImagePreviewClickListener {
    public void onImageClick(int position, String uri, View view);
    public void onImageLongClick(int position, List<DownloadedImage> downloadedImages, View view);
    public void onImageFavoriteClick(int position, String url,boolean favorite);

    void onLoad(View holder, int position, boolean checked);
}
