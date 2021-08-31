package com.example.myapplication.helpers;

import android.content.Context;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

public class FileExtensionAndName {

    private static Context mContext;
    private static FileExtensionAndName fileExtensionAndName;



    public static FileExtensionAndName get(Context context) {
        mContext = context;
        if (fileExtensionAndName == null) {
            fileExtensionAndName = new FileExtensionAndName();
        }
        return fileExtensionAndName;
    }


    public String getFileName(String url) {
        String filName = null;
        if (mContext == null) {
            throw new NullPointerException("context is null");
        }
        filName = URLUtil.guessFileName(url, null, null);

        return filName;
    }

    public String getFileExtension(String url) {
        String extension = null;
        if (mContext == null) {
            throw new NullPointerException("context is null");
        }
        extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return extension;
    }

}
