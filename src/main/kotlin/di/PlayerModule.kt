package com.di

import com.db.DynamoDbClientFactory
import com.model.DynamoPlayerRepository
import com.model.PlayerRepository
import org.koin.dsl.module
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

val playerModule = module {
    single<DynamoDbClient> { DynamoDbClientFactory.create() }
    single<PlayerRepository> { DynamoPlayerRepository(get()) }
    //single<PlayerRepository> { InMemoryPlayerRepository() }
}