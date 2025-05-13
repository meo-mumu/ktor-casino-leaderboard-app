//Player.kt

package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val pseudo: String,
    var points: Int = 0,
    val rank: Int? = null // Optionnel
)

fun validatePseudo(pseudo: String) {
    require(pseudo.length in 3..20) { "Pseudo must be between 3 and 20 characters." }
    require(pseudo.matches(Regex("^[A-Za-z0-9_]+$"))) { "Pseudo must contain only letters, digits, or underscores." }
}

fun validatePoints(points: Int) {
    require(points > 0) { "Points must be a positive integer." }
}