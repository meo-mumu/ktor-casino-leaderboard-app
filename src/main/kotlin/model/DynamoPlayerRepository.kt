package com.model

import com.db.POINTS_GSI_NAME
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest

private val log = LoggerFactory.getLogger("DynamoPlayerRepository")

class DynamoPlayerRepository(
    private val client: DynamoDbClient,
    private val tableName: String = "Players"
) : PlayerRepository {

    private fun playerToItem(player: Player): Map<String, AttributeValue> =
        mapOf(
            "pseudo" to AttributeValue.builder().s(player.pseudo).build(),
            "points" to AttributeValue.builder().n(player.points.toString()).build(),
            "tournament" to AttributeValue.builder().s("global").build() //mandatory to use the query request on the GSI POINTS_GSI_NAME
        )

    private fun itemToPlayer(item: Map<String, AttributeValue>): Player =
        Player(
            pseudo = item["pseudo"]!!.s(),
            points = item["points"]!!.n().toInt()
        )

    private fun getPlayersSortedByPointsDesc(): List<Player> {
        log.info("Querying all players sorted by points (desc) using GSI")
        val queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .indexName(POINTS_GSI_NAME)
            .keyConditionExpression("tournament = :tournament")
            .expressionAttributeValues(
                mapOf(":tournament" to AttributeValue.builder().s("global").build())
            )
            .scanIndexForward(false) // Descending order
            .build()
        val items = client.query(queryRequest).items()
        return items.map { itemToPlayer(it) }
    }

    override suspend fun addPlayerByPseudo(pseudo: String) {
        // Check if player exists
        val player = Player(pseudo)
        val putRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(playerToItem(player))
            .conditionExpression("attribute_not_exists(pseudo)")
            .build()
        try {
            client.putItem(putRequest)
        } catch (e: ConditionalCheckFailedException) {
            throw IllegalStateException("Player with pseudo $pseudo already exists", e)
        }
    }

    override suspend fun updatePlayerPoints(pseudo: String, pointsToAdd: Int) {
        val key = mapOf("pseudo" to AttributeValue.builder().s(pseudo).build())
        val updateRequest = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .updateExpression("SET points = points + :inc")
            .expressionAttributeValues(
                mapOf(
                    ":inc" to AttributeValue.builder().n(pointsToAdd.toString()).build()
                )
            )
            .conditionExpression("attribute_exists(pseudo)")
            .build()
        try {
            client.updateItem(updateRequest)
        } catch (e: ConditionalCheckFailedException) {
            throw NoSuchElementException("Player with pseudo $pseudo not found")
        }
    }

    override suspend fun getAllPlayersSortedByRank(): List<Player> {
        val players = getPlayersSortedByPointsDesc()
        if (players.isEmpty()) throw NoSuchElementException("No players found")
        return players.mapIndexed { idx, player ->
            Player(player.pseudo, player.points, idx + 1)
        }
    }

    override suspend fun getPlayerWithRankByPseudo(pseudo: String): Player {
        val players = getPlayersSortedByPointsDesc()
        val player = players.find { it.pseudo.equals(pseudo, ignoreCase = true) }
            ?: throw NoSuchElementException("Player with pseudo $pseudo not found")
        val rank = players.indexOf(player) + 1
        return Player(player.pseudo, player.points, rank)
    }

    override suspend fun clearAllPlayers() {
        val scanRequest = ScanRequest.builder()
            .tableName(tableName)
            .build()
        val items = client.scan(scanRequest).items()
        for (item in items) {
            val key = mapOf("pseudo" to item["pseudo"])
            val deleteRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build()
            client.deleteItem(deleteRequest)
        }
    }
}