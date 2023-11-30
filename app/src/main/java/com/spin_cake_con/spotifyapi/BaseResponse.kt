package com.example.spotifyapitest.spotifyapi

import com.google.gson.annotations.SerializedName

data class BaseResponse<T> (
    @SerializedName("albums") var albums: T? = null
)