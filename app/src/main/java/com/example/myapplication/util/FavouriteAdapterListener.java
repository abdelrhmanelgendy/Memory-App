package com.example.myapplication.util;

import com.example.myapplication.pojo.ImageToUpload;

import java.util.List;

public interface FavouriteAdapterListener {
    public void onImageClick(List<ImageToUpload> imageToUploadList, ImageToUpload imageToUpload, int adapterPosition);
}
