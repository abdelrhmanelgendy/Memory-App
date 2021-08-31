package com.example.myapplication.util;

import com.example.myapplication.pojo.ImageFromStorag;

public interface OnImageFromStorageClickListeners {
    public static final Boolean MULTIPLE_SELECTION=true;
    public static final Boolean SINGLE_SELECTION=false;
    void onImageClick(ImageFromStorag imageFromStorag,boolean state);
    void onImageLongClick(ImageFromStorag imageFromStorag);

}
