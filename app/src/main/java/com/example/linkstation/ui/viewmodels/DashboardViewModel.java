package com.example.linkstation.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkstation.model.StationModel;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelLiveData;
    private MutableLiveData<List<StationModel.Data.Station>> stationsLiveData;

    public DashboardViewModel() {
        userModelLiveData = new MutableLiveData<>();
        stationsLiveData = new MutableLiveData<>();
    }

    public LiveData<UserModel> getUserModel() {
        return userModelLiveData;
    }

    public LiveData<List<StationModel.Data.Station>> getStations() {
        return stationsLiveData;
    }

    public void fetchUserDetails(String token, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<UserModel> call = apiService.getUser(token);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userModelLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable throwable) {
                // Handle failure
            }
        });
    }

    public void fetchLatestPublishedStations(String token, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<StationModel> call = apiService.getLatestPublishedStations(token, 1, 3);
        call.enqueue(new Callback<StationModel>() {
            @Override
            public void onResponse(Call<StationModel> call, Response<StationModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stationsLiveData.postValue(response.body().getData().getStations());
                }
            }

            @Override
            public void onFailure(Call<StationModel> call, Throwable throwable) {
                // Handle failure
            }
        });
    }
}

