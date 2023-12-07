package com.spin_cake_con

import android.location.Location

data class PlaceResponse(
    val results: List<Place>,
    val status: String
)

data class Place(
    val geometry: Geometry,
    val name: String,
    val vicinity: String
)

data class Geometry(
    val location: LocationC
)

data class LocationC(
    val lat: Double,
    val lng: Double
)