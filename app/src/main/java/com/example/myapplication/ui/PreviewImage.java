package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.pojo.Image;
import com.squareup.picasso.Picasso;

public class PreviewImage extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        init(intent);


    }

    private void init(Intent intent) {
        String uri = intent.getStringExtra(AddNewMemory.IMAGE_URI_EXTRA);
        String tittle = intent.getStringExtra(AddNewMemory.MEMORY_TITLE);

        toolbar = findViewById(R.id.ActivityPrev_toolBar);
        imageView = findViewById(R.id.ActivityPrev_imageView);

        toolbar.setTitle(tittle);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d("TAG", "init: " + uri);
        imageView.setImageURI(Uri.parse(uri));
    }
}