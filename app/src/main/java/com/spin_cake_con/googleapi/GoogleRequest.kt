package com.example.spotifyapitest.googleapi

import com.google.gson.annotations.SerializedName

class GoogleRequest {
    @SerializedName("responses")
    lateinit var features: Array<Feature>
}