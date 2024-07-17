package com.example.linkstation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/v1/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
