package com.di

import com.db.DynamoDbClientFactory
import com.model.DynamoPlayerRepository
import com.model.PlayerRepository
import io.ktor.server.application.ApplicationEnvironment
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

fun playerModule(environment: ApplicationEnvironment) = module {
    single<DynamoDbClient> { DynamoDbClientFactory.create(environment) }
    single<PlayerRepository> { DynamoPlayerRepository(get()) }
    //single<PlayerRepository> { InMemoryPlayerRepository() }
}