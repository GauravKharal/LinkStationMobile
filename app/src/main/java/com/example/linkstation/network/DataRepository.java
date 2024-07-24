package com.example.linkstation.network;

import com.example.linkstation.model.RegisterRequest;
import com.example.linkstation.model.RegisterResponse;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.model.LoginRequest;
import com.example.linkstation.model.LoginResponse;
import com.example.linkstation.network.NetworkClient;

import retrofit2.Call;

public class DataRepository {
    private final ApiService apiService;

    public DataRepository() {
        apiService = NetworkClient.getRetrofitClient().create(ApiService.class);
    }

    public Call<LoginResponse> loginUser(LoginRequest loginRequest) {
        return apiService.loginUser(loginRequest);
    }
    public Call<RegisterResponse> registerUser(RegisterRequest registerRequest) {
        return apiService.registerUser(registerRequest);
    }
}
