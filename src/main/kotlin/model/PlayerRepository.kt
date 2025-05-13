//PlayerRepository.kt

package com.model

interface PlayerRepository {
    suspend fun addPlayerByPseudo(pseudo: String)
    suspend fun updatePlayerPoints(pseudo: String, pointsToAdd: Int)
    suspend fun getAllPlayersSortedByRank(): List<Player>
    suspend fun getPlayerWithRankByPseudo(pseudo: String): Player
    suspend fun clearAllPlayers()
}