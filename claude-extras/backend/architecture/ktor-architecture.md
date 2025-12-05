# Arquitectura Ktor

Documentacion de la arquitectura del backend con Ktor.

## Diagrama de Capas

```
┌─────────────────────────────────────────────────┐
│                    Client                        │
└─────────────────────┬───────────────────────────┘
                      │ HTTP
┌─────────────────────▼───────────────────────────┐
│                Ktor Plugins                      │
│  (CORS, Auth, RateLimit, ContentNegotiation)    │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│                   Routes                         │
│         (Definicion de endpoints)               │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│                Controllers                       │
│    (HTTP handling, validation, serialization)   │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│                  Services                        │
│            (Business logic)                     │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│               Repositories                       │
│          (Data access layer)                    │
└─────────────────────┬───────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────┐
│            PostgreSQL + Redis                    │
└─────────────────────────────────────────────────┘
```

## Flujo de Request

```
1. HTTP Request llega a Ktor
2. Plugins procesan (CORS, Auth, etc.)
3. Route matchea y delega a Controller
4. Controller valida input
5. Controller llama a Service
6. Service ejecuta logica de negocio
7. Service usa Repository para datos
8. Repository ejecuta query con Exposed
9. Respuesta sube por la cadena
10. Controller serializa y responde
```

## Application Entry Point

```kotlin
fun main() {
    embeddedServer(Netty, port = 8787) {
        configurePlugins()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}

fun Application.configurePlugins() {
    install(ContentNegotiation) { json() }
    install(CORS) { /* config */ }
    install(RateLimit) { /* config */ }
    install(StatusPages) { /* error handling */ }
}

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("jwt") { /* config */ }
    }
}

fun Application.configureRouting() {
    val authController: AuthController by inject()
    val userController: UserController by inject()

    routing {
        authRoutes(authController)
        authenticate("jwt") {
            userRoutes(userController)
        }
    }
}
```

## Dependency Injection (Koin)

```kotlin
val appModule = module {
    // Config
    single { DatabaseConfig(get()) }
    single { JwtConfig(get()) }

    // Repositories
    single { UserRepository() }
    single { ProductRepository() }

    // Services
    single { AuthService(get(), get()) }
    single { UserService(get()) }
    single { ProductService(get()) }

    // Controllers
    single { AuthController(get()) }
    single { UserController(get()) }
}

fun Application.configureDI() {
    install(Koin) {
        modules(appModule)
    }
}
```

## Route Definition

```kotlin
fun Route.authRoutes(authController: AuthController) {
    route("/auth/v1") {
        post("/login") { authController.login(call) }
        post("/register") { authController.register(call) }

        authenticate("jwt") {
            get("/verify") { authController.verify(call) }
            post("/refresh") { authController.refresh(call) }
        }
    }
}
```

## Controller Pattern

```kotlin
class AuthController(private val authService: AuthService) {

    suspend fun login(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()

        // Validation
        LoginValidator.validate(request).onFailure {
            call.respond(HttpStatusCode.BadRequest, ApiError(it.message))
            return
        }

        // Service call
        authService.login(request).fold(
            onSuccess = { response ->
                call.response.cookies.append(
                    Cookie("token", response.token, httpOnly = true)
                )
                call.respond(response)
            },
            onFailure = { error ->
                val status = when (error) {
                    is AuthException -> HttpStatusCode.Unauthorized
                    else -> HttpStatusCode.InternalServerError
                }
                call.respond(status, ApiError(error.message))
            }
        )
    }
}
```

## Service Pattern

```kotlin
class AuthService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        val user = userRepository.findByUsername(request.username)
            ?: return Result.failure(AuthException("Usuario no encontrado"))

        if (!BCrypt.verify(request.password, user.passwordHash)) {
            return Result.failure(AuthException("Contrasena incorrecta"))
        }

        val token = tokenService.generateToken(user)
        return Result.success(LoginResponse(
            token = token,
            user = user.toDto()
        ))
    }
}
```

## Repository Pattern

```kotlin
class UserRepository {

    suspend fun findByUsername(username: String): User? = dbQuery {
        Users.select { Users.username eq username }
            .singleOrNull()
            ?.toUser()
    }

    suspend fun create(dto: CreateUserDto): User = dbQuery {
        val id = Users.insertAndGetId {
            it[username] = dto.username
            it[email] = dto.email
            it[passwordHash] = dto.passwordHash
            it[name] = dto.name
        }
        findById(id.value)!!
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
```

## Error Handling

```kotlin
install(StatusPages) {
    exception<ValidationException> { call, cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            ApiError(cause.message, cause.errors)
        )
    }

    exception<AuthException> { call, cause ->
        call.respond(
            HttpStatusCode.Unauthorized,
            ApiError(cause.message)
        )
    }

    exception<NotFoundException> { call, cause ->
        call.respond(
            HttpStatusCode.NotFound,
            ApiError(cause.message)
        )
    }

    exception<Throwable> { call, cause ->
        logger.error("Unhandled exception", cause)
        call.respond(
            HttpStatusCode.InternalServerError,
            ApiError("Error interno del servidor")
        )
    }
}
```

## JWT Authentication

```kotlin
fun Application.configureJWT() {
    val config = environment.config.config("jwt")
    val secret = config.property("secret").getString()
    val issuer = config.property("issuer").getString()
    val audience = config.property("audience").getString()

    install(Authentication) {
        jwt("jwt") {
            realm = "ktor-app"
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (userId != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}
```

## Database Configuration

```kotlin
object DatabaseConfig {
    fun init(config: ApplicationConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.property("database.url").getString()
            driverClassName = "org.postgresql.Driver"
            username = config.property("database.user").getString()
            password = config.property("database.password").getString()
            maximumPoolSize = 10
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        // Flyway migrations
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()
    }
}
```

## Testing Architecture

```kotlin
class AuthServiceTest : FunSpec({
    val userRepository = mockk<UserRepository>()
    val tokenService = mockk<TokenService>()
    val authService = AuthService(userRepository, tokenService)

    beforeTest { clearMocks(userRepository, tokenService) }

    context("login") {
        test("returns token for valid credentials") {
            // Arrange, Act, Assert
        }
    }
})
```
