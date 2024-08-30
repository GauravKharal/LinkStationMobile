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

    private MutableLiveData<List<StationModel.Data.Station>> stationsLiveData;

    public StationsViewModel() {
        stationsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<StationModel.Data.Station>> getStations() {
        return stationsLiveData;
    }



    public void fetchRecentStations(String token, int page, int size, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<StationModel> call = apiService.getLatestPublishedStations(token, page, size);
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
