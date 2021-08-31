package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.databases.local.MemoryViewModel;


import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class TEST extends AppCompatActivity {


    private static final String TAG = "TAG205";
    Handler mHandler = new Handler();
    Runnable runnable;
    MemoryViewModel roomViewModel;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_e_s_t);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        textView = findViewById(R.id.test_dialog);



    }

}


