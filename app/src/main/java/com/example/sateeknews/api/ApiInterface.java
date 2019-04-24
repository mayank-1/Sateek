package com.example.sateeknews.api;

import com.example.sateeknews.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<News> getNews(
           @Query("country") String country,
           @Query("category") String category,
           @Query("apiKey") String apiKey
    );
}
