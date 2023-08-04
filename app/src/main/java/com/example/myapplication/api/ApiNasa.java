package com.example.myapplication.api;



import com.example.myapplication.model.HackNasa;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiNasa {
    @GET("planetary/apod")
    Call<HackNasa> getDataFromNasa(@Query("api_key") String apiKey, @Query("date") String date);

}
