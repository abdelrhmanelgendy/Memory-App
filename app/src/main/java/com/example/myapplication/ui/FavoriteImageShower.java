package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ImageViewSliderAdapter;
import com.example.myapplication.fragments.FavoriteFragment;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.FileExtensionAndName;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.dialogs.ProgressDialog;
import com.example.myapplication.jsonConverter.ListOfImageToUploadConverter;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.services.DownLoadInfo;
import com.example.myapplication.services.ImagesListDownLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteImageShower extends AppCompatActivity implements DownLoadInfo {
    private static final int EXTERNAL_STORAGE_RQ = 21;
    private static final String TAG = "TAG41";
    ViewPager viewPager;
    ImageViewSliderAdapter sliderAdapter;
    List<ImageToUpload> inPutList = new ArrayList<>();
    int position;
    int swipedPosition;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    TextView txtMemoryName;
    DatabaseReference lastDeletedRef;
    ImageToUpload lastImageToUpload;
    int lastPosition;
    TextView txViewConnectioDialog;
    ImageView btnBack, btnDownLoad, btnShare, btnRemoveFromFavourite;
    String memoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTransition();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_favorite_image_shower);
        ListOfImageToUploadConverter converter = new ListOfImageToUploadConverter();
        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.getStringExtra(FavoriteFragment.LIST_OF_IMAGE_TO_UPLOAD);
            position = intent.getIntExtra(FavoriteFragment.POSITION_OF_CLICKED_IMAGE, -1);
            inPutList = converter.getListFromString(stringExtra);
            swipedPosition = position;
        }
        sliderAdapter = new ImageViewSliderAdapter(inPutList, this, true);
        viewPager.setAdapter(sliderAdapter);
        viewPager.setCurrentItem(position);
        txtMemoryName.setText(inPutList.get(swipedPosition).getMemoryTittle());
        memoryName = inPutList.get(swipedPosition).getMemoryTittle();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                swipedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        progressDialog = new ProgressDialog(this, false);

    }

    private void initTransition() {
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        Fade fade = new Fade();
        View decorView = getWindow().getDecorView();

        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    private void initViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        viewPager = findViewById(R.id.activity_favoriteImage_viewPager);
        btnBack = findViewById(R.id.activity_favoriteImage_imageBack);
        btnDownLoad = findViewById(R.id.activity_favoriteImage_imageDownLoad);
        btnRemoveFromFavourite = findViewById(R.id.activity_favoriteImage_imageFavorite);
        btnShare = findViewById(R.id.activity_favoriteImage_imageShare);

        btnBack.setOnClickListener(i -> finish());
        btnDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnection.connection(getApplicationContext())) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        startRequestPermission();
                    } else {
                        downLoad();
                    }
                } else {
                    txViewConnectioDialog.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.teext_view_flashing));
                    txViewConnectioDialog.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.teext_view_flashing));
                }

            }
        });
        btnRemoveFromFavourite.setOnClickListener(i -> removeFromFavorite());
        btnShare.setOnClickListener(i -> share());
        txtMemoryName = findViewById(R.id.activity_favoriteImage_txtMemoryName);
        txViewConnectioDialog = findViewById(R.id.activity_favoriteImage_txtViewConnection);
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_RQ);

    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txViewConnectioDialog, getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    ProgressDialog progressDialog;

    private void share() {
        progressDialog.show(getResources().getString(R.string.gettingPhotoPath));
        ImageToUpload imageToUpload = inPutList.get(swipedPosition);
        ArrayList<Uri> uriBitbam = getUriBitbam(imageToUpload);


    }

    ArrayList<Uri> getUriBitbam(ImageToUpload imageToUpload) {
        ArrayList<Uri> imagesUri = new ArrayList<>();

        String fileName = FileExtensionAndName.get(this).getFileName(imageToUpload.getUrl());

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                try {
                    File mydir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                            File.separator + "SharedMemory" + File.separator + imageToUpload.getMemoryTittle());
                    if (!mydir.exists()) {
                        mydir.mkdirs();
                    } else {
                        deleteAllListOfFiles(mydir);
                    }

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String fileUri = mydir.getAbsolutePath() + File.separator + fileName;
                            FileOutputStream outputStream = null;
                            try {
                                outputStream = new FileOutputStream(fileUri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            try {
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Uri parse = Uri.parse(fileUri);
                            progressDialog.dismiss();
                            Intent sharerIntent = new Intent(Intent.ACTION_SEND);
                            String stringToShare = "Memory Tittle: " + imageToUpload.getMemoryTittle();
                            sharerIntent.setType("image/png");
                            sharerIntent.putExtra(Intent.EXTRA_TEXT, stringToShare);
                            sharerIntent.putExtra(Intent.EXTRA_STREAM, parse);
                            startActivity(sharerIntent);
                        }
                    });
                    thread.start();

                } catch (Exception e) {
                    Log.d("TAG51", "onBitmapLoaded: " + e.getMessage());
                }


            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(imageToUpload.getUrl()).into(target);
        return imagesUri;


    }

    private void deleteAllListOfFiles(File mydir) {
        for (File file : mydir.listFiles()) {
            if (file.exists()) {
                boolean delete = file.delete();
                Log.d(TAG, "deleteAllListOfFiles: " + delete);

            }
        }
    }

    private void removeFromFavorite() {
        if (!CheckInternetConnection.connection(getApplicationContext())) {
            txViewConnectioDialog.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.teext_view_flashing));
            txViewConnectioDialog.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.teext_view_flashing));
        } else {
            try {


                Log.d("TAG4000", "removeFromFavorite: " + swipedPosition);
                if (inPutList.size() == 0) {
                    finish();
                }
                ImageToUpload imageToUpload = inPutList.get(swipedPosition);
                String memoryId = imageToUpload.getMemoryId();
                String imageId = imageToUpload.getId();

                firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference child = firebaseDatabase.getReference("UserMemory").child(firebaseAuth.getCurrentUser().getUid())
                        .child("memory_" + memoryId)
                        .child("pictures")
                        .child(String.valueOf(imageId))
                        .child("imageFavorite");
                child.setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (inPutList.size() == 0) {
                            return;
                        }
                        if (swipedPosition > inPutList.size() || swipedPosition == inPutList.size()) {
                            return;
                        }

                        lastDeletedRef = child;
                        showSneakBar();
                        lastImageToUpload = inPutList.get(swipedPosition);
                        inPutList.remove(swipedPosition);
                        lastPosition = swipedPosition;


                        sliderAdapter = new ImageViewSliderAdapter(inPutList, getApplicationContext(), true);

                        viewPager.setAdapter(sliderAdapter);
                        if (inPutList.size() == 0) {
                            finish();
                        }
                        if (swipedPosition == inPutList.size()) {
                            viewPager.setCurrentItem(0);
                        } else if (swipedPosition == inPutList.size()) {
                            viewPager.setCurrentItem(1);
                        } else {

                            viewPager.setCurrentItem(swipedPosition);

                        }

//                viewPager.setCurrentItem(swipedPosition+1);
                    }
                });
            } catch (Exception e) {
                return;
            }
        }
    }

    private void showSneakBar() {
//
//        Snackbar.make(favoriteImageShowerBinding.activityFavoriteImageViewPager, "photo removed from favourite", Snackbar.LENGTH_SHORT)
//                .setActionTextColor(getResources().getColor(R.color.dpink))
//                .setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        lastDeletedRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                inPutList.add((lastPosition),lastImageToUpload);
//                                sliderAdapter.notifyDataSetChanged();
//                                viewPager.setCurrentItem((lastPosition));
//
//                            }
//                        });
//                    }
//                }).show();
    }

    private void downLoad() {
//        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                + "/" + mContext.getResources().getString(R.string.app_name) + "/" + memoryName
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + getApplicationContext().getResources().getString(R.string.app_name) + "/" + memoryName + "/" + "favouriteimages");
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            Log.d("TAG51", "downLoad: " + mkdir);
        }

        ImageToUpload imageToUpload = inPutList.get(swipedPosition);
        List<String> list = new ArrayList<>();

        if (ImagesListDownLoader.isDownloading) {
            Toast.makeText(getApplicationContext(), "please wait until current download is finished", Toast.LENGTH_SHORT).show();
        } else {
            list.add(imageToUpload.getUrl());

            startDownLoadService(list, imageToUpload.getMemoryTittle());


        }


    }


    private void startDownLoadService(List<String> urlList, String memoryName) {

        ImagesListDownLoader.downLoadInfo = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = null;
                for (int i = 0; i < urlList.size(); i++) {
                    serviceIntent = new Intent(FavoriteImageShower.this, ImagesListDownLoader.class);
                    serviceIntent.putExtra(ImageShowerFromMemory.MEMORY_NAME, memoryName );
                    serviceIntent.putExtra(ImageShowerFromMemory.SIZE, urlList.size());
                    serviceIntent.putExtra(ImageShowerFromMemory.POSITION, (i + 1));
                    serviceIntent.putExtra(ImageShowerFromMemory.URL, urlList.get(i));

                    startService(serviceIntent);

                }
            }
        });
        thread.start();


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

    @Override
    public void DownLoading(int progress) {

    }

    @Override
    public void pending() {

    }

    @Override
    public void Finished() {
        Intent serviceIntent = new Intent(FavoriteImageShower.this, ImagesListDownLoader.class);
        stopService(serviceIntent);
    }
}
