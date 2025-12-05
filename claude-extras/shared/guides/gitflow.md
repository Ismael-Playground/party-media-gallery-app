# Gitflow - Flujo de Trabajo Git

Guia de flujo de trabajo Git para el proyecto.

## Ramas Principales

### main

- Codigo en produccion
- Siempre deployable
- Solo merges de develop (releases) o hotfixes
- Protegida - requiere PR y review
- Taggeada con version semantica

### develop

- Rama de integracion
- Base para features
- Siempre actualizada con main
- Protegida - requiere PR

## Ramas de Soporte

### feature/*

- Nueva funcionalidad
- Base: `develop`
- Merge a: `develop`
- Naming: `feature/123-descripcion-corta`

### hotfix/*

- Correccion urgente en produccion
- Base: `main`
- Merge a: `main` Y `develop`
- Naming: `hotfix/descripcion-corta`

### release/*

- Preparacion de release
- Base: `develop`
- Merge a: `main` Y `develop`
- Naming: `release/1.2.0`

## Flujo Visual

```
main     ●─────────●─────────────────●────────●
         │         │                 │        │
         │         │                 │        │
develop  ●──●──●───●──●──●──●──●────●──●──●───●
            │     │        │        │
            │     │        │        │
feature    [====]  │        │        │
                 [======]  │        │
                         [====]    │
                                  hotfix
```

## Comandos Comunes

### Nueva Feature

```bash
# Crear rama desde develop
git checkout develop
git pull origin develop
git checkout -b feature/123-nueva-funcionalidad

# Trabajar...
git add .
git commit -m "feat: add new feature"

# Push
git push -u origin feature/123-nueva-funcionalidad

# Crear PR a develop
gh pr create --base develop --title "feat: Nueva funcionalidad"
```

### Hotfix

```bash
# Crear desde main
git checkout main
git pull origin main
git checkout -b hotfix/fix-critical-bug

# Fix...
git add .
git commit -m "fix: critical bug"

# Push y PR a main
git push -u origin hotfix/fix-critical-bug
gh pr create --base main --title "fix: Critical bug"

# Despues de merge a main, sync develop
git checkout develop
git merge main
git push origin develop
```

### Release

```bash
# Crear release branch
git checkout develop
git pull origin develop
git checkout -b release/1.2.0

# Bump version, update changelog
vim build.gradle.kts  # version = "1.2.0"
vim CHANGELOG.md

# Commit
git add .
git commit -m "chore: bump version to 1.2.0"

# Push y PR a main
git push -u origin release/1.2.0
gh pr create --base main --title "Release 1.2.0"

# Despues de merge
git checkout main
git pull
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin --tags

# Sync develop
git checkout develop
git merge main
git push origin develop
```

## Commits Convencionales

### Formato

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### Types

| Type | Uso |
|------|-----|
| `feat` | Nueva funcionalidad |
| `fix` | Correccion de bug |
| `docs` | Documentacion |
| `style` | Formato (no afecta logica) |
| `refactor` | Refactoring |
| `perf` | Mejora de performance |
| `test` | Tests |
| `chore` | Tareas de mantenimiento |
| `ci` | CI/CD |

### Ejemplos

```bash
feat(auth): add JWT refresh token support

fix(cart): resolve race condition in checkout

docs(api): update endpoint documentation

refactor(user): extract validation to separate service

test(auth): add integration tests for login flow

chore(deps): update Ktor to 2.3.7

ci(deploy): add staging environment
```

## Pull Requests

### Template

```markdown
## Descripcion
Breve descripcion de los cambios.

## Tipo de Cambio
- [ ] Feature
- [ ] Bug fix
- [ ] Refactor
- [ ] Documentation
- [ ] CI/CD

## Checklist
- [ ] Tests agregados/actualizados
- [ ] Lint pasando
- [ ] Documentacion actualizada
- [ ] PR title sigue conventional commits

## Screenshots (si aplica)

## Testing
Instrucciones para probar los cambios.
```

### Reglas

1. Titulo sigue conventional commits
2. Descripcion clara del cambio
3. Tests incluidos
4. Lint pasando
5. Al menos 1 reviewer
6. CI verde antes de merge

## Protecciones de Rama

### main

- Require PR
- Require review (1+)
- Require status checks
- No force push
- No delete

### develop

- Require PR
- Require status checks
- No force push

## Resolucion de Conflictos

```bash
# Actualizar feature con cambios de develop
git checkout feature/mi-feature
git fetch origin
git rebase origin/develop

# Resolver conflictos si hay
# ... editar archivos ...
git add .
git rebase --continue

# Push con force (solo en feature branches)
git push --force-with-lease
```

## Tags y Versiones

```bash
# Listar tags
git tag -l

# Crear tag anotado
git tag -a v1.2.0 -m "Release v1.2.0"

# Push tags
git push origin --tags

# Ver commits desde ultimo tag
git log $(git describe --tags --abbrev=0)..HEAD --oneline
```
