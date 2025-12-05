# Party Gallery - Documentacion de Agentes

Documentacion y configuracion de agentes Claude para Party Gallery App (Kotlin Multiplatform).

## Proposito

Este repositorio contiene:
- **20 agentes Claude especializados** para desarrollo Kotlin Multiplatform
- **8 comandos slash** para automatizacion
- **Documentacion tecnica** para frontend (Compose) y backend (Firebase)
- **Guias y principios** (SOLID, Clean Code, TDD)

## Stack de Party Gallery

| Capa | Tecnologia |
|------|------------|
| Frontend | Compose Multiplatform 1.5 |
| Backend | Firebase (Auth, Firestore, Storage, FCM) |
| Navigation | Voyager 1.0 |
| DI | Koin 3.5 |
| Networking | Ktor Client 2.3 |
| Local DB | SQLDelight 2.0 |
| Testing | Kotest, MockK, Compose Test |
| Build | Gradle 8.x + Kotlin DSL |

## Estructura

```
claude-extras/
├── .claude/
│   ├── agents/
│   │   ├── backend/           # Agentes para Firebase/Ktor
│   │   └── frontend/          # Agentes para Compose
│   └── commands/              # 8 slash commands
├── backend/                   # Docs Firebase/API
│   ├── api/
│   ├── architecture/
│   └── guides/
├── frontend/                  # Docs Compose
│   ├── components/
│   ├── design/
│   └── guides/
├── shared/                    # Docs Compartidas
│   ├── guides/
│   └── sprints/
├── CLAUDE.md                  # Router principal
└── README.md                  # Este archivo
```

## Agentes Disponibles

### Backend

| Agente | Descripcion |
|--------|-------------|
| `ktor-developer` | APIs REST con Ktor Client |
| `exposed-engineer` | SQLDelight para cache local |
| `security-analyzer` | Seguridad y Firebase Rules |
| `kotest-agent` | TDD con Kotest/MockK |
| `code-reviewer` | Review de codigo |
| `debugger` | Debugging |
| `devops` | CI/CD, GitHub Actions |
| `documentation-updater` | Documentacion |
| `integration-engineer` | Firebase, APIs externas |
| `SOLID-CLEAN-CODE` | Principios |

### Frontend

| Agente | Descripcion |
|--------|-------------|
| `compose-developer` | Compose Multiplatform, MVI |
| `ui-designer` | Material 3, theming |
| `accessibility-checker` | Accesibilidad |
| `compose-test-agent` | Testing UI |
| `code-reviewer` | Review de codigo |
| `performance-optimizer` | Performance |
| `devops` | CI/CD |
| `documentation-updater` | Documentacion |
| `multiplatform-engineer` | iOS/Android/Desktop/Web |
| `SOLID-CLEAN-CODE` | Principios |

## Comandos Slash

| Comando | Uso |
|---------|-----|
| `/test` | Ejecutar tests |
| `/review` | Code review |
| `/ticket` | Crear ticket GitHub |
| `/deploy-check` | Verificar pre-deploy |
| `/fix-lint` | Arreglar linting |
| `/sync-branches` | Sincronizar branches |
| `/make-releases` | Crear releases |
| `/analyze` | Analizar codigo |

## Integracion con Proyecto Principal

Los agentes de `.claude/` en el proyecto principal estan adaptados especificamente para Party Gallery:

```
party-media-gallery-app/
├── .claude/                   # Agentes adaptados
│   ├── ORCHESTRATION.md       # Sistema de orquestacion
│   ├── SPRINT_PLAN.md         # Plan de sprints
│   ├── agents/                # Agentes especificos
│   └── commands/              # Comandos
└── claude-extras/             # Este directorio (referencia)
```

## GitHub Project

**URL:** https://github.com/orgs/Ismael-Playground/projects/1

Todos los tickets se asignan a este proyecto.

## Uso

1. **Leer CLAUDE.md** para entender el routing de tareas
2. **Invocar agente** segun el tipo de tarea
3. **Seguir SPRINT_PLAN.md** para tickets
4. **Usar comandos slash** para automatizacion

---

*Party Gallery App - Documentacion de Agentes*
*Actualizado: 2025-12-04*
