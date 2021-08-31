package com.example.myapplication.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;

public class UploadNotification {
    public static final int NOTIFICATION_ID = 2;
    Context context;
    public static final String CHANNEL_ID = "uploading Progress";
    public static UploadNotification progressNotificationInstance;
    NotificationCompat.Builder notificationBuilder;
PendingIntent pendingIntent;


    public UploadNotification(Context context, PendingIntent pendingIntent) {
        this.context = context;
        this.pendingIntent = pendingIntent;
    }

    private final static String NOTIFICATION_CHANNEL_ID = "uploding";
    static NotificationManager notificationManager;
    static NotificationCompat.Builder notification;

    public void notification(String txt) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.def);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context);
        }
        notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_file_upload_24)
                .setLargeIcon(bitmap)
                .setProgress(100, 0, false)
                .setContentTitle("Uploading in Progress")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(false)
                .setSubText("")
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContentText(txt);


        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification.build());


    }

    public void setPreogress(int max,int preogress,String txt,Bitmap bitmap,int size) {
        notification.setProgress(max, preogress, false)
                .setSubText(preogress+"%")
                .setLargeIcon(bitmap)
                .setContentText(txt);
        if (max==0)
        {
            Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.def);

            notification.setOngoing(false)
                    .setContentTitle("Uploud Completed ")
                    .setSubText(100+"%")
                    .setContentText(size+"/"+size)
            .setLargeIcon(bitmap1);
        }

        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(Context context) {
        NotificationChannel channel1 = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "upload", NotificationManager.IMPORTANCE_HIGH);
        channel1.setDescription("");
        NotificationManagerCompat.from(context)
                .createNotificationChannel(channel1);

    }


}
