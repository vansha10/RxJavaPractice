package com.vansha10.rxjavapractice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vansha10.rxjavapractice.network.Repository;

import okhttp3.ResponseBody;

public class MainViewModel extends ViewModel {
    private Repository repository;

    public MainViewModel() {
        repository = Repository.getInstance();
    }

    public LiveData<ResponseBody> makeQuery(){
        return repository.makeReactiveQuery();
    }
}
