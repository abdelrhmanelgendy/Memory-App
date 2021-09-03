package com.example.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.adapters.AddNewMemoryAdapter;
import com.example.myapplication.databases.local.MemoryDatabase;
import com.example.myapplication.fragments.HomeFragement;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.FilesExtentions;
import com.example.myapplication.helpers.FireBaseConstants;
import com.example.myapplication.helpers.GPSOpen;
import com.example.myapplication.dialogs.InitializeConnectionDialog;
import com.example.myapplication.jsonConverter.GSONConverter;
import com.example.myapplication.jsonConverter.ImageFromStorageGSONConverter;
import com.example.myapplication.dialogs.ProgressDialog;
import com.example.myapplication.helpers.Resizer;
import com.example.myapplication.helpers.SoundsPlayer;
import com.example.myapplication.dialogs.TwoButtonDailog;
import com.example.myapplication.notifications.UploadNotification;
import com.example.myapplication.pojo.CurrentLocation;
import com.example.myapplication.pojo.Image;
import com.example.myapplication.pojo.ImageFromStorag;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.util.OnPictureClickListener;
import com.example.myapplication.util.TwoButtonDialogOnClickListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddNewMemory extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final int IMAGE_PICK_UP_REQ_CODE = 51;
    private static final int LOCATION_PERMISSION_REQCODE = 70;
    public static final String LOGIN_ACTIVITY_LISTED_IMAGE = "listOfImage";
    public static final String LOGIN_ACTIVITY_LISTED_IMAGE_ACTION = "listOfImageAction";
    private static final String TAG2 = "mostafa123";

    GPSOpen gpsOpen;

    ImageView imageFindLocation;
    Location userCurrentLocation;
    Animation shakeAnimation, flashingAnimation;

    private static final String LIST_OF_IMAGES_SHARED = "imagelistNameInPref";
    private static final String NAME_OF_LIST_OF_IMAGES_SHARED = "listofImages";
    private static final String DEF_SHARED_IMAGE = "none";
    private static final int IMAGE_PICK_UP_SINGLE_REQ_CODE = 52;
    private static final int PERMISSION_REQ_CODE = 19;
    public static final String IMAGE_ID_EXTRA = "image_id";
    public static final String IMAGE_URI_EXTRA = "image_uri";
    private static final int EDITE_IMAGE_REQ_CODE = 10;
    public static final String MEMORY_TITLE = "imagetittle";
    public static final int DEFAULT_IMAGE_ID = 1215;
    public static final String DEFAULT_IMAGE_NAME = "default";
    UploadNotification notification;
    //Location location
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    AddNewMemoryAdapter addNewMemoryAdapter;

    private static final String TAG = "AddNewMemory";
    List<Image> list = new ArrayList<>();
    List<Image> resizedList = new ArrayList<>();
    List<Image> copiedList = new ArrayList<>();
    List<Image> tempList = new ArrayList<>();
    CircleImageView imageViewMain, imageChooseImage;
    private int position = -1;
    public static int IMAGE_PICKED = -1;
    TextView txtLocationAddress;

    Uri mainImageUri = null;
    EditText txtTitle, txtDescription;
    SharedPreferences sharedPreferences;

    Button btnSave, btnCancel;
    boolean isUserCanceld = false;

    List<ImageToUpload> uploadedImagesNamesListUrl = new ArrayList<>();
    List<Integer> oldMemoryNamesList = new ArrayList<>();

    StorageReference storageReference;
    UploadTask uploadTask;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    CountDownTimer countDownTimer;
    boolean fromAdding = false;
    private int CustomeGalley_RESULT = 101;
    Image imgToDeleteAFAnimation;
    TextView txtViewConnectionDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_memory);

        intit(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        restoreListFromShredPref();
        View decorView = getWindow().getDecorView();
        Fade fade = new Fade();
        fade.excludeTarget(decorView.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getImagesFromOutsideApp();


    }

    private void getImagesFromOutsideApp() {
        Intent intent = getIntent();

        if (intent != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_SEND:


                    Uri imageuri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    Image image = new Image(list.size(), imageuri.toString());
                    tempList.add(image);
                    addNewMemoryAdapter.notifyDataSetChanged();
                    Log.d(TAG, "getImagesFromOutsideApp: " + imageuri);
                    if (firebaseAuth.getCurrentUser() == null) {

                        userNotLogingStartLoginActivity(list);

                        return;
                    }
                    resizeImageInList();

                    break;
                case Intent.ACTION_SEND_MULTIPLE:

                    ArrayList<Uri> uriArrayList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    for (int i = 0; i < uriArrayList.size(); i++) {
                        tempList.add(new Image(list.size(), uriArrayList.get(i).toString()));
                        addNewMemoryAdapter.notifyDataSetChanged();
                    }
                    if (firebaseAuth.getCurrentUser() == null) {
                        userNotLogingStartLoginActivity(list);

                        return;
                    }
                    resizeImageInList();
                    break;
                case "galleryAction":
                    String stringExtra = intent.getStringExtra(CustomeGalley.LIST_OF_SELECTED_IMAGES);
                    showMultipleImagesFromCustomGalley(stringExtra);
                    break;

//                case LOGIN_ACTIVITY_LISTED_IMAGE_ACTION:
//                    String stringExtra1 = intent.getStringExtra(AddNewMemory.LOGIN_ACTIVITY_LISTED_IMAGE);
//                    Log.d(TAG, "getImagesFromOutsideApp: " + stringExtra1);
//                    Log.d("TAG243", "OpenUi: " + stringExtra1);
//                    GSONConverter gsonConverter = new GSONConverter();
//                    List<Image> returnedList = gsonConverter.fromImagesToString(stringExtra1);
//                    list.clear();
//                    list.addAll(returnedList);
//                    resizeImageInList();
//                    addNewMemoryAdapter.notifyDataSetChanged();

            }
        }
    }

    private void userNotLogingStartLoginActivity(List<Image> list) {
        Intent loginActivityIntent = new Intent(AddNewMemory.this, LoginActivity.class);
        startActivity(loginActivityIntent);
    }

    private void showMultipleImagesFromCustomGalley(String stringExtra) {
        ImageFromStorageGSONConverter imageFromStorageGSONConverter = new ImageFromStorageGSONConverter(new Gson());
        ArrayList<ImageFromStorag> imageFromStorags = imageFromStorageGSONConverter.convertToImage(stringExtra);
        for (int i = 0; i < imageFromStorags.size(); i++) {
            ImageFromStorag imageFromStorag = imageFromStorags.get(i);
            Image image = imageFromStorag.getImage();
            list.add(image);
            Log.d(TAG, "showMultipleImagesFromCustomGalley: " + image.getUri());
        }
        addNewMemoryAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeConnectionDialog.changeConnetion(txtViewConnectionDialog, getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        InitializeConnectionDialog.endCheck();
    }

    private void intit(Context applicationContext) {
        flashingAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.teext_view_flashing);
        shakeAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.animation_shake_disapear);
        shakeAnimation.setAnimationListener(this);
        gpsOpen = new GPSOpen(this);
        Intent intent = new Intent(this, AddNewMemory.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 21, intent, PendingIntent.FLAG_ONE_SHOT);
        notification = new UploadNotification(getApplicationContext(), pendingIntent);
        btnSave = findViewById(R.id.add_new_btnAdd);
        btnCancel = findViewById(R.id.add_new_btnCancel);

        recyclerView = findViewById(R.id.activityAddNewMemory_Rec);
        gridLayoutManager = new GridLayoutManager(this, 3);
        addNewMemoryAdapter = new AddNewMemoryAdapter(applicationContext);
        recyclerView.setAdapter(addNewMemoryAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageViewMain = findViewById(R.id.activityAddNewMemory_IMG_main);
        imageChooseImage = findViewById(R.id.activityAddNewMemory_TV_choose);
        txtTitle = findViewById(R.id.activityAddNewMemory_txtTittle);
        txtLocationAddress = findViewById(R.id.activityAddNewMemory_txtLocation);
        imageFindLocation = findViewById(R.id.activityAddNewMemory_btnAddLocation);
        txtViewConnectionDialog = findViewById(R.id.activityAddNewMemory_txtViewConnection);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imageFindLocation.setOnClickListener(this);

        txtDescription = findViewById(R.id.activityAddNewMemory_txtDesc);
        list.add(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));

        addNewMemoryAdapter.setImgUri(list);
        sharedPreferences = getSharedPreferences(LIST_OF_IMAGES_SHARED, MODE_PRIVATE);
        addNewMemoryAdapter.setListener(new OnPictureClickListener() {
            @Override
            public void onClick(Image image) {
                Log.d("TAG200", "onClick: " + image.getUri());

                if (image.getId() == DEFAULT_IMAGE_ID) {
                    openMultiplePickUpActivity();

                } else {
                    File file = new File(image.getUri());
                    try {
                        AssetFileDescriptor fileDescriptor = getContentResolver().openAssetFileDescriptor(Uri.parse(image.getUri()), "r");
                        Log.d("TAG200", "onClick: " + (fileDescriptor.getLength() / 1024));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("TAG200", "onClick: " + e.getMessage());
                    }

                    long length = file.length();

                    ViewImage(image);
                }
            }

            @Override
            public void onLongClick(Image image, View img) {
                if (image.getId() != DEFAULT_IMAGE_ID) {

                    img.startAnimation(shakeAnimation);
                    imgToDeleteAFAnimation = image;

                }
            }

            @Override
            public void onEditeClick(Image image) {

                String uri = image.getUri();
                if (!uri.equals("default")) {
                    editeImage(image);
                    position = list.indexOf(image);

                }

            }
        });

        imageChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                openSinglePickUpActivity();

            }
        });
        imageViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainImageUri != null) {

                    openDialog();

                }
            }
        });

    }

    private void openMyCustomGallery() {
        Intent galleryIntent = new Intent(AddNewMemory.this, CustomeGalley.class);
        startActivityForResult(galleryIntent, CustomeGalley_RESULT);


    }

    private void openDialog() {
        TwoButtonDailog twoButtonDailog = new TwoButtonDailog();
        twoButtonDailog.ShowDialog(this, "");
        twoButtonDailog.setListener(new TwoButtonDialogOnClickListener() {
            @Override
            public void firstDialogOnClick() {
                Log.d(TAG, "firstDialogOnClick: view");
                if (mainImageUri != null)
                    previewImage(mainImageUri);

            }

            @Override
            public void secondDialogOnClick() {
                Log.d(TAG, "secondDialogOnClick: replace");
                openSinglePickUpActivity();

            }
        });
    }


    private void chechForPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewMemory.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == IMAGE_PICK_UP_REQ_CODE && resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int itemCount = data.getClipData().getItemCount();
                    for (int i = 0; i < itemCount; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();

                        Image image1 = new Image(i, uri.toString());
                        tempList.add(image1);


                    }

                    resizeImageInList();


                    addNewMemoryAdapter.notifyDataSetChanged();


                } else {
                    try {
                        addSingleImage(data.getData());
                    } catch (Exception e) {
                        return;
                    }
                }

            } else if (requestCode == EDITE_IMAGE_REQ_CODE && data != null && resultCode == RESULT_OK) {
                replaceImage(data.getData());
            } else if (requestCode == IMAGE_PICK_UP_SINGLE_REQ_CODE && data != null && resultCode == RESULT_OK) {
                Resizer.get(getApplicationContext());
                String s = Resizer.reduceBitmab(data.getData());
                imageViewMain.setImageURI(Uri.fromFile(new File(s)));
                IMAGE_PICKED = 1;
                mainImageUri = Uri.fromFile(new File(s));
            }

        } catch (Exception e) {
            return;
        }

    }

    boolean addForFirstTime = true;

    private void resizeImageInList() {

//Resizing These steps can be deleted but you need to add the extension to the file later
        int size = tempList.size();
        if (size > 15) {
            Toast.makeText(this, getResources().getString(R.string.memorycannotcontain15), Toast.LENGTH_LONG).show();
            tempList.clear();
            return;
        }
        if (getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ProgressDialog progressDialog = new ProgressDialog(AddNewMemory.this, true);
                    progressDialog.show(getResources().getString(R.string.processimages));
                    if (addForFirstTime) {
//                        resizedList.add(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));
                        addForFirstTime = false;
                    }
                    for (Image image : tempList) {

//
                        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> emitter) throws Exception {
//                                Thread resizeThread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
                                if (image.getId() != DEFAULT_IMAGE_ID) {
                                    Resizer.get(getApplicationContext());
                                    String outPutPath = Resizer.reduceBitmab(Uri.parse(image.getUri()));

                                    resizedList.add(new Image(image.getId(), Uri.fromFile(new File(outPutPath)).toString()));

                                }


                                if (resizedList.size() == tempList.size()) {
                                    Log.d("TAG200", "resizeImageInList: ");
                                    tempList.clear();
                                    list.addAll(resizedList);
                                    resizedList.clear();
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            addNewMemoryAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
//                            }
//                        });
//                        resizeThread.start();
                            }
                        });
                        observable.observeOn(AndroidSchedulers.mainThread());
                        observable.subscribeOn(Schedulers.io());
                        observable.subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull String s) {

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                    }

                }
            }, 2000);
        } else {
//            resizedList.clear();
            ProgressDialog progressDialog = new ProgressDialog(AddNewMemory.this, true);
            progressDialog.show(getResources().getString(R.string.processimages));
            if (addForFirstTime) {
//                resizedList.add(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));
                addForFirstTime = false;
            }

            for (Image image : tempList) {
                Thread resizeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (image.getId() != DEFAULT_IMAGE_ID) {
                            Resizer.get(getApplicationContext());
                            String outPutPath = Resizer.reduceBitmab(Uri.parse(image.getUri()));

                            resizedList.add(new Image(image.getId(), Uri.fromFile(new File(outPutPath)).toString()));
                            Log.d(TAG, "resizeImageInList123: " + resizedList.size());

                        }


                        if (resizedList.size() == tempList.size()) {
                            Log.d("TAG200", "resizeImageInList: ");
                            tempList.clear();
                            list.addAll(resizedList);
                            list.remove(0);

                            Log.d(TAG, "listed: " + list.toString());
                            resizedList.clear();
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    addNewMemoryAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
                resizeThread.start();

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, getResources().getString(R.string.acceptDeafaultPermission), Toast.LENGTH_LONG).show();
                }
                break;
            case LOCATION_PERMISSION_REQCODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startGettingLocation();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.PleaseAcceptLocationPermision), Toast.LENGTH_LONG).show();
                }
                break;
        }

    }


    void editeImage(Image image) {
//
//        Intent intent = new Intent(AddNewMemory.this, DsPhotoEditorActivity.class);
//
//
//        intent.setData(Uri.parse(image.getUri()));
//        intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "images-" + getResources().getString(R.string.app_name));
//
//        intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#101D25"));
//        intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#101D25"));
//        intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, new int[]{DsPhotoEditorActivity.TOOL_WARMTH, DsPhotoEditorActivity.TOOL_PIXELATE});
//        startActivityForResult(intent, EDITE_IMAGE_REQ_CODE);

    }

    void replaceImage(Uri uri) {
        String newUri = "";
        AssetFileDescriptor assetFileDescriptor = null;
        try {
            assetFileDescriptor = getContentResolver().openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (assetFileDescriptor != null) {
            Resizer.get(getApplicationContext());
            long length = (assetFileDescriptor.getLength() / 1024);
            if (length < 800) {
                newUri = uri.toString();
            }
            if (length >= 800 && length <= 1500) {


                String s = Resizer.reduceBitmabBySpecificSize(uri, 25);
                newUri = Uri.fromFile(new File(s)).toString();
            } else if (length > 1500 && length <= 3000) {
                String s = Resizer.reduceBitmabBySpecificSize(uri, 20);
                newUri = Uri.fromFile(new File(s)).toString();
            }
            if (length > 3000 && length <= 5000) {
                String s = Resizer.reduceBitmabBySpecificSize(uri, 15);
                newUri = Uri.fromFile(new File(s)).toString();
            } else if (length > 5000) {

                String s = Resizer.reduceBitmabBySpecificSize(uri, 10);
                newUri = Uri.fromFile(new File(s)).toString();
            }
        } else {
            newUri = uri.toString();
        }

        if (position != -1) {
            list.remove(position);
            Image image = new Image(position, newUri);
            list.add(position, image);
            addNewMemoryAdapter.notifyDataSetChanged();
        }
    }

    void addSingleImage(Uri uri) {
        Uri data1 = uri;

        Image image1 = new Image(list.size(), data1.toString());
        tempList.add(image1);
        addNewMemoryAdapter.notifyDataSetChanged();
        resizeImageInList();
    }

    private void ViewImage(Image image) {

        previewImage(Uri.parse(image.getUri()));

    }

    private void previewImage(Uri uri) {
        Intent imagePreview = new Intent(AddNewMemory.this, PreviewImage.class);

        imagePreview.putExtra(IMAGE_URI_EXTRA, uri.toString());
        imagePreview.putExtra(MEMORY_TITLE, txtTitle.getText().toString());

        startActivity(imagePreview);
    }

    private void openMultiplePickUpActivity() {

        chechForPermission();
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(i, "select multi Images"), IMAGE_PICK_UP_REQ_CODE);
        copiedList.addAll(list);
    }

    private void openSinglePickUpActivity() {

        chechForPermission();
        Intent i = new Intent();
        i.setType("image/*");

        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(i, "select multi Images"), IMAGE_PICK_UP_SINGLE_REQ_CODE);

    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreListFromShredPref();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_btnAdd:
                Log.d(TAG, "onClick: ");
                fromAdding = true;
                try {


                    addMemortTOFireBase();

                } catch (Exception e) {
                    return;
                }
                break;
            case R.id.add_new_btnCancel:
                try {


                    deleteResizedImages();
                    deleteMainImageFromFiles();
                    Log.d(TAG, "onClick: cancel ");
                    UserCanceld();

                } catch (Exception e) {
                    return;
                }
                break;

            case R.id.activityAddNewMemory_btnAddLocation:
                startGettingLocation();
                break;

        }
    }

    private void listAllFiles() {
        File file = new File(this.getFilesDir() + "/" + "ResizedImages");
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String path = files[i].getPath();
                    String path2 = files[i].getAbsolutePath();
                    Log.d("TAGFile", "listAllFiles: " + path);
                    boolean delete = files[i].delete();
                    Log.d("TAGFile", "listAllFiles: " + delete);

                }
            }
        }

    }

    private void startGettingLocation() {
        try {


            gpsOpen.turnGPSOn(new GPSOpen.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    if (isGPSEnable) {
                        getLocationIntoAddress();
                        ProgressDialog dialog = new ProgressDialog(AddNewMemory.this, true);
                        dialog.show(getResources().getString(R.string.gettingyourlocation));

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                dialog.dismiss();
                            }
                        }, 1000);


//                    getLocationIntoAddress();


                    } else {
                        Toast.makeText(AddNewMemory.this, getResources().getString(R.string.enableyourGPS), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            return;
        }

    }

    private void getLocationIntoAddress() {
        try {


            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                chechForLocationPermissions();
            }
            Task<Location> lastLocation = fusedLocationProviderClient.getLastLocation();
            lastLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {

                        try {
                            Location result = task.getResult();
                            Log.d(TAG, "onComplete: " + result);
                            if (result != null) {
                                userCurrentLocation = result;
                                double latitude = result.getLatitude();
                                double longitude = result.getLongitude();
                                Geocoder geocoder = new Geocoder(getApplicationContext());

                                List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 1);
                                if (fromLocation.size() > 0) {
                                    Log.d(TAG, "onComplete: " + fromLocation.toString());
                                    Address address = fromLocation.get(0);
                                    String mohafza = address.getAdminArea();
                                    String area = address.getLocality();
                                    String markz = address.getSubAdminArea();
                                    String country = address.getCountryName();
                                    String specificLocation = country + "," + mohafza + "," + markz + "," + area;
                                    txtLocationAddress.setText(specificLocation);
                                    if (fromAdding) {
                                        uploadImages();
                                    }

                                }
                            } else {
                                Toast.makeText(AddNewMemory.this, getResources().getString(R.string.locationGettingError), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(AddNewMemory.this, getResources().getString(R.string.locationGettingError), Toast.LENGTH_LONG).show();
                        }

                    }

                }
            });


        } catch (Exception e) {
            return;
        }
    }

    private void chechForLocationPermissions() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQCODE);
    }

    private void addMemortTOFireBase() {
        if (CheckInternetConnection.connection(getApplicationContext())) {


            if (txtTitle.getText().toString().length() == 0) {
                Toast.makeText(this, getResources().getString(R.string.EnterTittle), Toast.LENGTH_LONG).show();
                return;
            }
            if (txtTitle.getText().toString().length() < 6) {
                Toast.makeText(this, getResources().getString(R.string.Tittleshouldnbelessthan6), Toast.LENGTH_LONG).show();
                return;
            }
            if (list.size() < 1) {
                Toast.makeText(this, getResources().getString(R.string.choosesomeImages), Toast.LENGTH_LONG).show();
                return;
            }
            if (mainImageUri == null) {
                Toast.makeText(this, getResources().getString(R.string.choosemainphoto), Toast.LENGTH_LONG).show();
                return;
            }
            if (userCurrentLocation == null) {


                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.Location))
                        .setMessage(getResources().getString(R.string.provideLocation))
                        .setIcon(R.drawable.ic_baseline_fmd_good_24)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGettingLocation();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadImages();
                            }
                        }).show();


            } else if (userCurrentLocation != null) {
                uploadImages();
            }
        } else {
            txtViewConnectionDialog.setAnimation(flashingAnimation);
            txtViewConnectionDialog.startAnimation(flashingAnimation);
        }

    }

    private synchronized void uploadImages() {

        checkImagesSizes();


    }

    private void checkImagesSizes() {
//        for (int i = 0; i < list.size(); i++) {
//            Log.d("TAG200", "checkImagesSizes: " + list.get(i).getUri());
//            File file = new File(list.get(i).getUri());
//            Image image = list.get(i);
//            try {
//                AssetFileDescriptor fileDescriptor = getContentResolver().openAssetFileDescriptor(Uri.fromFile(file), "r");
//                long lengthInKB = (fileDescriptor.getLength() / 1024);
//                if (lengthInKB >= 800 && lengthInKB < 1000) {
//                    Resizer.get(getApplicationContext());
//                    String s = Resizer.reduceBitmabBySpecificSize(Uri.fromFile(file),30);
//                    list.remove(image);
//                    list.add(i,new Image(image.getId(),s));
//                    Log.d("TAG", "checkImagesSizes: "+image.getUri());
//                }
//                if (lengthInKB >= 1000 && lengthInKB <= 2000) {
//                    Resizer.get(getApplicationContext());
//                    String s = Resizer.reduceBitmabBySpecificSize(Uri.parse(image.getUri()),10);
//                    list.remove(image);
//                    list.add(i,new Image(image.getId(),s));
//                    Log.d("TAG200", "checkImagesSizes123: "+image.getUri());
//                }
//                if (lengthInKB > 2000 && lengthInKB <= 5000) {
//                    Resizer.get(getApplicationContext());
//                    String s = Resizer.reduceBitmabBySpecificSize(Uri.fromFile(file),10);
//                    list.remove(image);
//                    list.add(i,new Image(image.getId(),s));
//                }
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }


