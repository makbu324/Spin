package com.spin_cake_con

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface PlacesApiService {
    @GET
    fun getNearbyPlaces(@Url url: String): Call<PlaceResponse>
}