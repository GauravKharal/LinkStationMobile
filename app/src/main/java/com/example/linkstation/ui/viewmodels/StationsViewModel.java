package com.example.linkstation.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkstation.model.StationModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationsViewModel extends ViewModel {

    private MutableLiveData<List<StationModel.Data.Station>> latestStationsLiveData;
    private MutableLiveData<List<StationModel.Data.Station>> popularStationsLiveData;

    public StationsViewModel() {
        latestStationsLiveData = new MutableLiveData<>();
        popularStationsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<StationModel.Data.Station>> getLatestStations() {
        return latestStationsLiveData;
    }
    public LiveData<List<StationModel.Data.Station>> getPopularStations() {
        return popularStationsLiveData;
    }



    public void fetchLatestStations(String token, int page, int size, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<StationModel> call = apiService.getLatestPublishedStations(token, page, size);
        call.enqueue(new Callback<StationModel>() {
            @Override
            public void onResponse(Call<StationModel> call, Response<StationModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    latestStationsLiveData.postValue(response.body().getData().getStations());
                }
            }

            @Override
            public void onFailure(Call<StationModel> call, Throwable throwable) {
                // Handle failure
            }
        });
    }

    public void fetchPopularStations(String token, int page, int size, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<StationModel> call = apiService.getMostViewedStations(token,page,size);
        call.enqueue(new Callback<StationModel>() {
            @Override
            public void onResponse(Call<StationModel> call, Response<StationModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    popularStationsLiveData.postValue(response.body().getData().getStations());
                }
            }

            @Override
            public void onFailure(Call<StationModel> call, Throwable throwable) {
                // Handle failure
            }
        });
    }
}
