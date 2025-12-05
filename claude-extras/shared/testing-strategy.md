# Testing Strategy

Estrategia de testing para proyectos Kotlin.

## Piramide de Testing

```
          ┌───────────┐
          │    E2E    │     5%
          │  (Manual) │
          ├───────────┤
          │Integration│    20%
          │   Tests   │
          ├───────────┤
          │   Unit    │    75%
          │   Tests   │
          └───────────┘
```

## Stack de Testing

### Backend

| Herramienta | Version | Uso |
|-------------|---------|-----|
| Kotest | 5.8+ | Test framework |
| MockK | 1.13+ | Mocking |
| Ktor Test | 2.3+ | HTTP testing |
| Testcontainers | 1.19+ | DB testing |
| Kover | 0.7+ | Coverage |

### Frontend

| Herramienta | Version | Uso |
|-------------|---------|-----|
| Compose Test | 1.5+ | UI testing |
| Kotest | 5.8+ | Assertions |
| MockK | 1.13+ | Mocking |
| Turbine | 1.0+ | Flow testing |

## Unit Tests

### Backend - Service Test

```kotlin
class AuthServiceTest : FunSpec({

    val userRepository = mockk<UserRepository>()
    val tokenService = mockk<TokenService>()
    val authService = AuthService(userRepository, tokenService)

    beforeTest {
        clearMocks(userRepository, tokenService)
    }

    context("login") {
        test("returns token for valid credentials") {
            // Arrange
            val user = testUser()
            coEvery { userRepository.findByUsername("testuser") } returns user
            coEvery { tokenService.generateToken(user) } returns "jwt-token"

            // Act
            val result = authService.login(LoginRequest("testuser", "correct"))

            // Assert
            result.isSuccess shouldBe true
            result.getOrNull()?.token shouldBe "jwt-token"
        }

        test("fails for invalid password") {
            val user = testUser()
            coEvery { userRepository.findByUsername("testuser") } returns user

            val result = authService.login(LoginRequest("testuser", "wrong"))

            result.isFailure shouldBe true
        }

        test("fails for non-existent user") {
            coEvery { userRepository.findByUsername("unknown") } returns null

            val result = authService.login(LoginRequest("unknown", "password"))

            result.isFailure shouldBe true
        }
    }
})
```

### Frontend - ViewModel Test

```kotlin
class HomeViewModelTest {

    @Test
    fun `loadData updates state with data`() = runTest {
        val authRepo = mockk<AuthRepository>()
        val productsRepo = mockk<ProductsRepository>()

        coEvery { authRepo.getCurrentUser() } returns User(name = "Test")
        coEvery { productsRepo.getProducts() } returns listOf(testProduct())

        val viewModel = HomeViewModel(authRepo, productsRepo)

        viewModel.state.test {
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertEquals("Test", loaded.user?.name)
            assertEquals(1, loaded.products.size)
        }
    }
}
```

## Integration Tests

### Backend - HTTP Test

```kotlin
class AuthRoutesTest : FunSpec({

    test("POST /auth/v1/login returns token") {
        testApplication {
            application {
                configurePlugins()
                configureDI()
                configureRouting()
            }

            val response = client.post("/auth/v1/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "testuser", "password": "password"}""")
            }

            response.status shouldBe HttpStatusCode.OK
            val body = response.body<LoginResponse>()
            body.token.shouldNotBeEmpty()
        }
    }
})
```

### Backend - Database Test

```kotlin
class UserRepositoryTest : FunSpec({

    val postgres = PostgreSQLContainer("postgres:16")

    beforeSpec {
        postgres.start()
        Database.connect(postgres.jdbcUrl, postgres.username, postgres.password)
        transaction { SchemaUtils.create(Users) }
    }

    afterSpec { postgres.stop() }

    afterTest {
        transaction { Users.deleteAll() }
    }

    test("create and find user") {
        val repo = UserRepository()

        val created = repo.create(CreateUserDto(
            username = "test",
            email = "test@test.com"
        ))

        val found = repo.findById(created.id)

        found shouldNotBe null
        found?.username shouldBe "test"
    }
})
```

### Frontend - UI Test

```kotlin
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shows login form`() {
        composeTestRule.setContent {
            AppTheme { LoginScreen() }
        }

        composeTestRule
            .onNodeWithText("Email")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Contrasena")
            .assertIsDisplayed()
    }

    @Test
    fun `shows error on invalid login`() {
        val viewModel = mockk<LoginViewModel>()
        every { viewModel.state } returns MutableStateFlow(
            LoginState(error = "Credenciales invalidas")
        )

        composeTestRule.setContent {
            LoginScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithText("Credenciales invalidas")
            .assertIsDisplayed()
    }
}
```

## Coverage Goals

| Tipo | Objetivo |
|------|----------|
| Statements | 80%+ |
| Branches | 70%+ |
| Functions | 80%+ |
| Critical paths | 100% |

## Test Naming Convention

```kotlin
// Pattern: should_ExpectedBehavior_when_Condition
test("should return token when credentials are valid")
test("should fail when password is incorrect")
test("should throw NotFoundException when user doesn't exist")

// Or: behavior description
test("returns token for valid credentials")
test("fails for invalid password")
```

## Mocking Patterns

```kotlin
// Suspend functions
coEvery { repository.findById(any()) } returns user

// Returning different values
coEvery { repository.findById(id1) } returns user1
coEvery { repository.findById(id2) } returns user2

// Throwing exceptions
coEvery { repository.save(any()) } throws DatabaseException("Error")

// Verify calls
coVerify { repository.save(any()) }
coVerify(exactly = 1) { emailService.send(any()) }

// Capture arguments
val slot = slot<User>()
coEvery { repository.save(capture(slot)) } returns user
// slot.captured contains the argument
```

## Comandos

```bash
# Todos los tests
./gradlew test

# Tests especificos
./gradlew test --tests "AuthServiceTest"

# Con coverage
./gradlew koverReport

# Abrir reporte
open build/reports/kover/html/index.html

# UI tests Android
./gradlew connectedAndroidTest
```

## CI Integration

```yaml
test:
  runs-on: ubuntu-latest
  services:
    postgres:
      image: postgres:16
      env:
        POSTGRES_DB: test
        POSTGRES_USER: test
        POSTGRES_PASSWORD: test
      ports:
        - 5432:5432

  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Run tests
      run: ./gradlew test

    - name: Upload coverage
      uses: codecov/codecov-action@v4
```
