package com.example.myapplication.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityLanguageBinding;
import com.example.myapplication.helpers.DarkModeHelper;

public class ActivityDisplayMode extends AppCompatActivity {

    ImageView btnBack;
    ImageView btnOpenLoginn;
    Switch switchModeChange;
    TextView textViewModeName;
    boolean is_first_time = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        Intent intent = new Intent(ActivityDisplayMode.this, SplashActivity.class);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViews();
        getCurrentMode();
        Intent languageIntent = getIntent();
        if (languageIntent.getAction() != null) {

            if (languageIntent.getAction().equals(SplashActivity.FiRST_TIME_OPEN)) {
                is_first_time = true;
                btnBack.setVisibility(View.GONE);
                btnOpenLoginn.setVisibility(View.VISIBLE);
            } else {
                is_first_time = false;

                btnBack.setVisibility(View.VISIBLE);
                btnOpenLoginn.setVisibility(View.GONE);
            }

        } else {
            is_first_time = false;

            btnBack.setVisibility(View.VISIBLE);
            btnOpenLoginn.setVisibility(View.GONE);
        }
        switchModeChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {


                    String currentMode = DarkModeHelper.getPersistData();
                    Log.d("TAG21", "getCurrentMode: " + currentMode);


                    if (isChecked) {

                        DarkModeHelper.setMode(AppCompatDelegate.MODE_NIGHT_YES);
                        textViewModeName.setText(getResources().getString(R.string.ON));
                        if (!is_first_time) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 300);
                        }


                    } else {
                        DarkModeHelper.setMode(AppCompatDelegate.MODE_NIGHT_NO);
                        textViewModeName.setText(getResources().getString(R.string.OFF));

                        if (!is_first_time) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 300);
                        }

                    }

//                    startActivity(intent);

                } catch (Exception e) {
                    return;
                }
            }


        });

        btnOpenLoginn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putBoolean(SplashActivity.IS_FIRST_TIME, false);
                edit.apply();
                Intent loginIntent = new Intent(ActivityDisplayMode.this, SplashActivity.class);
                startActivity(loginIntent);
                }
                catch (Exception e)
                {
                    return;
                }
            }
        });
    }


    private void getCurrentMode() {
        try {


        DarkModeHelper.init(this);
        String currentMode = DarkModeHelper.getCurrentMode();
        Log.d("TAG21", "getCurrentMode: " + currentMode);
        if (currentMode.equals(DarkModeHelper.LIGHT_MODE)) {
            textViewModeName.setText(getResources().getString(R.string.OFF));
            switchModeChange.setChecked(false);
        } else if (currentMode.equals(DarkModeHelper.NIGHT_MODE)) {
            textViewModeName.setText(getResources().getString(R.string.ON));
            switchModeChange.setChecked(true);
        }
        }
        catch (Exception e)
        {
            return;
        }


    }

    private void initViews() {
        btnBack = findViewById(R.id.displayModeActivity_btnBack);
        btnOpenLoginn = findViewById(R.id.btnGOLogin);
        textViewModeName = findViewById(R.id.displayModeActivity_txtModeName);
        switchModeChange = findViewById(R.id.displayModeActivity_switch);
        btnBack.setOnClickListener(i -> onBackPressed());
    }


}