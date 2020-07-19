package com.shaun.letschat.Fragments;

import com.shaun.letschat.Notifications.MyResponse;
import com.shaun.letschat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAdovAcmo:APA91bEXHNoXFQBFiM0Br8OPEDLBudB5EWedgYd0knw-e1BcfgDp-rkw2Lcbs6seZX7Yn4HBT63BGtpRRHUczjlrds1rBIz0DpSifcrWIzVfWL18t26aJ7cyLZVaaIJJf2w-HY6z5yns"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}

//AAAAdovAcmo:APA91bEXHNoXFQBFiM0Br8OPEDLBudB5EWedgYd0knw-e1BcfgDp-rkw2Lcbs6seZX7Yn4HBT63BGtpRRHUczjlrds1rBIz0DpSifcrWIzVfWL18t26aJ7cyLZVaaIJJf2w-HY6z5yns
