package com.example.spotifyapitest.spotifyapi

import com.example.spotifyapitest.spotifyapi.BaseResponse
import com.example.spotifyapitest.spotifyapi.Item
import com.example.spotifyapitest.spotifyapi.Token
import com.example.spotifyapitest.spotifyapi.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface SpotifyApiService {
    @GET("v1/search")
    fun getAlbum(@Query("q") q:String, @Query("type") type:Array<String>,
        @Query("market") market:String, @Header("Authorization") Authorization: String): Call<BaseResponse<UserResponse>>


    @FormUrlEncoded
    @POST("api/token")
    fun getToken(@Field("grant_type") grantType:String,
                 @Field("client_id") client_id:String,
                 @Field("client_secret") client_secret:String): Call<Token>
}