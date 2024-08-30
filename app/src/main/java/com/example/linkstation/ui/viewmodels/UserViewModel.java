package com.example.linkstation.ui.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkstation.model.UpdateUserDetailsRequest;
import com.example.linkstation.model.UserModel;
import com.example.linkstation.network.ApiService;
import com.example.linkstation.network.RetrofitClient;

import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelLiveData;
    private MutableLiveData<Boolean> updateSuccessLiveData;

    public UserViewModel() {
        userModelLiveData = new MutableLiveData<>();
        updateSuccessLiveData = new MutableLiveData<>();
    }

    // Getter for fetching user details LiveData
    public LiveData<UserModel> getUserModel() {
        return userModelLiveData;
    }

    // Getter for tracking the success of the update operation
    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccessLiveData;
    }

    // Method to fetch user details
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

    // Method to update user details
    public void updateUserDetails(String token, String fullName, Date dateOfBirth, Context context) {
        UpdateUserDetailsRequest updatedUser = new UpdateUserDetailsRequest(fullName, dateOfBirth);
        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        Call<ResponseBody> call = apiService.updateUserDetails(token, updatedUser);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    updateSuccessLiveData.postValue(true);
                } else {
                    updateSuccessLiveData.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                updateSuccessLiveData.postValue(false);
            }

        });
    }
}
