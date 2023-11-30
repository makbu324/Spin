package com.example.spotifyapitest.spotifyapi

import com.example.spotifyapitest.spotifyapi.Item

data class UserResponse(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)