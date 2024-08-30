package com.example.linkstation.network;

import android.content.Context;

import com.chuckerteam.chucker.api.ChuckerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://mint-briefly-cheetah.ngrok-free.app";

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(new ChuckerInterceptor(context))
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
