package com.example.myapplication.util;

import android.view.View;

import com.example.myapplication.pojo.Image;

public interface OnPictureClickListener {
    public void onClick(Image image);
    public void onLongClick(Image image, View view);
    public void onEditeClick(Image image);
}
