package com.example.myapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.CustomeGalleryAdapter;
import com.example.myapplication.jsonConverter.ImageFromStorageGSONConverter;
import com.example.myapplication.pojo.Image;
import com.example.myapplication.pojo.ImageFromStorag;
import com.example.myapplication.util.OnImageFromStorageClickListeners;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;



public class CustomeGalley extends AppCompatActivity implements OnImageFromStorageClickListeners {

    public static final String LIST_OF_SELECTED_IMAGES = "selected_images";
    RecyclerView recyclerViewImages;
    CustomeGalleryAdapter galleryAdapter;
    ArrayList<ImageFromStorag> imageFromStoragsList = new ArrayList<>();
    GridLayoutManager gridLayoutManager;
    TextView txtChoosen, btnOk;
    ImageView btnBack;
    public static String CustomeGalley_ACTION="galleryAction";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custome_galley);
        init();
        getImageData();
        galleryAdapter.setImageFromStorageClickListeners(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send All to Add Memory

                ArrayList<ImageFromStorag> selectedImages = galleryAdapter.getSelectedImages();
                Intent intent =new Intent(CustomeGalley.this,AddNewMemory.class);
                ImageFromStorageGSONConverter converter =new ImageFromStorageGSONConverter(new Gson());
                String convertToJSONImage = converter.convertToJSON(selectedImages);
                intent.putExtra(LIST_OF_SELECTED_IMAGES,convertToJSONImage);
                intent.setAction(CustomeGalley_ACTION);
                Log.d("TAG", "onClick: "+convertToJSONImage);
                startActivity(intent);
//                finish();
            }
        });

    }

    private void getImageData() {
        String[] projection = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED};
        String order = MediaStore.Images.Media.DATE_TAKEN;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, order);
        if (cursor.moveToFirst()) {
            int x = 0;
            do {
                int dataColumnName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String paths = cursor.getString(dataColumnName);

                int dateColumnName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
                String dates = cursor.getString(dateColumnName);
                Image image = new Image(x, paths);
                ImageFromStorag imageFromStorag = new ImageFromStorag(image, dates, false);
                imageFromStoragsList.add(imageFromStorag);
                x++;


            }
            while (cursor.moveToNext());
            Collections.reverse(imageFromStoragsList);
            galleryAdapter.notifyDataSetChanged();


        }
    }

    private void init() {


        recyclerViewImages = findViewById(R.id.customGallery_rec);
        galleryAdapter = new CustomeGalleryAdapter(imageFromStoragsList, this);
        gridLayoutManager = new GridLayoutManager(this, 4);


        recyclerViewImages.setLayoutManager(gridLayoutManager);
        recyclerViewImages.setAdapter(galleryAdapter);
        txtChoosen = findViewById(R.id.customeGallery_txtChoosen);
        btnOk = findViewById(R.id.customeGallery_btnOk);
        btnBack = findViewById(R.id.customeGallery_btnBack);


    }

    @Override
    public void onImageClick(ImageFromStorag imageFromStorag, boolean state) {
        isCleared=false;
        ArrayList<ImageFromStorag> selectedImages = galleryAdapter.getSelectedImages();
        if (state==OnImageFromStorageClickListeners.SINGLE_SELECTION)
        {


        }
        else
        {
            if (selectedImages.size() == 0) {
                txtChoosen.setText("Tap or hold to select");
                btnOk.setVisibility(View.GONE);
            } else {
                txtChoosen.setText("Selected "+selectedImages.size());
                btnOk.setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    public void onImageLongClick(ImageFromStorag imageFromStorag) {
        isCleared=false;
        ArrayList<ImageFromStorag> selectedImages = galleryAdapter.getSelectedImages();

        if (selectedImages.size() == 0) {
            txtChoosen.setText("Tap or hold to select");
            btnOk.setVisibility(View.GONE);
        } else {
            txtChoosen.setText("Selected "+selectedImages.size());
            btnOk.setVisibility(View.VISIBLE);
        }
    }
boolean isCleared=false;
    @Override
    public void onBackPressed() {
        if (galleryAdapter.getSelectedImages().size()>0)
        {
            if (isCleared)
            {
                super.onBackPressed();
            }
            else
            {
                for (ImageFromStorag imageFromStorag :imageFromStoragsList)
                {
                    imageFromStorag.setChecked(false);
                    galleryAdapter.notifyDataSetChanged();
                    isCleared=true;
                    btnOk.setVisibility(View.GONE);
                    txtChoosen.setText("Tap or hold to select");
                }
            }
        }
        else
        {
            super.onBackPressed();
        }

    }
}