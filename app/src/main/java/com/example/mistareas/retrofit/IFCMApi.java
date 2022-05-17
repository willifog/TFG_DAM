package com.example.mistareas.retrofit;

import com.example.mistareas.models.FCMBody;
import com.example.mistareas.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type.application/json",
            "Authorization:key=AAAASOxI7Bk:APA91bF8qfpHCpcCuDj1Mwf5zN0t7sBeAubCJY8WUyxKf-ghk9BvdIAsu2raRWSO3AUcK96gkgTzq_hxC716ILgsy4beVaX1nUFKjOuvXkMBb7_skEvWswIK9zAvs97wjxFLgg2e1zD8"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
