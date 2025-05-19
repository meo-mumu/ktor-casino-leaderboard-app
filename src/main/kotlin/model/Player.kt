package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val pseudo: String,
    var points: Int = 0,
    val rank: Int? = null
)