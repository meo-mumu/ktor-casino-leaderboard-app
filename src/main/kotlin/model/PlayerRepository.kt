//PlayerRepository.kt

package com.model

object PlayerRepository {

    private val players = mutableListOf(
        Player("toto"),
        Player("tata"),
        Player("titi")
    )

    private fun getPlayerByPseudo(pseudo: String): Player? {
        return players.firstOrNull { it.pseudo.equals(pseudo, ignoreCase = true) }
    }


    fun addPlayerByPseudo(pseudo: String) {
        if (getPlayerByPseudo(pseudo) != null) {
            throw IllegalStateException("Player with pseudo $pseudo already exists")
        }
        players.add(Player(pseudo))
    }

    fun updatePlayerPoints(pseudo: String, pointsToAdd: Int) {
        val player = getPlayerByPseudo(pseudo)
        if (player == null) {
            throw NoSuchElementException("Player with pseudo $pseudo not found")
        }
        player.points += pointsToAdd
    }

    fun getPlayerWithRankByPseudo(pseudo: String): PlayerPointsRank {
        val sortedPlayers = players.sortedByDescending { it.points }
        val player = sortedPlayers.find { it.pseudo.equals(pseudo, ignoreCase = true) }
        if (player == null) {
            throw NoSuchElementException("Player with pseudo $pseudo not found")
        }
        val rank = sortedPlayers.indexOf(player) + 1
        return PlayerPointsRank(player.pseudo, player.points, rank)
    }


    fun getAllPlayersSortedByRank(): List<PlayerPointsRank> {
        if (players.isEmpty()) {
            throw NoSuchElementException("No players found")
        }
        val sortedPlayers = players.sortedByDescending { it.points }
        return players
            .sortedByDescending { it.points }
            .mapIndexed { index, player ->
                PlayerPointsRank(player.pseudo, player.points, index + 1)
            }
    }

    fun clearAllPlayers() {
        players.clear()
    }
}
