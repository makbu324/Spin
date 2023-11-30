package com.example.spotifyapitest.googleapi

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class LabelAnnotation(val mid: String, val description: String, val score: Double)

@kotlinx.serialization.Serializable
data class AnnotateImageResponse(val labelAnnotations: List<LabelAnnotation>)

@Serializable
data class ResponseWrapper(val responses: List<AnnotateImageResponse>)

@Serializable
data class ImageRequest(val content: String)

@Serializable
data class ImageSource(val imageUri: String)

@Serializable
data class Feature(val type: String, val maxResults: Int)


@Serializable
data class AnnotateImageRequest(val image: ImageRequest, val features: List<Feature>)

@Serializable
data class JsonRequest(val requests: List<AnnotateImageRequest>)

