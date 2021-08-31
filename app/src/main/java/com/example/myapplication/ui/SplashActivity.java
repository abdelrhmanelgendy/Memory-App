package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.helpers.DarkModeHelper;
import com.example.myapplication.helpers.LanguageHelper;
import com.example.myapplication.jsonConverter.UserConverter;
import com.example.myapplication.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    public static final String SPLASH_USER_KEY = "userKey";
    public static final String IS_FIRST_TIME = "firstOpen";
    public static String FiRST_TIME_OPEN="firstTimeAction";
    ImageView imgBackGround;
    Handler handler;

    ImageView imageDot;
    Drawable drawable;
    FirebaseAuth firebaseAuth;
    Animation fadeInAnimation, translateAnimation;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Intent intentDashboard;

    @Override
    protected void onResume() {
        super.onResume();
//        onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initMode();

        super.onCreate(savedInstanceState);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTimeOpenApp = defaultSharedPreferences.getBoolean(IS_FIRST_TIME, true);


        setContentView(R.layout.activity_splash2);
        intentDashboard = new Intent(SplashActivity.this, DashBoard.class);
        init();
        imageDot.setAnimation(fadeInAnimation);
        imgBackGround.setAnimation(translateAnimation);


        if (firebaseAuth.getCurrentUser() != null) {
            getUserData();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstTimeOpenApp) {
                    Intent languageIntent =new Intent(SplashActivity.this,ActivityLanguage.class);
                    languageIntent.setAction(FiRST_TIME_OPEN);

                    startActivity(languageIntent);
                }
                else {

                    if (firebaseAuth.getCurrentUser() != null) {


                        goDashBoard();
                    } else {
                        Pair[] pair = new Pair[2];
                        pair[0] = new Pair<View, String>(imgBackGround, ViewCompat.getTransitionName(imgBackGround));
                        pair[1] = new Pair<View, String>(imageDot, ViewCompat.getTransitionName(imageDot));
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        ActivityOptions a = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pair);
                        startActivity(i, a.toBundle());

                    }

                }
            }
        }, 2000);
    }

    private void initMode() {
        DarkModeHelper.init(this);
        String currentMode = DarkModeHelper.getPersistData();
        if (currentMode.equals(DarkModeHelper.NIGHT_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        String persistData = LanguageHelper.getPersistData(this);
        LanguageHelper.setLocale(this,persistData);



    }

    private void getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot != null) {
                        User currentUserData = snapshot.getValue(User.class);
                        String stringedUser = UserConverter.getStringedUser(currentUserData);
                        intentDashboard.putExtra(SPLASH_USER_KEY, stringedUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goDashBoard() {
        startActivity(intentDashboard);
    }


    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        imgBackGround = findViewById(R.id.Splash2_imageLogo);
        imageDot = findViewById(R.id.Splash2_imageDots);


        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_fade_in);
        translateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_translate_from_down);


    }

    @Override
    public void onBackPressed() {

    }

    public void createShortCut() {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.camera));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_baseline_download_24);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), AddNewMemory.class));
        sendBroadcast(shortcutintent);
    }
}