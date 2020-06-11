package com.vansha10.rxjavapractice.network;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface RequestApi {

    @GET("todo/1")
    Flowable<ResponseBody> makeQuery();
}
