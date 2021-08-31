package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.DownloadedImage;
import com.example.myapplication.adapters.MemoryViewerAdapter;
import com.example.myapplication.fragments.HomeFragement;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.helpers.PicassoBitmab;
import com.example.myapplication.services.DownLoadInfo;
import com.example.myapplication.services.ImagesListDownLoader;
import com.example.myapplication.jsonConverter.MemoryConverter;
import com.example.myapplication.pojo.CurrentLocation;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.util.ImagePreviewClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemoryViewer extends AppCompatActivity implements DownLoadInfo {

    public static final String JGSONED_MEMORY = "convertedMemory";
    public static final String IMAGE_POSITION = "image_position";
    public static final String IMAGE_URI = "image_uri";
    private static final int WRITE_STORAGE_REQ = 151;
    CircleImageView circleImageViewMainImage;
    TextView txtTittle;
    TextView txtLocation;
    TextView txtDate;
    TextView textViewDailogConnection;
    RecyclerView recyclerViewAllImages;
    private static final String TAG = "MemoryViewer";
    MemoryViewerAdapter viewerAdapter;
    List<DownloadedImage> imgsUrl = new ArrayList<>();
    GridLayoutManager gridLayoutManager;
    public static Memory memory;
    TextView txtToolBar;

    private MenuItem itemDownLoad;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    int selectedPosition = -1;
    ImageView btnDownLoad, btnBack, btnShare;
    Intent serviceIntent;
    Animation flashingAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_viewer);
        flashingAnimation = AnimationUtils.loadAnimation(this, R.anim.teext_view_flashing);
        serviceIntent = new Intent(MemoryViewer.this, ImagesListDownLoader.class);
        transitionAnimation();


        textViewDailogConnection = findViewById(R.id.memoryViewer_dailogConnection);
        InitializeConnectionDialog.changeConnetion(textViewDailogConnection, this);


        init();


        Intent intent = getIntent();
        initData(intent);
        setMenueVisibiltiy(false);


    }


    private void initMotions() {

    }

    void setMenueVisibiltiy(boolean visibiltiy) {
        btnDownLoad.setVisibility(visibiltiy ? View.VISIBLE : View.GONE);
//        btnShare.setVisibility(visibiltiy ? View.VISIBLE : View.GONE);
//        if (visibiltiy) {
//            startConstraintSet.setVisibility(motionsId[0], ConstraintSet.VISIBLE);
//            startConstraintSet.setVisibility(motionsId[1], ConstraintSet.VISIBLE);
//            endConstraintSet.setVisibility(motionsId[2], ConstraintSet.VISIBLE);
//            endConstraintSet.setVisibility(motionsId[3], ConstraintSet.VISIBLE);
//        } else {
//            startConstraintSet.setVisibility(motionsId[0], ConstraintSet.INVISIBLE);
//            startConstraintSet.setVisibility(motionsId[1], ConstraintSet.INVISIBLE);
//            endConstraintSet.setVisibility(motionsId[2], ConstraintSet.INVISIBLE);
//            endConstraintSet.setVisibility(motionsId[3], ConstraintSet.INVISIBLE);
//        }


    }

    void getDataFromFireBase() {
        List<Integer> returnedPosition = ImageShowerFromMemory.editedItems;
        if (selectedPosition != -1) {
            firebaseDatabase.getReference("UserMemory").child(firebaseUser.getUid()).child("memory_" + memory.getMemoryID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Memory memory = snapshot.getValue(Memory.class);
                            viewerAdapter.setImageToUploads(memory.getPictures());
                            viewerAdapter.setNotifyImagesAlso(false);
                            for (int i : returnedPosition) {
                                viewerAdapter.notifyItemChanged(i);
                            }
                            convertedMemory = new MemoryConverter().convertToGson(memory);
                            selectedPosition = -1;
                            returnedPosition.clear();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
//        InitializeConnectionDialog.changeConnetion(textViewDailogConnection,getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getDataFromFireBase();
    }

    private void transitionAnimation() {
        Fade fade = new Fade();
        View decorView = getWindow().getDecorView();
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(400));
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(decorView.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(findViewById(R.id.memortViewer_RecyclerView), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

    }

    String convertedMemory;

    private void initData(Intent intent) {
        convertedMemory = intent.getStringExtra(HomeFragement.MEMORY_TO_SHOW);
        MemoryConverter memoryConverter = new MemoryConverter();
        memory = memoryConverter.ConvertToMemory(convertedMemory);
        String tittle = memory.getTittle();
        txtTittle.setText(tittle);
        txtToolBar.setText(memory.getTittle());
        CurrentLocation location = memory.getLocation();
        getLocationSubAdminArea(location);
        String timeInMillis = memory.getTimeInMillis();
        getDate(timeInMillis);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        List<DownloadedImage> pictures = new ArrayList<>();
        List<ImageToUpload> uploadArrayList = new ArrayList<>();
        for (ImageToUpload imageToUpload : memory.getPictures()) {
            uploadArrayList.add(imageToUpload);
            pictures.add(new DownloadedImage(imageToUpload.getUrl()));
        }
        fillImageUrlList(pictures, uploadArrayList);
        //
        String mainPicUrl = memory.getMainPicUrl();
        Picasso.get().load(mainPicUrl)
                .into(circleImageViewMainImage);
        txtToolBar.setText(memory.getTittle());

        viewerAdapter.setImagePreviewClickListener(new ImagePreviewClickListener() {
            @Override
            public void onImageClick(int position, String uri, View view) {

                isCleared = false;
                Intent showImageIntent = new Intent(MemoryViewer.this, ImageShowerFromMemory.class);
                showImageIntent.putExtra(JGSONED_MEMORY, convertedMemory);
                Log.d(TAG, "onImageClick: " + position);
                showImageIntent.putExtra(IMAGE_POSITION, position);
                showImageIntent.putExtra(IMAGE_URI, uri);

                selectedPosition = position;
                startActivity(showImageIntent);

            }

            @Override
            public void onImageLongClick(int position, List<DownloadedImage> list, View view) {
                setActionBarTittle(list.size(), imgsUrl.size());

                isCleared = false;
                if (list.size() > 0) {
                    setMenueVisibiltiy(true);
                } else {
                    setMenueVisibiltiy(false);
                }

            }

            void setActionBarTittle(int seelcted, int tottalSize) {
                if (seelcted > 0) {
                    txtToolBar.setText("selected " + seelcted + "/" + tottalSize);
                } else {
                    txtToolBar.setText(memory.getTittle());
                }
            }

            @Override
            public void onImageFavoriteClick(int position, String url, boolean favorite) {


                if (CheckInternetConnection.connection(getApplicationContext())) {
                    int index = memory.getMemoryID();
                    index = (index == 0) ? 1 : index;
                    if (favorite) {
                        updateFalse(position, url, favorite, index);
                    } else {
                        updateTrue(position, url, favorite, index);
                    }
                    getDataFromFireBase();


                } else {
                    textViewDailogConnection.setAnimation(flashingAnimation);
                    textViewDailogConnection.startAnimation(flashingAnimation);
                }

            }

            @Override
            public void onLoad(View holder, int position, boolean checked) {
                ImageView viewById = (ImageView) holder.findViewById(R.id.imgeView_ImageViewAdapterAddFavorite);
                Log.d(TAG, "onLoad: " + checked + " " + position);
                if (checked) {
                    viewById.setImageDrawable(getResources().getDrawable(R.drawable.favorite_red));
                } else {
                    viewById.setImageDrawable(getResources().getDrawable(R.drawable.favoritr_white));
                }

            }
        });


    }

    private void updateFalse(int position, String url, boolean favorite, int index) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("UserMemory").child(firebaseUser.getUid())
                .child("memory_" + index)
                .child("pictures")
                .child(String.valueOf(position))
                .child("imageFavorite")
                .setValue(false);

    }

    private void updateTrue(int position, String url, boolean favorite, int index) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("UserMemory").child(firebaseUser.getUid())
                .child("memory_" + index)
                .child("pictures")
                .child(String.valueOf(position))
                .child("imageFavorite")
                .setValue(true);


    }

    private void fillImageUrlList(List<DownloadedImage> pictures, List<ImageToUpload> uploadList) {
        imgsUrl.addAll(pictures);
        viewerAdapter.setImageToUploads(uploadList);
        viewerAdapter.setNotifyImagesAlso(true);
        viewerAdapter.notifyDataSetChanged();
    }

    private void getDate(String timeInMillis) {
        long time = Long.parseLong(timeInMillis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = day + "/" + month + "/" + year;
        txtDate.setText(date);

    }

    private void getLocationSubAdminArea(CurrentLocation location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (latitude != 0 && longitude != 0) {
            try {
                List<Address> fromLocation = new Geocoder(getApplicationContext()).getFromLocation(latitude, longitude, 1);
                if (fromLocation.size() > 0) {
                    String subAdminArea = fromLocation.get(0).getSubAdminArea();
                    txtLocation.setText(subAdminArea);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void init() {
        txtToolBar =findViewById(R.id.memorrViewer_txtMemoryName);
        circleImageViewMainImage = findViewById(R.id.memortViewer_imgMain);
        txtDate = findViewById(R.id.memortViewer_memoryDate);
        txtTittle = findViewById(R.id.memortViewer_memoryTittle);
        txtLocation = findViewById(R.id.memortViewer_memoryAddress);
        recyclerViewAllImages = findViewById(R.id.memortViewer_RecyclerView);
        btnDownLoad =findViewById(R.id.memoryViewer_btn_Download);
        btnShare =findViewById(R.id.memoryViewer_btn_Share);
        btnBack =findViewById(R.id.memoryViewer_btn_cancel);
        viewerAdapter = new MemoryViewerAdapter(imgsUrl, getApplicationContext());
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerViewAllImages.setAdapter(viewerAdapter);
        recyclerViewAllImages.setHasFixedSize(true);
        recyclerViewAllImages.setItemViewCacheSize(20);
        recyclerViewAllImages.setLayoutManager(gridLayoutManager);

        circleImageViewMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showImageIntent = new Intent(MemoryViewer.this, ShowMainImageFromMemoryViewer.class);
                showImageIntent.putExtra(JGSONED_MEMORY, new MemoryConverter().convertToGson(memory));
                startActivity(showImageIntent);


            }
        });
        btnDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckInternetConnection.connection(getApplicationContext())) {
//                    InitializeConnectionDialog.flashTextView();
                    textViewDailogConnection.setAnimation(flashingAnimation);
                    textViewDailogConnection.startAnimation(flashingAnimation);

                } else {
                    List<DownloadedImage> selectedImages = viewerAdapter.getSelectedImages();
                    if (selectedImages.size() == 0) {
                        Toast.makeText(MemoryViewer.this, "choose images first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        permissionRequest();
                    } else {
                        startDownLoad();
                    }
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionRequest();
                } else {
                    startShare();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
//        toolbar.inflateMenu(R.menu.down_load_images_menu);
//        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
//        toolbar.setNavigationOnClickListener(i -> onBackPressed());
//
//        setSupportActionBar(toolbar);


    }

    boolean isCleared = false;

    @Override
    public void onBackPressed() {

        if (viewerAdapter.getSelectedImages().size() > 0) {
            if (isCleared) {
                super.onBackPressed();
            } else {
                viewerAdapter.getSelectedImages().clear();
                for (int i = 0; i < imgsUrl.size(); i++) {
                    DownloadedImage downloadedImage = imgsUrl.get(i);
                    downloadedImage.setChecked(false);
                    viewerAdapter.setNotifyImagesAlso(false);

                    viewerAdapter.notifyDataSetChanged();
                    isCleared = true;
                    setMenueVisibiltiy(false);
                    setActionBarBlankTittle();

                }


            }

        } else {
            super.onBackPressed();
        }
    }

    private void setActionBarBlankTittle() {


        txtToolBar.setText(memory.getTittle());

    }


    private void startDownLoad() {
        List<String> urlList = new ArrayList<>();
        for (DownloadedImage image : viewerAdapter.getSelectedImages()) {
            urlList.add(image.getImgUrl());
        }

        if (ImagesListDownLoader.isDownloading) {
            Toast.makeText(getApplicationContext(), "please wait until current download is finished", Toast.LENGTH_SHORT).show();
        } else {
            startDownLoadService(urlList, memory.getTittle());


        }
    }

    private void startShare() {
        PicassoBitmab.deleteAllOldFiles(memory.getTittle());

        List<DownloadedImage> selectedImages = viewerAdapter.getSelectedImages();
        if (selectedImages.size() == 0) {
            Toast.makeText(MemoryViewer.this, "choose images first", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> urlList = new ArrayList<>();
        for (DownloadedImage image : viewerAdapter.getSelectedImages()) {
            urlList.add(image.getImgUrl());
        }
        PicassoBitmab.gettingUri(MemoryViewer.this, memory.getTittle(), this, urlList);


    }

    private void startDownLoadService(List<String> urlList, String memoryName) {
        ImagesListDownLoader.downLoadInfo = this;
        for (int i = 0; i < urlList.size(); i++) {
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

    private void permissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQ);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_STORAGE_REQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownLoad();
            } else {
                Toast.makeText(this, "accept permission to downLoad", Toast.LENGTH_SHORT).show();
            }
        }
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


}