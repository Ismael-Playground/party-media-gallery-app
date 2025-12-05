package com.partygallery.di

import com.partygallery.data.auth.FirebaseAuthService
import com.partygallery.data.repository.AuthRepositoryImpl
import com.partygallery.domain.repository.AuthRepository
import com.partygallery.presentation.store.LoginStore
import com.partygallery.presentation.store.SignUpStore
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Koin DI Configuration for Party Gallery.
 *
 * S1-004: Configurar Koin DI
 *
 * Architecture:
 * - domainModule: Use cases and domain services
 * - dataModule: Repositories, data sources
 * - presentationModule: ViewModels/Stores
 * - platformModule: Platform-specific implementations (expect/actual)
 */

/**
 * Domain layer module.
 * Contains use cases and domain-level services.
 */
val domainModule = module {
    // Use cases will be added here as they are implemented
    // Example:
    // factory { GetUserProfileUseCase(get()) }
    // factory { LoginUseCase(get()) }
}

/**
 * Data layer module.
 * Contains repositories and data sources.
 */
val dataModule = module {
    // Firebase Auth Service (S2-001)
    single { FirebaseAuthService() }

    // Auth Repository (S2-002)
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // Other repository implementations will be added here
    // Example:
    // single<UserRepository> { UserRepositoryImpl(get(), get()) }
    // single<PartyEventRepository> { PartyEventRepositoryImpl(get()) }

    // Data sources
    // single { FirebaseDataSource(get()) }
    // single { LocalDataSource(get()) }
}

/**
 * Presentation layer module.
 * Contains ViewModels/Stores for MVI pattern.
 */
val presentationModule = module {
    // Login Store (S2-003)
    factory { LoginStore(get()) }

    // SignUp Store (S2-010)
    factory { SignUpStore(get()) }

    // Other stores will be added here
    // Example:
    // factory { HomeStore(get(), get()) }
    // factory { ProfileStore(get()) }
}

/**
 * Common modules shared across all platforms.
 */
fun commonModules(): List<Module> = listOf(
    domainModule,
    dataModule,
    presentationModule,
)

/**
 * Initialize Koin with common modules.
 * Platform-specific modules should be added via platformModule parameter.
 *
 * @param platformModule Platform-specific Koin module
 * @param appDeclaration Optional Koin app configuration
 */
fun initKoin(platformModule: Module = module { }, appDeclaration: KoinAppDeclaration = { }) = startKoin {
    appDeclaration()
    modules(
        commonModules() + platformModule,
    )
}

/**
 * Simplified init for platforms that don't need custom configuration.
 */
fun initKoin(platformModule: Module = module { }) = initKoin(platformModule) { }
