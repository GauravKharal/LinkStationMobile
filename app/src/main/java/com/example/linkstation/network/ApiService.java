package com.example.linkstation.network;

import com.example.linkstation.model.LoginRequest;
import com.example.linkstation.model.RegisterRequest;
import com.example.linkstation.model.UserModel;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/users/login")
    Call<UserModel> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/v1/users/register")
    Call<UserModel> registerUser(@Body RegisterRequest registerRequest);

    @GET("/api/v1/users/logout")
    Call<Void> logoutUser();

    @GET("/endpoint")
    Call<ResponseBody> getEndpointData();

    @POST("/endpoint")
    Call<ResponseBody> postEndpointData(@Body RequestBody requestBody);
}
