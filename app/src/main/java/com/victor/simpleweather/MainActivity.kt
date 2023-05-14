package com.victor.simpleweather

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.victor.simpleweather.databinding.ActivityMainBinding
import com.victor.simpleweather.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the user's current location
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission to access the user's location
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Get the latitude and longitude of the user's current location
            val latitude = location.latitude
            val longitude = location.longitude

            // Use the Retrofit call to get the current weather at the user's location
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(WeatherApiService::class.java)

            val apiKey = "42d72070534a4fad9c850815231405"
            val query = "$latitude,$longitude"
            val call = service.getCurrentWeather(apiKey, query)
            call.enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        if (weatherResponse != null) {
                            val location = weatherResponse.location
                            val current = weatherResponse.current
                            binding.cityTextview.text = location.name
                            binding.countryTextview.text = location.country
                            binding.temperatureTextview.text = current.temp_c.toString() + "°C"
                            binding.conditionTextview.text = current.condition.text
                        } else {
                            println("Empty response body")
                        }
                    } else {
                        println("Error response: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    println("Failure: ${t.message}")
                }
            })

            // Remove the location updates after getting the user's location
            locationManager.removeUpdates(this)
        }
    }
}
