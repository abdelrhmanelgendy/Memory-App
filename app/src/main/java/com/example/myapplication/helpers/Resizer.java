package com.example.myapplication.helpers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Resizer {

    private static Context mContext;

    public static void get(Context context) {
        mContext = context;
    }

    private static File createFileDirec() {
        File file = new File(mContext.getFilesDir() + "/" + "ResizedImages");
        boolean mkdir = file.mkdir();
        Log.d("TAG11", "createFileDirec: " + file.getAbsolutePath());
        return file;
    }


    private static String saveBitMab(Bitmap bitmap, int quality) {
        File fileDirec = createFileDirec();
        long timeInMillis = System.currentTimeMillis();
        String filePath = fileDirec + "/" + timeInMillis + ".JPEG";
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return filePath;
    }

    public static String reduceBitmab(Uri uri) {
        Bitmap bitmapByUri = getBitmapByUri(uri);

        String s = "";
        AssetFileDescriptor file = null;
        try {
            file = mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file != null) {

            long sizeInKB = file.getLength() / 1024;
            Log.d("TAG200", "reduceBitmab: "+sizeInKB);
            if (sizeInKB <= 250) {
                s = saveBitMab(bitmapByUri, 70);
            } else if (sizeInKB > 250 && sizeInKB <= 500) {
                s = saveBitMab(bitmapByUri, 55);
            } else if (sizeInKB > 500 && sizeInKB < 700) {
                s = saveBitMab(bitmapByUri, 45);
            } else if (sizeInKB >= 700 && sizeInKB <= 1000) {
                s = saveBitMab(bitmapByUri, 40);
            } else if (sizeInKB > 1000 && sizeInKB <= 3000) {
                s = saveBitMab(bitmapByUri, 40);
            } else if (sizeInKB > 3000 && sizeInKB <= 5000) {
                s = saveBitMab(bitmapByUri, 30);
            } else if (sizeInKB > 5000 && sizeInKB <= 7000) {
                s = saveBitMab(bitmapByUri, 30);
            } else if (sizeInKB > 7000 && sizeInKB <= 10000) {
                s = saveBitMab(bitmapByUri, 30);
            } else if (sizeInKB > 10000) {
                s = saveBitMab(bitmapByUri, 15);
            }
            else
            {
                s = saveBitMab(bitmapByUri, 5);
            }
        }
        return s;
    }



    private static Bitmap getBitmapByUri(Uri uri) {
        Bitmap mBitmap = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                mBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mBitmap;
    }

    public static String reduceBitmabBySpecificSize(Uri fromFile, int quality) {
        Bitmap bitmapByUri = getBitmapByUri(fromFile);

        String s = "";
        AssetFileDescriptor file = null;
        try {
            file = mContext.getContentResolver().openAssetFileDescriptor(fromFile, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file != null) {
            s = saveBitMab(bitmapByUri, quality);
        }
        return s;
    }
}
