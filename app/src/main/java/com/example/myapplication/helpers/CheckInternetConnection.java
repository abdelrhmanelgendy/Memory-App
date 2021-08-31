package com.example.myapplication.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;

public class CheckInternetConnection {
    public static final String CONNECTED = "connected";
    public static final String NOT_CONNECTED = "not_connected";
    public static final String UNKNOWN = "unKnwon";
    public static String connectionState = UNKNOWN;

    public static boolean connection(ConnectivityManager connectivityManager) {
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo data = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || data.isConnected()) {
            return true;
        } else {
            return false;


        }


    }
    public static boolean connection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo data = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || data.isConnected()) {
            return true;
        } else {
            return false;


        }


    }
    public static void internetPersent() {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("www.youtube.com", 80);
                    boolean connected = socket.isConnected();
                    if (connected) {
                        connectionState = CONNECTED;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionState = NOT_CONNECTED;
                }
            }
        });
        thread.start();


    }

}
