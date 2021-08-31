package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.R;
import com.example.myapplication.adapters.ImageViewSliderAdapter;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.helpers.SoundsPlayer;
import com.example.myapplication.services.DownLoadInfo;
import com.example.myapplication.services.ImagesListDownLoader;
import com.example.myapplication.jsonConverter.MemoryConverter;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ImageShowerFromMemory extends AppCompatActivity implements DownLoadInfo {

    private static final int EXTERNAL_STORAGE_RQ = 51;
    ImagesListDownLoader downLoader;
    List<ImageToUpload> imgsUrl = new ArrayList<>();
    public static List<Integer> editedItems = new ArrayList<>();
    private static final String TAG = "TAG51";
    ViewPager viewPager;
    Animation animation;
    public static int selectedPosition = -1;
    Memory memory1;
    public static final String URL = "url";
    public static final String POSITION = "position";
    public static final String MEMORY_NAME = "memoryName";
    public static final String SIZE = "size";
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    ImageView imgDownLoad;
    ImageView imgback;
    LottieAnimationView heartLottie;
    ImageView imgFavorite;
    TextView textViewConnectionDialog;
    Intent serviceIntent;
    Animation flashingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flashingAnimation=AnimationUtils.loadAnimation(this,R.anim.teext_view_flashing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_shower_from_memory);
        serviceIntent = new Intent(ImageShowerFromMemory.this, ImagesListDownLoader.class);

        editedItems.clear();
        animation = AnimationUtils.loadAnimation(this, R.anim.activity_slide_in_left);
        transitionAnimation();
        init();

        Intent intent = getIntent();
        String memory = intent.getStringExtra(MemoryViewer.JGSONED_MEMORY);

        int position = intent.getIntExtra(MemoryViewer.IMAGE_POSITION, -1);
        MemoryConverter memoryConverter = new MemoryConverter();
        memory1 = memoryConverter.ConvertToMemory(memory);

        List<ImageToUpload> pictures = memory1.getPictures();
        imgsUrl.addAll(pictures);
        ImageViewSliderAdapter sliderAdapter = new ImageViewSliderAdapter(imgsUrl, getApplicationContext(), false);
        viewPager = findViewById(R.id.activity_image_show_viewPager);
        viewPager.setAdapter(sliderAdapter);
        viewPager.setCurrentItem(position);
        selectedPosition = position;
        if (selectedPosition != -1) {
            setFavoriteIconColor(memory1.getPictures().get(selectedPosition).isImageFavorite());
        }
        Log.d(TAG, "onCreate: " + position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPosition = position;
                setFavoriteIconColor(memory1.getPictures().get(position).isImageFavorite());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (selectedPosition != -1) {
            setFavoriteIconColor(memory1.getPictures().get(selectedPosition).isImageFavorite());
        }
        imgDownLoad.setOnClickListener(i -> downLoad());
    }

    private void downLoad() {

        if (CheckInternetConnection.connection(this))
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                startRequestPermission();
            } else {
                downLoadCurrentImage();
            }
        }
        else
        {
            textViewConnectionDialog.setAnimation(flashingAnimation);
            textViewConnectionDialog.startAnimation(flashingAnimation);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(textViewConnectionDialog, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    private void init() {
        textViewConnectionDialog = findViewById(R.id.imageShower_txtConnectionDialog);
        downLoader = new ImagesListDownLoader();

        imgFavorite = findViewById(R.id.imageShower_imgFavorite);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        imgback = findViewById(R.id.activity_MemoryImageShower_back);
        imgDownLoad = findViewById(R.id.activity_MemoryImageShower_downLoad);

        heartLottie = findViewById(R.id.imageShower_lottieHearts);

//        getSupportActionBar()
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (CheckInternetConnection.connection(getApplicationContext()))
              {
                  boolean isImageFavorite = memory1.getPictures().get(selectedPosition).isImageFavorite();
                  if (isImageFavorite) {
                      updateFalse(selectedPosition, memory1.getMemoryID());
                  } else {
                      updateTrue(selectedPosition, memory1.getMemoryID());
                  }
                  editedItems.add(selectedPosition);
              }
              else
              {
                  textViewConnectionDialog.setAnimation(flashingAnimation);
                  textViewConnectionDialog.startAnimation(flashingAnimation);
              }
            }
        });
        imgback.setOnClickListener(o -> onBackPressed());


    }

    private void updateTrue(int selectedPosition, int memoryID) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("UserMemory").child(firebaseUser.getUid())
                .child("memory_" + memoryID)
                .child("pictures")
                .child(String.valueOf(selectedPosition))
                .child("imageFavorite")
                .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SoundsPlayer soundsPlayer =new SoundsPlayer(getApplicationContext(),R.raw.memory_like);
                soundsPlayer.play();
                memory1.getPictures().get(selectedPosition).setImageFavorite(true);
                setFavoriteIconColor(true);
                MemoryViewer.memory.getPictures().get(selectedPosition).setImageFavorite(true);
                heartLottie.playAnimation();
            }
        });


    }


    private void updateFalse(int position, int index) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("UserMemory").child(firebaseUser.getUid())
                .child("memory_" + index)
                .child("pictures")
                .child(String.valueOf(position))
                .child("imageFavorite")
                .setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SoundsPlayer soundsPlayer =new SoundsPlayer(getApplicationContext(),R.raw.memory_dis_like);
                soundsPlayer.play();
                memory1.getPictures().get(selectedPosition).setImageFavorite(false);
                setFavoriteIconColor(false);
                MemoryViewer.memory.getPictures().get(selectedPosition).setImageFavorite(true);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_RQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downLoadCurrentImage();
            }
        }
    }

    private void downLoadCurrentImage() {
        String memoryName = memory1.getTittle();
        String url = imgsUrl.get(selectedPosition).getUrl();
        List<String> urlList = new ArrayList<>();
        urlList.add(url);
        if (ImagesListDownLoader.isDownloading) {
            Toast.makeText(getApplicationContext(), "please wait until current download is finished", Toast.LENGTH_SHORT).show();
        } else {
            startDownLoadService(urlList, memoryName);


        }


    }


    private void startDownLoadService(List<String> urlList, String memoryName) {
        ImagesListDownLoader.downLoadInfo = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = null;
                for (int i = 0; i < urlList.size(); i++) {
                    serviceIntent = new Intent(ImageShowerFromMemory.this, ImagesListDownLoader.class);
                    serviceIntent.putExtra(MEMORY_NAME, memoryName);
                    serviceIntent.putExtra(SIZE, urlList.size());
                    serviceIntent.putExtra(POSITION, (i + 1));
                    serviceIntent.putExtra(URL, urlList.get(i));

                    startService(serviceIntent);

                }
            }
        });
        thread.start();


    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_RQ);
    }


    private void transitionAnimation() {
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        Fade fade = new Fade();
        View decorView = getWindow().getDecorView();

        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(decorView.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(findViewById(R.id.activity_image_show_viewPager), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    @Override
    public void DownLoading(int progress) {
        Log.d(TAG, "DownLoading: " + progress);
    }

    @Override
    public void pending() {
        Log.d(TAG, "pending: ");
    }

    @Override
    public void Finished() {
        Log.d(TAG, "Finished: ");
        stopService(serviceIntent);

    }

    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        private int currentPage;

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
        }

        public final int getCurrentPage() {
            return currentPage;
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    void setFavoriteIconColor(boolean isFavorite) {

        imgFavorite.setImageDrawable(isFavorite ? getResources().getDrawable(R.drawable.favorite_red) : getResources().getDrawable(R.drawable.favoritr_white));
    }
}

