//PlayerRepository.kt

package com.model

interface PlayerRepository {
    fun addPlayerByPseudo(pseudo: String)
    fun updatePlayerPoints(pseudo: String, pointsToAdd: Int)
    fun getAllPlayersSortedByRank(): List<PlayerPointsRank>
    fun getPlayerWithRankByPseudo(pseudo: String): PlayerPointsRank
    fun clearAllPlayers()
}