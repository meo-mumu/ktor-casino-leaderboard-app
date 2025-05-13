package com.db

import io.ktor.server.application.*
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.regions.Region

import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException
import java.net.URI

private val log = LoggerFactory.getLogger("App")

const val DYNAMO_SERVER_URL = "http://localhost:8000"
const val AWS_ACCESS_KEY_ID = "accessKey"
const val AWS_SECRET_ACCESS_KEY = "secretKey"
const val POINTS_GSI_NAME = "PointsIndex"

object DynamoDbClientFactory {
    fun create(): DynamoDbClient {
        log.info("Creation client DynamoDb on  $DYNAMO_SERVER_URL")
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(DYNAMO_SERVER_URL))
            .region(Region.EU_WEST_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
                )
            )
            .build()
    }
}

fun ensurePlayersTable(client: DynamoDbClient, tableName: String = "Players") {
    try {
        client.describeTable(
            DescribeTableRequest.builder().tableName(tableName).build()
        )
        log.info("Table $tableName already exists")
    } catch (e: ResourceNotFoundException) {
        log.info("Table $tableName does not exist, creating table...")
        createPlayersTable(client)
        log.info("Table $tableName creation successful")
    }
}

fun createPlayersTable(client: DynamoDbClient) {
    log.info("Create table Players with GSI (tournament, points)...")
    val request = CreateTableRequest.builder()
        .tableName("Players")
        .keySchema(
            KeySchemaElement.builder()
                .attributeName("pseudo")
                .keyType(KeyType.HASH)
                .build()
        )
        .attributeDefinitions(
            AttributeDefinition.builder()
                .attributeName("pseudo")
                .attributeType(ScalarAttributeType.S)
                .build(),
            AttributeDefinition.builder()
                .attributeName("tournament")
                .attributeType(ScalarAttributeType.S)
                .build(),
            AttributeDefinition.builder()
                .attributeName("points")
                .attributeType(ScalarAttributeType.N)
                .build()
        )
        .provisionedThroughput(
            ProvisionedThroughput.builder()
                .readCapacityUnits(5)
                .writeCapacityUnits(5)
                .build()
        )
        .globalSecondaryIndexes(
            GlobalSecondaryIndex.builder()
                .indexName(POINTS_GSI_NAME)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("tournament")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("points")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .projection(
                    Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build()
                )
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(5)
                        .writeCapacityUnits(5)
                        .build()
                )
                .build()
        )
        .build()

    client.createTable(request)
}

fun Application.configureDynamoDb() {
    val dynamoClient by inject<DynamoDbClient>()
    log.info("DynamoDbClient injected: ${dynamoClient::class.simpleName}")
    log.info("Configuration DynamoDB launched")
    ensurePlayersTable(dynamoClient)
    log.info("Configuration DynamoDB end")
}