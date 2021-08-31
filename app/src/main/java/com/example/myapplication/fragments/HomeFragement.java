package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.RoomDatabase;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import com.example.myapplication.adapters.HomeFragmentAllMemories;
import com.example.myapplication.databases.local.MemoryDao;
import com.example.myapplication.databases.local.MemoryDatabase;
import com.example.myapplication.databases.local.MemoryViewModel;
import com.example.myapplication.databases.local.OnGetDataFromDatabaseListener;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.FireBaseConstants;
import com.example.myapplication.helpers.LanguageHelper;
import com.example.myapplication.jsonConverter.MemoryConverter;
import com.example.myapplication.jsonConverter.UserConverter;
import com.example.myapplication.pojo.CurrentLocation;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.pojo.User;
import com.example.myapplication.ui.AddNewMemory;
import com.example.myapplication.ui.MemoryViewer;
import com.example.myapplication.util.HomeFragmentMemoryGet;
import com.example.myapplication.util.MemoryClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class HomeFragement extends Fragment implements View.OnClickListener, HomeFragmentMemoryGet {
    public static final String MEMORY_TO_SHOW = "show_memory";
    public static final String HOME_FRAGMENT_USER_DATA_ARGS = "homeFragmentUserKey";
    HomeFragmentAllMemories homeFragmentAllMemoriesAdapter;
    Context context;
    HomeFragmentListener homeFragmentListener;
    MemoryDao memoryDao;
    public static final String USER_FIRST_TIME_ADD_MEMORY = "userAddingForFirstTime";
    public static List<String> userOldFavoriteImage = new ArrayList<>();

    public Context getsContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    RecyclerView recyclerViewAllMemory;
    TextView txtAddNewMemory;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    TextView txtUserName;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayoutMain;
    CardView cardViewMain;
    List<Integer> oldMemoryNamesList = new ArrayList<>();
    ProgressBar progressBar;
    CircleImageView circleImageView1, circleImageView2, circleImageView3, circleImageViewWhite;
    TextView txtImagecRemainsCount, txtOldMemoryTittle, TxtOldMemoryLocation, txtOldMemoryDate;
    Memory lastMemory;
    Button buttonOpenLastMemory;
    private static final String TAG = "HomeFragementDataBase";
    LinearLayout linearLayoutBigContainer;
    HomeFragmentMemoryGet homeFragmentMemortGet;
    MemoryViewModel memoryViewModel;

    public static HomeFragement getInstance(String user) {
        HomeFragement homeFragement = new HomeFragement();
        Bundle bundle = new Bundle();
        bundle.putString(HOME_FRAGMENT_USER_DATA_ARGS, user);
        homeFragement.setArguments(bundle);
        return homeFragement;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View homeFragmentView = inflater.inflate(R.layout.fragment_home_fragement, container, false);
        initMainViews(homeFragmentView);
        init(homeFragmentView, getContext());
        onGetDataVisibility(false);
        setContext(getContext());
        Bundle arguments = getArguments();
        memoryDao = MemoryDatabase.getInstance(getContext()).memoryDao();
        memoryViewModel = ViewModelProviders.of(getActivity()).get(MemoryViewModel.class);


//
//        if (arguments != null && arguments.getString(HOME_FRAGMENT_USER_DATA_ARGS) != null) {
//            String userStringed = arguments.getString(HOME_FRAGMENT_USER_DATA_ARGS);
//            User user = UserConverter.getUser(userStringed);
//            getUserName(user);
//            getUserLastMemories(homeFragmentView);
//            Log.d(TAG, "onCreateView: 1");
//        } else {

        if (CheckInternetConnection.connection(getsContext())) {
            Log.d(TAG, "onCreateView: connection");
            getUserData(homeFragmentView);
        } else {
            Log.d(TAG, "onCreateView: noconnection");
            getDataFromDataBase(homeFragmentView);

        }


        return homeFragmentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean aBoolean = defaultSharedPreferences.getBoolean(USER_FIRST_TIME_ADD_MEMORY, true);
        if (aBoolean) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle(getResources().getString(R.string.addingDialogtxt))
                    .setNeutralButton(getResources().getString(R.string.later), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                        }
                    }).setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openAddMemory();
                }
            }).setCancelable(false)
            ;
            builder.show();

        }
        homeFragmentListener = (HomeFragmentListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initMainViews(View homeFragmentView) {

        recyclerViewAllMemory = homeFragmentView.findViewById(R.id.homeFragemt_Rec_allMemories);
        circleImageView1 = homeFragmentView.findViewById(R.id.homeFragemt_imgPreview_img1);

        circleImageView2 = homeFragmentView.findViewById(R.id.homeFragemt_imgPreview_img2);
        circleImageView3 = homeFragmentView.findViewById(R.id.homeFragemt_imgPreview_img3);
        circleImageViewWhite = homeFragmentView.findViewById(R.id.homeFragemt_imgPreview_imgCount);
        txtImagecRemainsCount = homeFragmentView.findViewById(R.id.homeFragemt_imgPreview_tvCount);
        txtOldMemoryTittle = homeFragmentView.findViewById(R.id.homeFragemt_txtMemoryName);
        TxtOldMemoryLocation = homeFragmentView.findViewById(R.id.homeFragemt_txtLocation);
        txtOldMemoryDate = homeFragmentView.findViewById(R.id.homeFragemt_txtDaysDate);
    }

    private void init(View homeFragmentView, Context context) {
        txtAddNewMemory = homeFragmentView.findViewById(R.id.homeFragemt_TV_AddNew);
        txtAddNewMemory.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        txtUserName = homeFragmentView.findViewById(R.id.homeFragemt_txtName);
        relativeLayoutMain = homeFragmentView.findViewById(R.id.homeFragemt_relative_main);
        cardViewMain = homeFragmentView.findViewById(R.id.homeFragemt_Card_oldMemory);
        swipeRefreshLayout = homeFragmentView.findViewById(R.id.homeFragemt_swipeRefreshLayout);
        buttonOpenLastMemory = homeFragmentView.findViewById(R.id.homeFragemt_TV_arrowGo);
        progressBar = homeFragmentView.findViewById(R.id.homeFragemt_progressBar);
        swipeRefreshLayout.setRefreshing(true);
        homeFragmentMemortGet = new HomeFragement();

        linearLayoutBigContainer = homeFragmentView.findViewById(R.id.homeFragemt_linearLayoutContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getUserData(homeFragmentView);
            }
        });
        buttonOpenLastMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastMemory != null) {
                    Log.d(TAG, "onClick: " + lastMemory.toString());
                    MemoryConverter converter = new MemoryConverter();
                    String s = converter.convertToGson(lastMemory);
                    Intent intent = new Intent(getActivity(), MemoryViewer.class);
                    intent.putExtra(MEMORY_TO_SHOW, s);
                    startActivity(intent);
                }
            }
        });
        relativeLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastMemory != null) {
                    Log.d(TAG, "onClick: " + lastMemory.toString());
                    MemoryConverter converter = new MemoryConverter();
                    String s = converter.convertToGson(lastMemory);
                    Intent intent = new Intent(getActivity(), MemoryViewer.class);
                    intent.putExtra(MEMORY_TO_SHOW, s);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeFragemt_TV_AddNew:
                openAddMemory();
                break;
        }
    }

    private void openAddMemory() {
        Intent i = new Intent(getActivity(), AddNewMemory.class);
        i.setAction("123");
        startActivity(i);
    }

    void getUserName(User user) {
        txtUserName.setText(user.getUserName());
    }

    private void getUserData(View view) {

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("UsersData").child(firebaseAuth.getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot != null) {
                            User currentUserData = snapshot.getValue(User.class);
                            homeFragmentListener.onGetUserData(currentUserData);
                            getUserName(currentUserData);


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            getUserLastMemories(view);
        }

    }


    private void getUserLastMemories(View view) {
        oldMemoryNamesList.clear();
        if (CheckInternetConnection.connection((ConnectivityManager) getsContext().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            databaseReference = FirebaseDatabase.getInstance().getReference("UserMemory").child(FireBaseConstants.getfirebaseUser().getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Memory> userMemoryList = new ArrayList<>();
                    if (snapshot.exists()) {
                        if (snapshot != null) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Memory memory = snapshot1.getValue(Memory.class);
                                String key = snapshot1.getKey();
                                int keynum = getKeyASInt(key);
                                oldMemoryNamesList.add(keynum);
                                userMemoryList.add(memory);
                            }


                        }
                        homeFragmentListener.onGetAllMemories(userMemoryList);
                        updateOfflineCashing(userMemoryList);

                        createMemoryAdapter(userMemoryList, view);


                    }

                    if (oldMemoryNamesList.size() > 0) {
                        Integer max = Collections.max(oldMemoryNamesList);

                        FirebaseDatabase.getInstance().getReference("UserMemory").child(FireBaseConstants.getfirebaseUser().getUid())
                                .child("memory_" + max)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot != null) {
                                                Memory memory = snapshot.getValue(Memory.class);


                                                onGetLastMemory(memory);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    } else {
                        cardViewMain.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onDataChange: new user " + oldMemoryNamesList.size());
                        swipeRefreshLayout.setRefreshing(false);
                        onGetDataVisibility(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });

        } else {
            getDataFromDataBase(view);
        }


    }

    private void createMemoryAdapter(List<Memory> userMemoryList, View view) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getsContext(), 2);
        gridLayoutManager.setAutoMeasureEnabled(false);
        recyclerViewAllMemory = view.findViewById(R.id.homeFragemt_Rec_allMemories);
        homeFragmentAllMemoriesAdapter = new HomeFragmentAllMemories(view.getContext());
        recyclerViewAllMemory.setLayoutManager(gridLayoutManager);
        recyclerViewAllMemory.setHasFixedSize(true);
        recyclerViewAllMemory.setItemViewCacheSize(20);
        recyclerViewAllMemory.setAdapter(homeFragmentAllMemoriesAdapter);
        homeFragmentAllMemoriesAdapter.setList(userMemoryList);
        homeFragmentAllMemoriesAdapter.notifyDataSetChanged();
        homeFragmentAllMemoriesAdapter.setMemoryClickListener(new MemoryClickListener() {
            @Override
            public void onMemoryClick(Memory memory) {
                if (memory != null) {
                    Log.d(TAG, "onMemoryClick: " + memory);
                    Intent memoryViewer = new Intent(view.getContext(), MemoryViewer.class);
                    MemoryConverter memoryConverter = new MemoryConverter();
                    String s = memoryConverter.convertToGson(memory);
                    memoryViewer.putExtra(HomeFragement.MEMORY_TO_SHOW, s);
                    startActivity(memoryViewer);
                }
            }
        });
    }


    private void getDataFromDataBase(View view) {

        memoryViewModel.getData(getContext());
        memoryViewModel.memoryMutableLiveData.observe(getActivity(), new Observer<List<Memory>>() {
            @Override
            public void onChanged(List<Memory> memories) {
                Log.d("TAGGet", "onChanged: " + memories.toString());

                createMemoryAdapter(memories, view);
                if (memories.size() > 1) {
                    getLastMemory(memories);
                }
                homeFragmentListener.onGetAllMemories(memories);

                swipeRefreshLayout.setRefreshing(false);
                onGetDataVisibility(true);


            }
        });

    }

    private void getLastMemory(List<Memory> memories) {
        if (CheckInternetConnection.connection(getsContext())) {
            return;
        }
        List<Integer> memoryIdList = new ArrayList<>();
        for (Memory memory : memories) {
            memoryIdList.add(memory.getMemoryID());

        }
        Collections.sort(memoryIdList);
        Integer biggestMemortID = memoryIdList.get(memoryIdList.size() - 1);
        Memory LastMemory = memories.get(biggestMemortID - 1);
        onGetLastMemory(LastMemory);

    }

    private int getKeyASInt(String key) {
        String memory = key.replace("memory_", "");

        return Integer.parseInt(memory);
    }

    @Override
    public void onGetUserMemory(List<Memory> arrayList, View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAllMemory = view.findViewById(R.id.homeFragemt_Rec_allMemories);
        homeFragmentAllMemoriesAdapter = new HomeFragmentAllMemories(view.getContext());
        recyclerViewAllMemory.setLayoutManager(linearLayoutManager);
        recyclerViewAllMemory.setAdapter(homeFragmentAllMemoriesAdapter);
        homeFragmentAllMemoriesAdapter.setList(arrayList);
        homeFragmentAllMemoriesAdapter.notifyDataSetChanged();
        homeFragmentAllMemoriesAdapter.setMemoryClickListener(new MemoryClickListener() {
            @Override
            public void onMemoryClick(Memory memory) {
                if (memory != null) {
                    Log.d(TAG, "onMemoryClick: " + memory);
                    Intent memoryViewer = new Intent(view.getContext(), MemoryViewer.class);
                    MemoryConverter memoryConverter = new MemoryConverter();
                    String s = memoryConverter.convertToGson(memory);
                    memoryViewer.putExtra(HomeFragement.MEMORY_TO_SHOW, s);
                    startActivity(memoryViewer);
                }
            }
        });


    }


    public void onGetLastMemory(Memory memory) {
        lastMemory = memory;
        List<ImageToUpload> imagesUrl = memory.getPictures();
        CurrentLocation location = memory.getLocation();
        String tittle = memory.getTittle();
        int picturesCount = memory.getPicturesCount();
        long timeInMillis = 0;
        if (memory.getTimeInMillis() != null && !memory.getTimeInMillis().equals("")) {
            timeInMillis = Long.parseLong(memory.getTimeInMillis());
            setUpDate(timeInMillis);
        }

        if (memory.getLocation() != null && memory.getLocation().getLatitude() != 0) {
            setUpLocation(location);
        }
        if (imagesUrl != null && imagesUrl.size() > 1) {
            setupListOfImage(imagesUrl, picturesCount);
        }

        txtOldMemoryTittle.setText(tittle);

    }


    private void setUpDate(long timeInMillis) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            int minutes = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR);
            LocalDateTime oldDate = LocalDateTime.of(year, month, day, hour, minutes);
            LocalDateTime localDateTime = LocalDateTime.now();
            if (LanguageHelper.getDeviceLanguage().equals(LanguageHelper.ARABIC_CODE)) {
                long tottalDays = Duration.between(oldDate, localDateTime).toDays();
                if (tottalDays == 1l) {
                    txtOldMemoryDate.setText(" منذ " + tottalDays + " ايام ");
                } else if (tottalDays == 0l) {
                    txtOldMemoryDate.setText("اليوم");
                } else {
                    txtOldMemoryDate.setText(" منذ " + tottalDays + " ايام ");
                }
            } else {
                long tottalDays = Duration.between(oldDate, localDateTime).toDays();
                if (tottalDays == 1l) {
                    txtOldMemoryDate.setText(tottalDays + " Day Ago");
                } else if (tottalDays == 0l) {
                    txtOldMemoryDate.setText("Today");
                } else {
                    txtOldMemoryDate.setText(tottalDays + " Days Ago");
                }
            }

        }

    }

    private void setUpLocation(CurrentLocation location) {
        if (getActivity() != null) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (fromLocation.size() > 0) {
                    Address address = fromLocation.get(0);
                    TxtOldMemoryLocation.setText(address.getSubAdminArea());
                }


            } catch (IOException e) {
                return;
            }

        }
    }

    private void setupListOfImage(List<ImageToUpload> imageUrls, int imagesCount) {
//        Log.d(TAG, "setupListOfImage: " + imageUrls.toString());
        if (imagesCount == 1) {
            String s = imageUrls.get(0).getUrl();
            Picasso.get().load(s).resize(800, 800).centerCrop().into(circleImageView1);
            circleImageView2.setVisibility(View.GONE);
            circleImageView3.setVisibility(View.GONE);

            hideWhiteImage();
            hideSwiperRefresher(400);

        }
        if (imagesCount == 2) {
            String s1 = imageUrls.get(0).getUrl();
            String s2 = imageUrls.get(1).getUrl();
            Picasso.get().load(s1).resize(800, 800).centerCrop().into(circleImageView1);
            Picasso.get().load(s2).resize(800, 800).centerCrop().into(circleImageView2);
            circleImageView3.setVisibility(View.GONE);

            hideWhiteImage();
            hideSwiperRefresher(600);

        }

        //resize(300, 300).
        if (imagesCount == 3) {
            String s1 = imageUrls.get(0).getUrl();
            String s2 = imageUrls.get(1).getUrl();
            String s3 = imageUrls.get(2).getUrl();
            Picasso.get().load(s1).resize(800, 800).centerCrop().into(circleImageView1);
            Picasso.get().load(s2).resize(800, 800).centerCrop().into(circleImageView2);
            Picasso.get().load(s3).resize(800, 800).centerCrop().into(circleImageView3);
            hideWhiteImage();
            hideSwiperRefresher(800);


        }
        if (imagesCount > 3) {
            String s1 = imageUrls.get(0).getUrl();
            String s2 = imageUrls.get(1).getUrl();
            String s3 = imageUrls.get(2).getUrl();
            Log.d(TAG, "setupListOfImage: " + imageUrls.get(0));
            circleImageView1.getBorderColor();
            Picasso.get().load(s1).resize(800, 800).centerCrop().into(circleImageView1);

            Picasso.get().load(s2).resize(800, 800).centerCrop().into(circleImageView2);
            Picasso.get().load(s3).resize(800, 800).centerCrop().into(circleImageView3);
            showWhiteImage();
            int remains = imagesCount - 3;
            txtImagecRemainsCount.setText("+" + remains + "");
            hideSwiperRefresher(1000);


        }


    }

    private void hideWhiteImage() {
        circleImageViewWhite.setVisibility(View.GONE);
        txtImagecRemainsCount.setVisibility(View.GONE);
    }

    private void showWhiteImage() {
        circleImageViewWhite.setVisibility(View.VISIBLE);
        txtImagecRemainsCount.setVisibility(View.VISIBLE);
    }

    void hideSwiperRefresher(int sleep) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                onGetDataVisibility(true);
            }
        }, sleep);
        if (getActivity() != null) {

            recyclerViewAllMemory.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.recycler_view_slide_down));
        }

    }

    void onGetDataVisibility(boolean visible) {

        if (visible) {
            linearLayoutBigContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    private void updateOfflineCashing(List<Memory> memories) {
        memoryDao.deleteAllMemories().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: delete completed");
                        insertNewListOfMemory(memories);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }
                });

    }

    private void insertNewListOfMemory(List<Memory> memories) {
        memoryDao.insertMemories(memories).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Insert Completed");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }
                });
    }

}
