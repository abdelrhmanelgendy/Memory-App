package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityLanguageBinding;
import com.example.myapplication.helpers.LanguageHelper;

public class ActivityLanguage extends AppCompatActivity {
    ActivityLanguageBinding activityLanguageBinding;
    private static final String TAG = "ActivityLanguage11";
    boolean Is_firstTime;
    RadioGroup radioGroup;

    @Override
    protected void onResume() {
        super.onResume();
        String deviceLanguage = LanguageHelper.getPersistData(this);
        LanguageHelper.setLocale(this, deviceLanguage);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String deviceLanguage = "en";
        try {


            deviceLanguage = LanguageHelper.getPersistData(this);
            LanguageHelper.setLocale(this, deviceLanguage);
        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityLanguageBinding = DataBindingUtil.setContentView(this, R.layout.activity_language);

        Intent intent = getIntent();
        if (intent.getAction() != null) {
            if (intent.getAction().equals(SplashActivity.FiRST_TIME_OPEN)) {
                Is_firstTime = true;
                activityLanguageBinding.languageActivityBtnBack.setVisibility(View.GONE);
                activityLanguageBinding.btnChangeMode.setVisibility(View.VISIBLE);
            } else {
                Is_firstTime = false;

                activityLanguageBinding.languageActivityBtnBack.setVisibility(View.VISIBLE);
                activityLanguageBinding.btnChangeMode.setVisibility(View.GONE);
            }
        } else {
            Is_firstTime = false;

            activityLanguageBinding.languageActivityBtnBack.setVisibility(View.VISIBLE);
            activityLanguageBinding.btnChangeMode.setVisibility(View.GONE);
        }

        if (deviceLanguage.equals(LanguageHelper.ARABIC_CODE)) {
            Log.d(TAG, "onCreate: arabic ");

            activityLanguageBinding.languageActivityRadioArabic.setChecked(true);
            activityLanguageBinding.languageActivityRadioEnglish.setChecked(false);
            LanguageHelper.setLocale(ActivityLanguage.this, LanguageHelper.ARABIC_CODE);


        } else {
            Log.d(TAG, "onCreate: english");

            activityLanguageBinding.languageActivityRadioEnglish.setChecked(true);
            activityLanguageBinding.languageActivityRadioArabic.setChecked(false);
            LanguageHelper.setLocale(ActivityLanguage.this, LanguageHelper.ENGLISH_CODE);

        }
        initData();

        activityLanguageBinding.languageActivityRadioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {


                    if (isChecked) {
                        activityLanguageBinding.languageActivityRadioArabic.setChecked(false);
                        Log.d(TAG, "onCheckedChanged: englishPressed");
                        LanguageHelper.setLocale(ActivityLanguage.this, LanguageHelper.ENGLISH_CODE);
                        LanguageHelper.persist(getApplicationContext(), LanguageHelper.ENGLISH_CODE);
                        if (!Is_firstTime) {
                            reopen();
                        }

                    }
                } catch (Exception e) {
                    return;
                }

            }
        });
        activityLanguageBinding.languageActivityRadioArabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {


                    if (isChecked) {
                        activityLanguageBinding.languageActivityRadioEnglish.setChecked(false);
                        Log.d(TAG, "onCheckedChanged: arabic changes");

                        LanguageHelper.setLocale(ActivityLanguage.this, LanguageHelper.ARABIC_CODE);
                        LanguageHelper.persist(getApplicationContext(), LanguageHelper.ARABIC_CODE);
                        if (!Is_firstTime) {
                            reopen();
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
        activityLanguageBinding.languageActivityBtnBack.setOnClickListener(i -> finish());
        activityLanguageBinding.btnChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                Intent chooseMode = new Intent(ActivityLanguage.this, ActivityDisplayMode.class);
                chooseMode.setAction(SplashActivity.FiRST_TIME_OPEN);
                startActivity(chooseMode);
                }catch (Exception e)
                {

                }

            }
        });
    }

    private void initData() {
//        String persistData = LanguageHelper.getPersistData(this);
//        if (persistData.equals(LanguageHelper.ARABIC_CODE)) {
//            activityLanguageBinding.languageActivityRadioArabic.setChecked(true);
//            activityLanguageBinding.languageActivityRadioEnglish.setChecked(false);
//        } else {
//            activityLanguageBinding.languageActivityRadioArabic.setChecked(false);
//            activityLanguageBinding.languageActivityRadioEnglish.setChecked(true);
//        }

    }

    @Override
    public void onBackPressed() {
        if (Is_firstTime) {
            finishAffinity();
        } else {
            finish();
        }

    }

    void reopen() {

        Intent intent = new Intent(ActivityLanguage.this, DashBoard.class);
        startActivity(intent);
    }
}