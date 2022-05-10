package com.example.mistareas.providers;

import com.example.mistareas.models.FCMBody;
import com.example.mistareas.models.FCMResponse;
import com.example.mistareas.retrofit.IFCMApi;
import com.example.mistareas.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){

    }

    public Call<FCMResponse> sendNotification (FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
