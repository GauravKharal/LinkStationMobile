package com.example.linkstation.ui.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkstation.model.StationModel;
import com.example.linkstation.model.StationViewsModel;
import com.example.linkstation.model.ViewsModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationsViewModel extends ViewModel {

    private MutableLiveData<List<StationModel.Data.Station>> latestStationsLiveData;
    private MutableLiveData<List<StationModel.Data.Station>> popularStationsLiveData;
    private MutableLiveData<List<ViewsModel.Data>> viewsLiveData;
    private MutableLiveData<List<StationViewsModel.Data>> myPopularStationsLiveData;

    public StationsViewModel() {
        latestStationsLiveData = new MutableLiveData<>();
        popularStationsLiveData = new MutableLiveData<>();
        viewsLiveData = new MutableLiveData<>();
        myPopularStationsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<StationModel.Data.Station>> getLatestStations() {
        return latestStationsLiveData;
    }

    public LiveData<List<StationModel.Data.Station>> getPopularStations() {
        return popularStationsLiveData;
    }

    public LiveData<List<ViewsModel.Data>> getTotalViews() {
        return viewsLiveData;
    }

    public LiveData<List<StationViewsModel.Data>> getMyPopularStations() {
        return myPopularStationsLiveData;
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
        Call<StationModel> call = apiService.getMostViewedStations(token, page, size);
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

    // Method to fetch views data
    public void fetchViewsData(String token, int days, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<ViewsModel> call = apiService.getTotalViews(token, days);
        call.enqueue(new Callback<ViewsModel>() {
            @Override
            public void onResponse(Call<ViewsModel> call, Response<ViewsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewsLiveData.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ViewsModel> call, Throwable throwable) {
                // Handle failure
            }
        });
    }

    // Method to fetch my popular stations
    public void fetchMyPopularStations(String token, Context context) {
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<StationViewsModel> call = apiService.getMyMostViewedStations(token);
        call.enqueue(new Callback<StationViewsModel>() {
            @Override
            public void onResponse(Call<StationViewsModel> call, Response<StationViewsModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myPopularStationsLiveData.postValue(response.body().getData());
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<StationViewsModel> call, Throwable throwable) {
            }
        });
    }
}
