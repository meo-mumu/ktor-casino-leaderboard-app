package com

import com.db.configureDynamoDb
import com.di.configureDependencyInjection
import com.routing.configurePlayersRoutes
import com.serialization.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDependencyInjection()
    configureDynamoDb()
    configurePlayersRoutes()
}
