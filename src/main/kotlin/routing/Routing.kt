package com.routing

import com.model.Player
import com.model.PlayerRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

@Serializable
data class PlayerRequest(val pseudo: String) {
    fun validate() {
        require(pseudo.length in 3..20) { "Pseudo must be between 3 and 20 characters." }
        require(pseudo.matches(Regex("^[A-Za-z0-9_]+$"))) { "Pseudo must contain only letters, digits, or underscores." }
    }
}

@Serializable
data class UpdatePointsRequest(val points: Int) {
    fun validate() {
        require(points > 0) { "Points must be a positive integer." }
    }
}

fun Application.configurePlayersRoutes() {

    install(StatusPages) {
        val log = LoggerFactory.getLogger("Routing")

        exception<IllegalStateException> { call, cause ->
            log.error("Conflict: ${cause.message}")
            call.respond(HttpStatusCode.Conflict, cause.message ?: "Conflict")
        }

        exception<NoSuchElementException> { call, cause ->
            log.error("Not found: ${cause.message}")
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }

        exception<IllegalArgumentException> { call, cause ->
            log.error("Validation error: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "Invalid request")
        }

        exception<Throwable> { call, cause ->
            log.error("Internal server error: ${cause.message}")
            call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${cause.message}")
        }
    }

    routing {
        val log = LoggerFactory.getLogger("Routing")
        val playerRepo by inject<PlayerRepository>()
        log.info("PlayerRepository injected: ${playerRepo::class.simpleName}")

        route("/players") {

            get {
                log.info("GET /players - Fetching all players sorted by rank")
                val playersWithRank: List<Player> = playerRepo.getAllPlayersSortedByRank()
                call.respond(playersWithRank)
            }

            post {
                val request = call.receive<PlayerRequest>()
                log.info("POST /players - Adding player: ${request.pseudo}")
                request.validate()
                playerRepo.addPlayerByPseudo(request.pseudo)
                call.respondText("Player ${request.pseudo} added successfully")
            }

            delete {
                log.info("DELETE /players - Clearing all players")
                playerRepo.clearAllPlayers()
                call.respondText("All players removed")
            }

            route("{pseudo}") {

                get {
                    val pseudo = call.parameters["pseudo"]!!
                    log.info("GET /players/$pseudo - Fetching player with rank")
                    val playerWithRank = playerRepo.getPlayerWithRankByPseudo(pseudo)
                    call.respond(playerWithRank)
                }

                put("points") {
                    val pseudo = call.parameters["pseudo"]!!
                    val updateRequest = call.receive<UpdatePointsRequest>()
                    log.info("PUT /players/$pseudo/points - Updating points by ${updateRequest.points}")
                    updateRequest.validate()
                    playerRepo.updatePlayerPoints(pseudo, updateRequest.points)
                    call.respondText("Player $pseudo updated with ${updateRequest.points} points")
                }

            }
        }
    }
}