//Player.kt

package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val pseudo: String,
    var points: Int = 0
)

@Serializable
data class PlayerPointsRank(
    val pseudo: String,
    val points: Int,
    val rank: Int
)