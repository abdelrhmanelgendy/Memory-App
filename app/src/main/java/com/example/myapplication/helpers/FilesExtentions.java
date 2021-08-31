package com.example.myapplication.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FilesExtentions {

    public static String getExtension(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        return singleton.getExtensionFromMimeType(contentResolver.getType(uri));

    }
}
