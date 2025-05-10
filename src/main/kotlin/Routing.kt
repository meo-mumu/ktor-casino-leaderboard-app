package com

//import com.model.InMemoryPlayerRepository
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
import org.koin.ktor.ext.inject

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
        //staticResources("static", "static")

        val playerRepo by inject<PlayerRepository>()
        println(playerRepo)
        //val playerRepo = InMemoryPlayerRepository()

        route("/players") {

            // Liste triée des joueurs avec classement
            get {
                val playersWithRank: List<PlayerPointsRank> = playerRepo.getAllPlayersSortedByRank()
                call.respond(playersWithRank)
            }

            // Ajouter un joueur
            post("add") {
                val request = call.receive<PlayerRequest>()
                playerRepo.addPlayerByPseudo(request.pseudo)
                call.respondText("Player ${request.pseudo} added successfully")
            }

            // Supprimer tous les joueurs
            delete {
                playerRepo.clearAllPlayers()
                call.respondText("All players removed")
            }

            // Sous-routes pour un pseudo donné
            route("{pseudo}") {

                // Récupérer infos joueur avec classement
                get {
                    val pseudo = call.parameters["pseudo"]!!
                    val playerWithRank = playerRepo.getPlayerWithRankByPseudo(pseudo)
                    call.respond(playerWithRank)
                }

                // Mettre à jour les points du joueur
                put("points") {
                    val pseudo = call.parameters["pseudo"]!!
                    val request = call.receive<UpdatePointsRequest>()
                    playerRepo.updatePlayerPoints(pseudo, request.points)
                    call.respondText("Player $pseudo updated with ${request.points} points")
                }

                post("addPoints") {
                    val pseudo = call.parameters["pseudo"]!!
                    val request = call.receive<UpdatePointsRequest>()
                    playerRepo.updatePlayerPoints(pseudo, request.points)
                    call.respondText("Player $pseudo updated with ${request.points} points")
                }
            }
        }
    }
}