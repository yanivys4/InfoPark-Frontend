package com.example.infopark.RESTApi;

import com.example.infopark.Utils.Test;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {
    @GET("test")
    Call<Test> getTest();

    @POST("register")
    Call<com.example.infopark.RESTApi.ResponseMessage> register(@Body RegisterForm registerForm);

    @POST("login")
    Call<ResponseMessage> login(@Body LoginForm loginForm);


}
