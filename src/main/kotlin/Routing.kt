package com

import com.model.PlayerPointsRank
import com.model.PlayerRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class PlayerRequest(val pseudo: String)

@Serializable
data class UpdatePointsRequest(val points: Int)

fun Application.configureRouting() {

    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, cause.message ?: "Conflict")
        }

        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }

        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${cause.message}")
        }
    }

    routing {
        staticResources("static", "static")



        route("/players") {

            // Ajouter un joueur
            post("add") {
                val request = call.receive<PlayerRequest>()
                PlayerRepository.addPlayerByPseudo(request.pseudo)
                call.respondText("Player ${request.pseudo} added successfully")
            }

            // Liste triée des joueurs avec classement
            get {
                val playersWithRank: List<PlayerPointsRank> = PlayerRepository.getAllPlayersSortedByRank()
                call.respond(playersWithRank)
            }

            // Supprimer tous les joueurs
            delete {
                PlayerRepository.clearAllPlayers()
                call.respondText("All players removed")
            }

            // Sous-routes pour un pseudo donné
            route("{pseudo}") {

                // Récupérer infos joueur avec classement
                get {
                    val pseudo = call.parameters["pseudo"]!!
                    val playerWithRank = PlayerRepository.getPlayerWithRankByPseudo(pseudo)
                    call.respond(playerWithRank)
                }

                // Mettre à jour les points du joueur
                put("points") {
                    val pseudo = call.parameters["pseudo"]!!
                    val request = call.receive<UpdatePointsRequest>()
                    PlayerRepository.updatePlayerPoints(pseudo, request.points)
                    call.respondText("Player $pseudo updated with ${request.points} points")
                }

                post("addPoints") {
                    val pseudo = call.parameters["pseudo"]!!
                    val request = call.receive<UpdatePointsRequest>()
                    PlayerRepository.updatePlayerPoints(pseudo, request.points)
                    call.respondText("Player $pseudo updated with ${request.points} points")
                }
            }
        }
    }
}


/*
fun Application.configureRouting() {
    routing {
        staticResources("static", "static")

        //updated implementation
        route("/players") {
            get {
                val players = PlayerRepository.allPlayers()
                call.respond(players)
            }

            get("/byPseudo/{playerPseudo}") {
                val pseudo = call.parameters["playerPseudo"]
                if (pseudo == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val player = PlayerRepository.playerByPseudo(pseudo)
                if (player == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(player)
            }
            get("/byPriority/{priority}") {
                val priorityAsText = call.parameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val players = PlayerRepository.playersByPriority(priority)

                    if (players.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(players)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            //add the following new route
            post {
                try {
                    val player = call.receive<Player>()
                    PlayerRepository.addPlayer(player)
                    call.respond(HttpStatusCode.Created)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{playerName}") {
                val name = call.parameters["playerName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                if (PlayerRepository.removePlayer(name)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}*/
