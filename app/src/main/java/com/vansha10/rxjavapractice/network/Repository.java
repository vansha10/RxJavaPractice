package com.vansha10.rxjavapractice.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class Repository {
    private static final String TAG = "Repository";
    private static Repository instance;

    public static Repository getInstance(){
        if(instance == null){
            instance = new Repository();
        }
        return instance;
    }


    public LiveData<ResponseBody> makeReactiveQuery(){
        // converting flowable to livedata
        return LiveDataReactiveStreams.fromPublisher(ServiceGenerator.getRequestApi()
                .makeQuery()
                .subscribeOn(Schedulers.io()));
    }
}
