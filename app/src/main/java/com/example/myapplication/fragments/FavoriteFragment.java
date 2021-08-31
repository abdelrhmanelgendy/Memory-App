package com.example.myapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.FavoriteMemoryAdapter;
import com.example.myapplication.helpers.CheckInternetConnection;
import com.example.myapplication.helpers.FireBaseConstants;
import com.example.myapplication.jsonConverter.ListOfImageToUploadConverter;
import com.example.myapplication.jsonConverter.ListOfMemoryConverter;
import com.example.myapplication.pojo.ImageToUpload;
import com.example.myapplication.pojo.Memory;
import com.example.myapplication.ui.FavoriteImageShower;
import com.example.myapplication.util.FavouriteAdapterListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class FavoriteFragment extends Fragment implements MemoryEventListener, FavouriteAdapterListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "favoriteFragmentTAG";
    public static final String LIST_OF_IMAGE_TO_UPLOAD = "list_of_image_toUpload";
    public static final String POSITION_OF_CLICKED_IMAGE = "clicked_image_position";
    EditText txtSearch;
    private String mParam1;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    MemoryEventListener memoryEventListener;
    RecyclerView favoriteRecyclerView;
    boolean internetConnectionState = false;
    List<ImageToUpload> lists = new ArrayList<>();
    FavoriteMemoryAdapter favoriteMemoryAdapter;
    GridLayoutManager gridLayoutManager;

    public FavoriteFragment() {
    }


    public static FavoriteFragment newInstance(String stringedMemory) {
        Log.d(TAG, "newInstance: " + stringedMemory);
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, stringedMemory);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initVars();
        CheckInternetConnection connectionState = new CheckInternetConnection();
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connection = connectionState.connection(connectivityManager);
        if (connection) {
            //Device is online
            internetConnectionState = true;


        } else {
            internetConnectionState = false;
            //Device is oflline
        }

    }

    private void initVars() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        memoryEventListener = this;
        favoriteMemoryAdapter = new FavoriteMemoryAdapter();
        favoriteMemoryAdapter.setContext(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        favoriteMemoryAdapter.setFavouriteAdapterListener(this);

    }

    private void getAllMemories() {
        databaseReference = FirebaseDatabase.getInstance().getReference("UserMemory").child(FireBaseConstants.getfirebaseUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Memory> userMemoryList = new ArrayList<>();
                if (snapshot.exists()) {
                    if (snapshot != null) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Memory memory = snapshot1.getValue(Memory.class);
                            userMemoryList.add(memory);

                        }
                        memoryEventListener.onGetAllMemories(userMemoryList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_favorite, container, false);
        favoriteRecyclerView = inflate.findViewById(R.id.favoriteFragmentRecyclerViewx);
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (txtSearch.getText().length()>0)
        {
            filterList(txtSearch.getText().toString());

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtSearch = view.findViewById(R.id.favoriteFragmentET_search);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    filterList(txtSearch.getText().toString());
                    return true;
                }
                return true;
            }
        });

        if (CheckInternetConnection.connection(getContext())) {
            getAllMemories();
        } else {
            getFavouriteOfflineCashing();
        }
        createSearchObservable();
    }

    private void getFavouriteOfflineCashing() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            ListOfMemoryConverter listOfMemoryConverter = new ListOfMemoryConverter();
            List<Memory> memories = listOfMemoryConverter.fromStringToMemoryList(mParam1);
            memoryEventListener.onGetAllMemories(memories);
        }

    }

    private void createSearchObservable() {
        Observable<String> searchObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> emitter) throws Exception {
                txtSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        emitter.onNext(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }).distinctUntilChanged();
        searchObservable.subscribe(txt -> filterList(txt));
    }

    private void filterList(String searchFor) {
        if (searchFor.length() == 0) {
            favoriteMemoryAdapter.setListOfimageToUploads(lists);
            favoriteMemoryAdapter.notifyDataSetChanged();
        }
        List<ImageToUpload> filteredList = new ArrayList<>();
        for (ImageToUpload imageToUpload : lists) {
            if (imageToUpload.getMemoryTittle().toLowerCase().contains(searchFor.toLowerCase())) {
                filteredList.add(imageToUpload);
            }
        }
        favoriteMemoryAdapter.setListOfimageToUploads(filteredList);
        favoriteMemoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAllMemories(List<Memory> memoryList) {

        lists = new ArrayList<>();
        lists.clear();

        for (Memory memory : memoryList) {
            List<ImageToUpload> pictures = memory.getPictures();
            for (ImageToUpload imageToUpload : pictures) {
                if (imageToUpload.isImageFavorite()) {
                    lists.add(imageToUpload);
                }
            }
        }


        Log.d(TAG, "onGetAllMemories: " + lists.size());
        Collections.reverse(lists);
        favoriteMemoryAdapter.setListOfimageToUploads(lists);
        favoriteRecyclerView.setLayoutManager(gridLayoutManager);
//        favoriteRecyclerView.hasFixedSize();
        favoriteRecyclerView.setAdapter(favoriteMemoryAdapter);
        favoriteMemoryAdapter.notifyDataSetChanged();

    }


    @Override
    public void onImageClick(List<ImageToUpload> lists, ImageToUpload imageToUpload, int adapterPosition) {
        Log.d(TAG, "onImageClick: " + imageToUpload);
        ListOfImageToUploadConverter listOfImageToUploadConverter = new ListOfImageToUploadConverter();
        String listToString = listOfImageToUploadConverter.fromListToString(lists);
        Intent intent = new Intent(getContext(), FavoriteImageShower.class);
        intent.putExtra(LIST_OF_IMAGE_TO_UPLOAD, listToString);
        intent.putExtra(POSITION_OF_CLICKED_IMAGE, adapterPosition);
        startActivity(intent);

    }
}