//            if (i == list.size() - 1) {
        getUserLastMemories();
//                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
//            }
//        }


    }


    private void getUserLastMemories() {
        oldMemoryNamesList.clear();


        databaseReference = FirebaseDatabase.getInstance().getReference("UserMemory").child(FireBaseConstants.getfirebaseUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot != null) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String key = snapshot1.getKey();
                            Log.d(TAG, "onDataChange: " + key);
                            int keynum = getKeyASInt(key);
                            oldMemoryNamesList.add(keynum);

                        }
                    }

                }
                if (oldMemoryNamesList.size() > 0) {
                    Integer max = Collections.max(oldMemoryNamesList);
                    createMemoryWith((max + 1));


                    Log.d(TAG, "onDataChange: old user " + oldMemoryNamesList.size());
                } else {
                    createMemoryForTheFirstTime();

                    Log.d(TAG, "onDataChange: new user " + oldMemoryNamesList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });


    }

    private void createMemoryForTheFirstTime() {
        Log.d(TAG, "createMemoryForTheFirstTime: -----1'");
        Thread thread = new Thread(new Runnable() {
            @Override

            public void run() {
                uploadMemories(1);
                uploadMainImage(1);
            }
        });
        if (uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(this, getResources().getString(R.string.Uploadingprogress), Toast.LENGTH_LONG).show();
            Configuration configuration = getResources().getConfiguration();


        } else {
            thread.start();
        }


    }

    private void uploadMainImage(int i) {
        Uri uri = mainImageUri;

        storageReference = FirebaseStorage.getInstance().getReference(FireBaseConstants.getfirebaseUser().getUid()).child("memory_" + i).child("mainImage" + "." + "PNG");
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("TAG53", "onSuccess: " + uri);
                        FirebaseDatabase.getInstance().getReference("UserMemory").child(FireBaseConstants.getfirebaseUser().getUid())
                                .child("memory_" + i).child("mainPicUrl")
                                .setValue(uri.toString());
                        Log.d("TAG53", "onSuccess2: " + uri);
                        deleteMainImageFromFiles();
                        listAllFiles();
//                            mainImageUri = null;
                    }
                });
            }
        });


    }

    private void deleteMainImageFromFiles() {
        if (mainImageUri != null) {
            File file = new File(mainImageUri.getPath());
            boolean delete = file.delete();
            Log.d(TAG, "deleteMainImageFromFiles: " + delete);
        }
    }

    int x = 0;
    int currentUploading = 0;

    private void uploadMemories(int i) {
        String memoryTittle = txtTitle.getText().toString();
        uploadedImagesNamesListUrl.clear();
        for (Image image : list) {

            if (image.getId() != DEFAULT_IMAGE_ID) {

                String extension = FilesExtentions.getExtension(Uri.parse(image.getUri()), this);
                storageReference = FirebaseStorage.getInstance().getReference(FireBaseConstants.getfirebaseUser().getUid()).child("memory_" + i).child(image.getId() + "." + "PNG");
                File file = new File(image.getUri());
                Uri uri = Uri.fromFile(file);
                Log.d(TAG2, uri.toString());

                uploadTask = storageReference.putFile(Uri.parse(image.getUri()));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        x++;

                        String c = FireBaseConstants.getfirebaseUser().getUid() + "/" + "memory_" + i + "/" + image.getId() + "." + "PNG";
                        Log.d("TAG51", "onSuccess: " + c);
                        Task<Uri> uriTask = FirebaseStorage.getInstance().getReference(FireBaseConstants.getfirebaseUser().getUid() + "/" + "memory_" + i + "/" + image.getId() + "." + "PNG")
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Log.d(TAG, "onSuccess: " + i);
                                        uploadedImagesNamesListUrl.add(new ImageToUpload(uri.toString(), String.valueOf(currentUploading), false, String.valueOf(i), memoryTittle));
                                        int uploadedSize = uploadedImagesNamesListUrl.size();
                                        int originalSize = list.size();
                                        Log.d("TAG51", "onSuccess: uploadedSize " + uploadedSize + " originalSize" + originalSize);
                                        if (uploadedSize == originalSize) {
                                            Log.d("TAG52", "onSuccess: " + i);
                                            updateUser(i, uploadedImagesNamesListUrl);
//                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
                                            uploadMainImage(i);
//                                        }
//                                    }, 2000);
                                        }
                                        currentUploading++;

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure1: " + e.getMessage());
                                    }
                                });


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        Log.d(TAG, "onProgress: " + image.getId() + "  size" + (list.size() - 1));
                        Bitmap bitMap = getBitMap(Uri.parse(image.getUri()));
                        double progress = (100 * (snapshot.getBytesTransferred()) / (snapshot.getTotalByteCount()));
                        notification.notification(x + "/" + (list.size() - 1));
                        notification.setPreogress(100, (int) progress, x + "/" + (list.size()), bitMap, list.size());


                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        if (x == list.size() - 1) {
                            x = 0;
                            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.defult);


                            notification.notification(x + "/" + list.size());
                            notification.setPreogress(0, 0, "", bitmap2, list.size());


                            Log.d("TAG52", "onComplete: " + uploadedImagesNamesListUrl.size());
                            uploadMainImage(i);


                        }


                    }

                });

            }

        }


    }


    private void updateUser(int index, List<ImageToUpload> uploadedImagesNamesListUrl) {
        Log.d("TAG51", "onSuccess: " + index);
        Log.d(TAG, "updateUser: qewfqwfqwf" + index);

        String title = txtTitle.getText().toString();
        String description = txtDescription.getText().toString();
        int count = list.size();
        CurrentLocation location = new CurrentLocation(0, 0);
        if (userCurrentLocation != null) {
            location = new CurrentLocation(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
        }

        String mainPicUrl = "hihi";
        String timeCreated = System.currentTimeMillis() + "";


        databaseReference = FirebaseDatabase.getInstance().getReference("UserMemory").child(FireBaseConstants.getfirebaseUser().getUid())
                .child("memory_" + index);


        Memory memory = new Memory(title, description, location, uploadedImagesNamesListUrl, mainPicUrl, count, timeCreated, index);
        databaseReference.setValue(memory).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putBoolean(HomeFragement.USER_FIRST_TIME_ADD_MEMORY, false);
                edit.apply();
//                insertMemoryIntoDataBse(memory);
                Toast.makeText(AddNewMemory.this, getResources().getString(R.string.MemoryCreatedSuccessfuly), Toast.LENGTH_LONG).show();
                notification.setPreogress(0, 0, "", null, list.size());
                userFinishUploading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG200", "onFailure: " + e.getMessage());
            }
        });


    }

    private void insertMemoryIntoDataBse(Memory memory) {
        MemoryDatabase instance = MemoryDatabase.getInstance(getApplicationContext());
        instance.memoryDao()
                .insertOneMemory(memory)
                .observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.d("TAGERROR", "onError: " + e.getMessage());
            }
        });

    }

    private void userFinishUploading() {
        ProgressDialog progressDialog = new ProgressDialog(this, true);
        progressDialog.show(getResources().getString(R.string.inProgress));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fromAdding = false;
                list.clear();
                deleteResizedImages();
                uploadedImagesNamesListUrl.clear();
                oldMemoryNamesList.clear();
                deleteFromPref();
                list.add(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));
                addNewMemoryAdapter.notifyDataSetChanged();
                txtTitle.getText().clear();
                txtDescription.getText().clear();
                txtLocationAddress.setText("");
                imageViewMain.setImageResource(R.drawable.def);
                clearCash();

                progressDialog.dismiss();


            }
        }, 1500);


    }

    private void clearCash() {
        try {
            File file = new File(getApplicationContext().getFilesDir() + "/" + "ResizedImages");
            boolean b = file.delete();
            Log.d(TAG, "deleteCache: " + file.isDirectory());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Bitmap getBitMap(Uri parse) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), parse);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    private void createMemoryWith(int i) {
        Log.d(TAG, "createMemoryWith: " + i);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                uploadMemories(i);
                uploadMainImage(i);

            }
        });
        if (uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(this, getResources().getString(R.string.Uploadingprogress), Toast.LENGTH_LONG).show();

        } else {
            thread.start();
        }
    }

    private void UserCanceld() {
        isUserCanceld = true;
        deleteFromPref();
        listAllFiles();
        finish();
    }

    private int getKeyASInt(String key) {
        String memory = key.replace("memory_", "");

        return Integer.parseInt(memory);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (!isUserCanceld) {
            deleteFromPref();

            saveListToSharedPref();
        }


        Log.d(TAG, "onStop: ");
    }

    void deleteFromPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(LIST_OF_IMAGES_SHARED, MODE_PRIVATE);
        sharedPreferences.edit().remove(NAME_OF_LIST_OF_IMAGES_SHARED).commit();


    }

    void saveListToSharedPref() {
        if (list.size() > 1) {
            List<Image> tempList = new ArrayList<>();
            tempList.addAll(list);
            tempList.remove(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));

            GSONConverter gsonConverter = new GSONConverter();
            String jsonList = gsonConverter.fromImagesToString(tempList);

            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(NAME_OF_LIST_OF_IMAGES_SHARED, jsonList);
            edit.apply();
        }
    }

    void restoreListFromShredPref() {
        Log.d(TAG, "onRestoreInstanceState: ");
        sharedPreferences = getSharedPreferences(LIST_OF_IMAGES_SHARED, MODE_PRIVATE);
        String string = sharedPreferences.getString(NAME_OF_LIST_OF_IMAGES_SHARED, DEF_SHARED_IMAGE);
        if (!string.equals("none")) {
            Log.d(TAG, "restoreListFromShredPref: " + string);
            GSONConverter gsonConverter = new GSONConverter();
            list.clear();
            List<Image> list2 = gsonConverter.fromImagesToString(string);
            list2.remove(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));
            Log.d("TAGCHECK", "restoreListFromShredPref: " + list2.toString());
            list.addAll(list2);

            addNewMemoryAdapter.notifyDataSetChanged();
        }


    }

    void deleteResizedImages() {
        Log.d(TAG, "deleteResizedImages: " + resizedList.toString());
        for (Image image : resizedList) {
            if (image.getId() != DEFAULT_IMAGE_ID) {
                File file = new File(Uri.parse(image.getUri()).getPath());
                boolean delete = file.delete();
                Log.d("TAG201", "deleteResizedImages: " + delete);
                Log.d("TAG201", "deleteResizedImages: " + image.getUri());
            }

        }
    }


    @Override
    public void onAnimationStart(Animation animation) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SoundsPlayer soundsPlayer = new SoundsPlayer(getApplicationContext(), R.raw.item_deleted);
                soundsPlayer.play();
            }
        }, 100);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == shakeAnimation) {
            if (imgToDeleteAFAnimation != null) {

                list.remove(imgToDeleteAFAnimation);
                addNewMemoryAdapter.notifyDataSetChanged();
                deleteFromFiles(imgToDeleteAFAnimation);
                imgToDeleteAFAnimation = null;


            }
            if (list.size() == 0) {
                list.add(new Image(DEFAULT_IMAGE_ID, DEFAULT_IMAGE_NAME));
            }
        }
    }

    private void deleteFromFiles(Image imgToDeleteAFAnimation) {
        File file = new File(Uri.parse(imgToDeleteAFAnimation.getUri()).getPath());
        boolean delete = file.delete();
        Log.d(TAG, "animtest: " + delete);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();

    }
}
