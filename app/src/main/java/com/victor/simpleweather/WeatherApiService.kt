package com.victor.simpleweather

import com.victor.simpleweather.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/v1/current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Call<WeatherResponse>
}
