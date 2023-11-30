package com.example.spotifyapitest.googleapi

data class WebDetection(
    val bestGuessLabels: List<BestGuessLabel>,
    val fullMatchingImages: List<FullMatchingImage>,
    val pagesWithMatchingImages: List<PagesWithMatchingImage>,
    val partialMatchingImages: List<PartialMatchingImageX>,
    val visuallySimilarImages: List<VisuallySimilarImage>,
    val webEntities: List<WebEntity>
)