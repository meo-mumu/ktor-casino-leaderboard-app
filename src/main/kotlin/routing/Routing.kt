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
data class PlayerRequest(val pseudo: String)

@Serializable
data class UpdatePointsRequest(val points: Int)

fun Application.configureRouting() {

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

            post("add") {
                val request = call.receive<PlayerRequest>()
                log.info("POST /players/add - Adding player: ${request.pseudo}")
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
                    val request = call.receive<UpdatePointsRequest>()
                    log.info("PUT /players/$pseudo/points - Updating points by ${request.points}")
                    playerRepo.updatePlayerPoints(pseudo, request.points)
                    call.respondText("Player $pseudo updated with ${request.points} points")
                }

                post("addPoints") {
                    val pseudo = call.parameters["pseudo"]!!
                    val request = call.receive<UpdatePointsRequest>()
                    log.info("POST /players/$pseudo/addPoints - Adding points: ${request.points}")
                    playerRepo.updatePlayerPoints(pseudo, request.points)
                    call.respondText("Player $pseudo updated with ${request.points} points")
                }
            }
        }
    }
}