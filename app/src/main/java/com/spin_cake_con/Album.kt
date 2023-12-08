package com.spin_cake_con

import java.util.UUID

data class Album(
    val artist: String,
    val title: String,
    val year: String,
    val base64_album_art: String,
    val id: UUID,
    val link: String,
    var list_of_prices: MutableList<Triple<String, Double, String>>
    // *** Index 0: Price, Index 1: Location found, Index 2: Memo ***
)

public const val sgt_pepper_art = "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"