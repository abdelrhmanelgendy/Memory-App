package com.example.myapplication.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.helpers.DownloadManager;
import com.example.myapplication.helpers.FileExtensionAndName;
import com.example.myapplication.ui.ImageShowerFromMemory;


import java.io.File;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ImagesListDownLoader extends Service {
    private static final String TAG = "TAG51";
    private static final CharSequence DOWNLOAD_CHANNEL_NAME = "image downloads";

    static Context mContext;
    static DownloadManager mDownloadManager;
    static ImagesListDownLoader instance;
    public static boolean isDownloading = false;
    private static final int PROGRESS_NOTIFICATION = 12;
    private static final int FINISHED_NOTIFICATION = 13;
    private final static String NOTIFICATION_CHANNEL_ID = "downloading";
    NotificationCompat.Builder notification;
    NotificationManager notificationManager;
    public static DownLoadInfo downLoadInfo;


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this);
        }
        mContext = this;
        getInstance(mContext);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String url = intent.getStringExtra(ImageShowerFromMemory.URL);
        int position = intent.getIntExtra(ImageShowerFromMemory.POSITION, -1);
        int size = intent.getIntExtra(ImageShowerFromMemory.SIZE, -1);
        String memeoryName = intent.getStringExtra(ImageShowerFromMemory.MEMORY_NAME);
        downLoad(url, memeoryName, position, size, mContext);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static ImagesListDownLoader getInstance(Context context) {
        if (instance == null) {
            mContext = context;
            mDownloadManager = DownloadManager.getInstance(context);
            instance = new ImagesListDownLoader();
        }
        return instance;
    }


    int finishedTime = 0;
    boolean isFinshed = false;

    int times = 1;

    public void downLoad(String url, String memoryName, int position, int size, Context context) {
        isDownloading = true;

        setNotification(mContext, PROGRESS_NOTIFICATION);
        String filePath = getPath(memoryName, url);
        Log.d(TAG, "downLoad: " + filePath);

        Observable<Integer> integerObservable = mDownloadManager.downLoad(url, filePath);


        integerObservable.subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        downLoadInfo.DownLoading(integer);
                        Log.d(TAG, "onNext: " + integer);


                        update(Color.rgb(230, 86, 132), 100, integer, "donwloading", times + "/" + size, integer + "%");
                        notificationNotify(PROGRESS_NOTIFICATION, notification.build());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {
                        times++;

                        notificationManager.notify(PROGRESS_NOTIFICATION, notification.build());
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                finishedTime += 1;
                                if (finishedTime == size) {
                                    downLoadInfo.Finished();
                                    isDownloading = false;
                                    notificationManager.cancel(PROGRESS_NOTIFICATION);
                                    stopSelfResult(PROGRESS_NOTIFICATION);
                                    stopSelf();
                                    update(Color.RED, 0, 0, "Memory Download Manager", "download complete", null);
                                    notificationNotify(FINISHED_NOTIFICATION, notification.build());
                                    stopForeground(true);
                                    Toast.makeText(mContext, "download complete", Toast.LENGTH_SHORT).show();

                                    isFinshed = false;
                                }
                            }
                        }, 1000);

                    }
                });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(Context context) {
        NotificationChannel downloadChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        downloadChannel.setDescription("");
        NotificationManagerCompat.from(context).createNotificationChannel(downloadChannel);

    }


    void setNotification(Context context, int notfyID) {
        downLoadInfo.pending();
        Intent downloadIntent = new Intent(android.app.DownloadManager.ACTION_VIEW_DOWNLOADS);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 12, downloadIntent, PendingIntent.FLAG_ONE_SHOT);
        notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Memory Download Manager")
                .setSmallIcon(R.drawable.ic_baseline_download_24)
                .setProgress(100, 0, true)
                .setOnlyAlertOnce(true)
                .setContentText("pending")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.rgb(230, 86, 132))
                .setContentIntent(pendingIntent)
                .setOngoing(false);
        startForeground(notfyID, notification.build());
    }


    private String getPath(String memoryName, String url) {
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + mContext.getResources().getString(R.string.app_name) + "/" + memoryName;
        createDirectory(directoryPath);
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + mContext.getResources().getString(R.string.app_name) + "/" + memoryName + "/"
                + FileExtensionAndName.get(mContext).getFileName(url);
    }

    private void createDirectory(String directoryPath) {

        File file = new File(directoryPath);


        boolean exists = file.exists();
        if (!exists) {

            boolean mkdir = file.mkdir();
            Log.d("TAG51", "createDirectory: " + directoryPath + " " + exists + " op" + mkdir);
        }
    }

    void update(int notificationColor, int maxProgress, int progress, String contentTitleProcess, String contentTextTimes, String subTxt) {
        notification.setColor(notificationColor)
                .setProgress(maxProgress, progress, false)
                .setSubText(subTxt)
                .setContentTitle(contentTitleProcess)
                .setContentText(contentTextTimes);
    }

    void notificationNotify(int id, Notification notification) {
        notificationManager.notify(id, notification);
    }
}
