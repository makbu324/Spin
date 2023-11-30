package com.example.spotifyapitest.googleapi

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query



interface GoogleApiService {
    @POST("v1/images:annotate?")
    fun getGuess(@Body googleRequest: RequestBody,
                 @Query("key") key: String): Call<WebDetectionResponse>
}