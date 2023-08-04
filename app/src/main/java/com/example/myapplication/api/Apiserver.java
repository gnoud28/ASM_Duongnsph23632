package com.example.myapplication.api;

import com.example.myapplication.local;
import com.example.myapplication.model.ApiData;
import com.example.myapplication.model.DataServer;
import com.example.myapplication.model.HackNasa;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Apiserver {
    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

    Apiserver apiService = new Retrofit.Builder()
            .baseUrl(local.URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Apiserver.class);

    @POST("api/add")
    Call<Void> postData(@Body HackNasa data);

    @GET("api/")
    Call<DataServer> getData();

    @GET("api/detail/{id}")
    Call<ApiData> getDataDetail(@Path("id")String id);

    @PUT("api/update/{id}")
    Call<Void> updateData(@Path("id") String id, @Body HackNasa data);

    @DELETE("api/delete/{id}")
    Call<Void> deleteData(@Path("id") String id);


}
