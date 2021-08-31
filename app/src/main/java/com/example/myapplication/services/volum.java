package com.example.myapplication.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.controls.templates.ToggleTemplate;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;

public class volum extends AccessibilityService {
    private static final String TAG = "TAG205";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG,"Service connected");

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d("TAG205","Key pressed via accessibility is: "+event.getKeyCode());
        return super.onKeyEvent(event);
    }
}
