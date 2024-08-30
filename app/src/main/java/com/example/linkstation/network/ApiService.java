package com.example.linkstation.network;

import com.example.linkstation.model.ChangePasswordRequest;
import com.example.linkstation.model.CreateStationRequest;
import com.example.linkstation.model.LoginRequest;
import com.example.linkstation.model.RefreshTokenRequest;
import com.example.linkstation.model.RegisterRequest;
import com.example.linkstation.model.StationModel;
import com.example.linkstation.model.UpdateUserDetailsRequest;
import com.example.linkstation.model.UserModel;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @POST("/api/v1/users/login")
    Call<UserModel> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/v1/users/register")
    Call<UserModel> registerUser(@Body RegisterRequest registerRequest);

    @POST("/api/v1/users/logout")
    Call<UserModel> logoutUser(@Header("Authorization") String token);

    @POST("/api/v1/users/refresh-token")
    Call<UserModel> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    @GET("/api/v1/users/get-user")
    Call<UserModel> getUser(@Header("Authorization") String token);

    @POST("/api/v1/users/change-password")
    Call<ResponseBody> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @Multipart
    @POST("/api/v1/users/update-avatar")
    Call<ResponseBody> updateAvatar(@Header("Authorization") String token, @Part MultipartBody.Part avatar);


    @POST("/api/v1/users/update-user-details")
    Call<ResponseBody> updateUserDetails(@Header("Authorization") String token, @Body UpdateUserDetailsRequest request);



    @Multipart
    @POST("/api/v1/station/create")
    Call<StationModel> createStation(
            @Header("Authorization") String token,
            @Part MultipartBody.Part stationImage,
            @Part List<MultipartBody.Part> linkImages,
            @Part("stationUrl") RequestBody stationUrl,
            @Part("stationTitle") RequestBody stationTitle,
            @Part("stationDescription") RequestBody stationDescription,
            @Part("instagram") RequestBody instagram,
            @Part("facebook") RequestBody facebook,
            @Part("twitter") RequestBody twitter,
            @Part("youtube") RequestBody youtube,
            @Part("visibility") RequestBody visibility,
            @Part("links[url]") List<RequestBody> linkUrls,
            @Part("links[title]") List<RequestBody> linkTitles,
            @Part("links[position]") List<RequestBody> linkPositions
    );


    @GET
    Call<ResponseBody> getStation(@Url String url);

    @GET("/api/v1/station/public")
    Call<StationModel> getLatestPublishedStations(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    @GET("/endpoint")
    Call<ResponseBody> getEndpointData();

    @POST("/endpoint")
    Call<ResponseBody> postEndpointData(@Body RequestBody requestBody);
}
