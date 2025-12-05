# Backend Documentation - Ktor

Documentacion del backend Kotlin con Ktor.

## Stack

| Tecnologia | Version | Uso |
|------------|---------|-----|
| Kotlin | 1.9+ | Lenguaje |
| Ktor Server | 2.3+ | Framework HTTP |
| Exposed | 0.45+ | ORM |
| Koin | 3.5+ | DI |
| PostgreSQL | 16 | Base de datos |
| Redis | 7+ | Cache/Rate limiting |
| Flyway | 10+ | Migraciones |

## Estructura del Proyecto

```
backend/
├── src/main/kotlin/
│   ├── Application.kt           # Entry point
│   ├── config/                  # Configuracion
│   │   ├── DatabaseConfig.kt
│   │   ├── SecurityConfig.kt
│   │   └── KoinConfig.kt
│   ├── routes/                  # Rutas HTTP
│   │   ├── AuthRoutes.kt
│   │   ├── UserRoutes.kt
│   │   └── ProductRoutes.kt
│   ├── controllers/             # Handlers HTTP
│   │   ├── AuthController.kt
│   │   └── UserController.kt
│   ├── services/                # Logica de negocio
│   │   ├── AuthService.kt
│   │   └── UserService.kt
│   ├── repositories/            # Acceso a datos
│   │   ├── UserRepository.kt
│   │   └── ProductRepository.kt
│   └── models/                  # Modelos
│       ├── entities/            # Tablas Exposed
│       ├── dto/                 # Data Transfer Objects
│       └── domain/              # Domain models
├── src/main/resources/
│   ├── application.conf         # Configuracion
│   └── db/migration/            # Flyway migrations
└── src/test/kotlin/             # Tests
```

## Arquitectura de Capas

```
Routes -> Controllers -> Services -> Repositories -> Database
```

### Route
Define endpoints HTTP usando DSL de Ktor.

### Controller
Maneja HTTP request/response, validacion, serialization.

### Service
Contiene logica de negocio, es independiente de HTTP.

### Repository
Acceso a base de datos usando Exposed.

## Documentacion Detallada

| Documento | Ruta |
|-----------|------|
| API Reference | `api/API-Reference.md` |
| Arquitectura | `architecture/ktor-architecture.md` |
| Seguridad | `guides/security.md` |
| Operaciones | `guides/operations.md` |

## Desarrollo Local

```bash
# Iniciar PostgreSQL y Redis
docker-compose up -d db redis

# Ejecutar migraciones
./gradlew flywayMigrate

# Iniciar servidor (puerto 8787)
./gradlew :backend:run

# Health check
curl http://localhost:8787/health
```

## Variables de Entorno

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/app
DATABASE_USER=postgres
DATABASE_PASSWORD=secret
REDIS_URL=redis://localhost:6379
JWT_SECRET=your-256-bit-secret
JWT_ISSUER=app
JWT_AUDIENCE=app-users
PORT=8787
```

## Testing

```bash
# Todos los tests
./gradlew :backend:test

# Test especifico
./gradlew :backend:test --tests "AuthServiceTest"

# Con cobertura
./gradlew :backend:koverReport
```
