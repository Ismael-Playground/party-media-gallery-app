package com.partygallery.di

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    
    // HTTP Client
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            install(Logging) {
                level = LogLevel.INFO
            }
            
            // Platform-specific engine will be configured in platform modules
        }
    }
    
    // API Clients for different services
    single { PartyGalleryApiClient(httpClient = get()) }
    single { FirebaseApiClient(httpClient = get()) }
    
    // Third-party API clients
    single { InstagramApiClient(httpClient = get()) }
    single { TikTokApiClient(httpClient = get()) }
    single { TwitterApiClient(httpClient = get()) }
    single { FacebookApiClient(httpClient = get()) }
    single { PinterestApiClient(httpClient = get()) }
    single { SpotifyApiClient(httpClient = get()) }
    single { GoogleMapsApiClient(httpClient = get()) }
    single { AlgoliaApiClient(httpClient = get()) }
}

// API Client interfaces - to be implemented
interface PartyGalleryApiClient
interface FirebaseApiClient
interface InstagramApiClient
interface TikTokApiClient
interface TwitterApiClient
interface FacebookApiClient
interface PinterestApiClient
interface SpotifyApiClient
interface GoogleMapsApiClient
interface AlgoliaApiClient