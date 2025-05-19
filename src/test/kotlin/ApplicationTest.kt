package com

import com.model.Player
import com.model.PlayerRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.Test
import kotlin.test.assertEquals


suspend inline fun <reified T> HttpResponse.deserialize(): T =
    Json.decodeFromString(bodyAsText())

class ApplicationTest : KoinTest {
    companion object {
        private val playerRepo = mockk<PlayerRepository>(relaxed = true)

        @JvmField
        val koinExtension = KoinTestExtension.Companion.create {
            modules(
                module {
                    single<PlayerRepository> { playerRepo }
                }
            )
        }
    }

    @BeforeEach
    fun before() {
        clearAllMocks()
    }

    @AfterEach
    fun after() {
        stopKoin()
    }

    @Test
    fun `get player after adding returns correct data`() = testApplication {
        client.delete("/players")
        client.post("/players") { contentType(ContentType.Application.Json); setBody("""{"pseudo":"bob"}""") }
        client.put("/players/bob/points") { contentType(ContentType.Application.Json); setBody("""{"points":42}""") }
        val response = client.get("/players/bob")
        assertEquals(HttpStatusCode.OK, response.status)
        val player = response.deserialize<Player>()
        assertEquals("bob", player.pseudo)
        assertEquals(42, player.points)
        assertEquals(1, player.rank)
    }

    @Test
    fun `get all players returns sorted by rank`() = testApplication {
        client.delete("/players")
        client.post("/players") { contentType(ContentType.Application.Json); setBody("""{"pseudo":"alice"}""") }
        client.post("/players") { contentType(ContentType.Application.Json); setBody("""{"pseudo":"bob"}""") }
        client.post("/players") { contentType(ContentType.Application.Json); setBody("""{"pseudo":"carol"}""") }

        client.put("/players/alice/points") { contentType(ContentType.Application.Json); setBody("""{"points":10}""") }
        client.put("/players/bob/points") { contentType(ContentType.Application.Json); setBody("""{"points":30}""") }
        client.put("/players/carol/points") { contentType(ContentType.Application.Json); setBody("""{"points":20}""") }

        val response = client.get("/players")
        assertEquals(HttpStatusCode.OK, response.status)
        val players = response.deserialize<List<Player>>()
        assertEquals(listOf("bob", "carol", "alice"), players.map { it.pseudo })
        assertEquals(listOf(1, 2, 3), players.map { it.rank })
    }


    @Test
    fun `cannot add player with pseudo too short`() = testApplication {
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"pseudo":"ab"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `cannot add duplicate player`() = testApplication {
        client.delete("/players")
        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"pseudo":"uniqueuser"}""")
        }
        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"pseudo":"uniqueuser"}""")
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun `get non-existent player returns NotFound`() = testApplication {
        client.delete("/players")
        val response = client.get("/players/inconnu")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `cannot update points for deleted player`() = testApplication {
        client.delete("/players")
        client.post("/players") { contentType(ContentType.Application.Json); setBody("""{"pseudo":"ghost"}""") }
        client.delete("/players")
        val response = client.put("/players/ghost/points") {
            contentType(ContentType.Application.Json)
            setBody("""{"points":10}""")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `cannot add points with negative value`() = testApplication {
        client.delete("/players")
        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"pseudo":"alice"}""")
        }
        val response = client.put("/players/alice/points") {
            contentType(ContentType.Application.Json)
            setBody("""{"points":-5}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }


}