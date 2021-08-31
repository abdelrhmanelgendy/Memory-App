package com.example.myapplication.databases.local;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.RoomDatabase;

import com.example.myapplication.pojo.Memory;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MemoryViewModel extends ViewModel {
    public MutableLiveData<List<Memory>> memoryMutableLiveData = new MutableLiveData<>();
    public OnGetDataFromDatabaseListener getDataFromDatabaseListener;

    public void setGetDataFromDatabaseListener(OnGetDataFromDatabaseListener getDataFromDatabaseListener) {
        this.getDataFromDatabaseListener = getDataFromDatabaseListener;
    }

    public void getData(Context context) {
        MemoryDatabase memoryDatabase = MemoryDatabase.getInstance(context);
        MemoryDao memoryDao = memoryDatabase.memoryDao();
        memoryDao.getAllMemories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<List<Memory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        getDataFromDatabaseListener.onDataStarted();
                    }

                    @Override
                    public void onNext(@NonNull List<Memory> memories) {
                        Log.d("TAGGet", "onNext: "+memories
                        .toString());
                        memoryMutableLiveData.setValue(memories);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        getDataFromDatabaseListener.onDataCompleted();
                    }
                });

    }


}
