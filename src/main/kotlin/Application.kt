package com

import com.db.configureDynamoDb
import com.db.createPlayersTable
import com.di.configureDependencyInjection
import com.routing.configurePlayersRoutes
import com.serialization.configureSerialization
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDependencyInjection()
    configureDynamoDb()
    configurePlayersRoutes()
}
