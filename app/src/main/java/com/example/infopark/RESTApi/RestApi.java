package com.example.infopark.RESTApi;

import com.example.infopark.Utils.Test;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestApi {

    @POST("register")
    Call<ResponseMessage> register(@Body RegisterForm registerForm);

    @POST("login")
    Call<LoginResponse> login(@Body LoginForm loginForm);

    @POST("getSalt")
    Call<ResponseMessage> getSalt(@Body LoginForm loginForm);

    @POST("getSavedLocation")
    Call<SavedLocation> getSavedLocation(@Body RequestSavedLocation requestSavedLocation);

    @POST("setSavedLocation")
    Call<ResponseMessage> setSavedLocation(@Body RequestSavedLocation requestSavedLocation);

}
