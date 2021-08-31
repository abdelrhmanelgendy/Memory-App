package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.services.DownLoadInfo;
import com.example.myapplication.services.ImagesListDownLoader;
import com.example.myapplication.jsonConverter.MemoryConverter;
import com.example.myapplication.pojo.Memory;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowMainImageFromMemoryViewer extends AppCompatActivity implements DownLoadInfo {
    private static final int EXTERNAL_STORAGE_RQ = 5451;
    PhotoView photoViewMainImage;
    Toolbar toolbar;
    Memory memory;
    ImageView imgBack, imgDownLoad;
    TextView txtViewDialogConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_main_image_from_memory_viewer);
        init();
    }


    private void init() {
        txtViewDialogConnection = findViewById(R.id.activity_showMainImage_txtConnectionDialog);
        imgBack = findViewById(R.id.activity_showMainImage_imgBack);
        imgDownLoad = findViewById(R.id.activity_showMainImage_imgDownLoad);
        memory = new MemoryConverter().ConvertToMemory(getIntent().getStringExtra(MemoryViewer.JGSONED_MEMORY));
        photoViewMainImage = findViewById(R.id.activity_showMainImage_Image);
        Picasso.get().load(memory.getMainPicUrl()).into(photoViewMainImage);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        imgDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          downLoad();
            }
        });
    }

    private void downLoad() {
        if (CheckInternetConnection.connection(getApplicationContext())) {
            String memoryName = memory.getTittle();
            String url = memory.getMainPicUrl();
            Log.d("openUserPicture", "onClick: " + url);
            List<String> urlList = new ArrayList<>();
            urlList.add(url);
            if (ImagesListDownLoader.isDownloading) {
                Toast.makeText(getApplicationContext(), "please wait until current download is finished", Toast.LENGTH_SHORT).show();
            } else {

                if (CheckInternetConnection.connection(getApplicationContext()))
                {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        startRequestPermission();
                    } else {
                        startDownLoadService(urlList, memoryName);
                    }
                }





            }
        }
        else
        {
            InitializeConnectionDialog.flashTextView();
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_RQ);
    }
    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txtViewDialogConnection, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        InitializeConnectionDialog.endCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.down_load_images_menu, menu);
        return true;
    }


    private void startDownLoadService(List<String> urlList, String memoryName) {

        ImagesListDownLoader.downLoadInfo = this;
        Intent serviceIntent = null;
        for (int i = 0; i < urlList.size(); i++) {
            serviceIntent = new Intent(ShowMainImageFromMemoryViewer.this, ImagesListDownLoader.class);
            serviceIntent.putExtra(ImageShowerFromMemory.MEMORY_NAME, memoryName);
            serviceIntent.putExtra(ImageShowerFromMemory.SIZE, urlList.size());
            serviceIntent.putExtra(ImageShowerFromMemory.POSITION, (i + 1));
            serviceIntent.putExtra(ImageShowerFromMemory.URL, urlList.get(i));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }


    }


    @Override
    public void DownLoading(int progress) {

    }

    @Override
    public void pending() {

    }

    @Override
    public void Finished() {
        Intent serviceIntent = new Intent(ShowMainImageFromMemoryViewer.this, ImagesListDownLoader.class);
        stopService(serviceIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_RQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downLoad();
            }
        }
    }
}