package com.example.myapplication.helpers;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

public class DownloadManager {

    private static Context mContext;
    private static DownloadManager downloadManagerInstance;

    public static DownloadManager getInstance(Context context) {

        mContext = context;

        if (downloadManagerInstance == null) {
            downloadManagerInstance = new DownloadManager();
        }

        return downloadManagerInstance;
    }

    private DownloadManager() {
    }

    public Observable<Integer> downLoad(String urlPath, String filePath) {

        Observable<Integer> downloadObervable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int count;
                try {

                    long downloadedLenght = 0;
                    URL url = new URL(urlPath);
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    int contentLength = urlConnection.getContentLength();
                    InputStream inputStream = url.openStream();
                    byte[] meta_data = new byte[1024];
                    FileOutputStream outputStream = new FileOutputStream(filePath);
                    while ((count = inputStream.read(meta_data)) != -1) {

                        downloadedLenght += count;
                        outputStream.write(meta_data, 0, count);
                        int progress = (int) ((100 * downloadedLenght) / contentLength);
                        emitter.onNext(progress);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    emitter.onComplete();

                } catch (Exception e) {
                    Log.d("TAG51", "downLoad: " + e.getMessage());
                }

            }
        });
        return downloadObervable;

    }
}

