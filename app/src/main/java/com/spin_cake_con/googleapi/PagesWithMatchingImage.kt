package com.example.spotifyapitest.googleapi

data class PagesWithMatchingImage(
    val fullMatchingImages: List<FullMatchingImage>,
    val pageTitle: String,
    val partialMatchingImages: List<PartialMatchingImageX>,
    val url: String
)