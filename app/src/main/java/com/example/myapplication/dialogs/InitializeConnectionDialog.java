package com.example.myapplication.dialogs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.LanguageHelper;

public class InitializeConnectionDialog {
    static Handler handler = new android.os.Handler(Looper.getMainLooper());
    static Runnable runnable;
    static boolean forTheFirstTime = false;
    static Context mContext;
    static TextView mTextView;

    public static void changeConnetion(TextView textView, Context context) {
        mContext = context;
        mTextView=textView;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String offline = "No Internet Connection";
        if (LanguageHelper.getDeviceLanguage().equals(LanguageHelper.ARABIC_CODE)) {
            offline = "لا يوجد اتصال بالانترنت";
        }

        String online = context.getResources().getString(R.string.connectionOnline);
        int activeColor = context.getResources().getColor(R.color.connectionViewActiveColor);
        int offlineColor = context.getResources().getColor(R.color.connectionViewOflineColor);
        String finalOffline = offline;
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 50);
                boolean isConnected = checkConnection(connectivityManager);

                textView.setText(isConnected ? online : finalOffline);
                textView.setBackgroundColor(isConnected ? activeColor : offlineColor);

                if (!isConnected) {
                    textView.setVisibility(View.VISIBLE);
                    Log.d("TAGConnection", "run: visible1");
                    forTheFirstTime = true;
                } else {

                    textView.setVisibility(View.GONE);


                }

            }
        }, 50);


    }

    public static void endCheck() {

        handler.removeCallbacks(runnable);
    }

    private static boolean checkConnection(ConnectivityManager connectivityManager) {
        boolean connection = CheckInternetConnection.connection(connectivityManager);
        return connection;

    }

    public static void flashTextView() {
        Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.teext_view_flashing);
        mTextView.setAnimation(animation);
        mTextView.startAnimation(animation);


    }


}
