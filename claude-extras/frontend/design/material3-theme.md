# Material 3 Theme - Party Gallery Design System

Sistema de diseño **Dark Mode First** basado en Material Design 3 con estetica social moderna.

## Filosofia de Diseño

Party Gallery utiliza un diseño **oscuro inmersivo** que:
- Resalta el contenido visual (fotos/videos)
- Reduce fatiga visual en ambientes de fiesta
- Crea una estetica premium y moderna
- Optimiza para visualizacion nocturna

---

## Paleta de Colores

### Dark Theme (Principal)

| Token | Valor | Uso |
|-------|-------|-----|
| `background` | #0A0A0A | Fondo principal de la app |
| `surface` | #141414 | Cards, contenedores |
| `surfaceVariant` | #1E1E1E | Cards elevadas, inputs |
| `surfaceContainer` | #252525 | Contenedores secundarios |
| `primary` | #F59E0B | Acciones principales, acentos |
| `onPrimary` | #000000 | Texto sobre primary |
| `primaryContainer` | #422006 | Contenedores highlight |
| `secondary` | #FBBF24 | Acciones secundarias, badges |
| `tertiary` | #FB923C | Acentos terciarios |
| `onSurface` | #FFFFFF | Texto principal |
| `onSurfaceVariant` | #A1A1AA | Texto secundario |
| `outline` | #3F3F46 | Bordes, divisores |
| `outlineVariant` | #27272A | Bordes sutiles |
| `error` | #EF4444 | Estados de error |
| `success` | #22C55E | Estados de exito |
| `warning` | #FBBF24 | Advertencias |

### Light Theme (Alternativo)

| Token | Valor | Uso |
|-------|-------|-----|
| `background` | #FAFAFA | Fondo principal |
| `surface` | #FFFFFF | Cards, contenedores |
| `surfaceVariant` | #F4F4F5 | Cards elevadas |
| `primary` | #D97706 | Acciones principales |
| `onPrimary` | #FFFFFF | Texto sobre primary |
| `onSurface` | #18181B | Texto principal |
| `onSurfaceVariant` | #71717A | Texto secundario |
| `outline` | #E4E4E7 | Bordes |

### Semantic Colors

| Estado | Dark Mode | Light Mode | Uso |
|--------|-----------|------------|-----|
| Success | #22C55E | #16A34A | Confirmaciones, uploads |
| Warning | #FBBF24 | #D97706 | Atención requerida |
| Error | #EF4444 | #DC2626 | Errores, destructivo |
| Info | #3B82F6 | #2563EB | Información neutral |
| Live | #EF4444 | #DC2626 | Indicador de party live |

### Gradientes

```
AMBER GLOW (Primary Accent)
Linear: 135deg
From: #F59E0B
To: #D97706
Usage: Botones principales, highlights

SUNSET GRADIENT
Linear: 135deg
From: #F59E0B
Via: #FB923C
To: #F97316
Usage: Avatares destacados, badges premium

DARK FADE (Para overlays)
Linear: 180deg
From: transparent 0%
Via: rgba(10, 10, 10, 0.6) 50%
To: rgba(10, 10, 10, 0.95) 100%
Usage: Overlay sobre imagenes con texto

CARD GLOW
Radial: circle at center
From: rgba(245, 158, 11, 0.15) 0%
To: transparent 70%
Usage: Hover states, focus
```

---

## Tipografia

### Font Stack

```
PRIMARY: Inter
- Pesos: 400 (Regular), 500 (Medium), 600 (SemiBold), 700 (Bold)
- Uso: Todo el UI

DISPLAY: Space Grotesk (opcional)
- Pesos: 500, 700
- Uso: Headlines de marketing, titulos hero
```

### Type Scale

| Style | Size | Weight | Line Height | Uso |
|-------|------|--------|-------------|-----|
| `displayLarge` | 48sp | 700 | 56sp | Hero titles |
| `displayMedium` | 36sp | 700 | 44sp | Section headers |
| `displaySmall` | 30sp | 600 | 38sp | Feature titles |
| `headlineLarge` | 24sp | 700 | 32sp | Screen titles |
| `headlineMedium` | 20sp | 600 | 28sp | Card titles grandes |
| `headlineSmall` | 18sp | 600 | 26sp | Section titles |
| `titleLarge` | 18sp | 600 | 26sp | App bar titles |
| `titleMedium` | 16sp | 600 | 24sp | Card titles |
| `titleSmall` | 14sp | 600 | 20sp | Subtitles |
| `bodyLarge` | 16sp | 400 | 24sp | Texto principal |
| `bodyMedium` | 14sp | 400 | 20sp | Texto secundario |
| `bodySmall` | 12sp | 400 | 16sp | Captions, timestamps |
| `labelLarge` | 14sp | 500 | 20sp | Botones |
| `labelMedium` | 12sp | 500 | 16sp | Chips, tabs |
| `labelSmall` | 11sp | 500 | 16sp | Badges, counters |

---

## Espaciado

| Token | Valor | Uso |
|-------|-------|-----|
| `xxs` | 2dp | Espaciado minimo |
| `xs` | 4dp | Entre elementos muy relacionados |
| `sm` | 8dp | Entre elementos relacionados |
| `md` | 12dp | Padding interno de cards |
| `lg` | 16dp | Padding estandar |
| `xl` | 20dp | Entre secciones |
| `2xl` | 24dp | Margenes de pantalla |
| `3xl` | 32dp | Separacion de secciones |
| `4xl` | 48dp | Espaciado grande |

---

## Formas (Border Radius)

| Token | Radius | Uso |
|-------|--------|-----|
| `none` | 0dp | Elementos sin radius |
| `xs` | 4dp | Badges pequeños |
| `sm` | 8dp | Chips, pequeños botones |
| `md` | 12dp | Botones, inputs |
| `lg` | 16dp | Cards estandar |
| `xl` | 20dp | Cards grandes, modals |
| `2xl` | 24dp | Bottom sheets |
| `full` | 9999dp | Avatares, pills |

---

## Componentes

### Cards de Media (Estilo Principal)

```kotlin
// MediaCard - Card con imagen y overlay
Surface(
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 10f),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface
) {
    Box {
        // Imagen de fondo
        AsyncImage(
            model = imageUrl,
            contentScale = ContentScale.Crop
        )

        // Gradient overlay en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xF00A0A0A)
                        )
                    )
                )
        )

        // User info overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Avatar(url = userAvatar, size = 32.dp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(userName, style = labelMedium, color = white)
                Text(timestamp, style = bodySmall, color = onSurfaceVariant)
            }
        }

        // Engagement metrics
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            Icon(Icons.Heart, tint = white)
            Text(likeCount, color = white)
        }
    }
}
```

### Avatar con Borde de Acento

```kotlin
@Composable
fun Avatar(
    url: String,
    size: Dp = 40.dp,
    showBorder: Boolean = false
) {
    Box(
        modifier = if (showBorder) {
            Modifier
                .size(size + 4.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF59E0B),
                            Color(0xFFFB923C)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(2.dp)
        } else Modifier.size(size)
    ) {
        AsyncImage(
            model = url,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
```

### Tabs (Pill Style)

```kotlin
@Composable
fun PillTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex
            Surface(
                onClick = { onTabSelected(index) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    Color.Transparent,
                border = if (!isSelected)
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                else null
            ) {
                Text(
                    text = tab,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### Bottom Navigation

```kotlin
@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF0A0A0A),
        contentColor = Color.White
    ) {
        listOf(
            Icons.Home to "Home",
            Icons.Search to "Search",
            Icons.Add to "Create",
            Icons.Chat to "Chat",
            Icons.Person to "Profile"
        ).forEachIndexed { index, (icon, label) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFF59E0B),
                    selectedTextColor = Color(0xFFF59E0B),
                    unselectedIconColor = Color(0xFFA1A1AA),
                    unselectedTextColor = Color(0xFFA1A1AA),
                    indicatorColor = Color(0xFF1E1E1E)
                )
            )
        }
    }
}
```

### Input Field

```kotlin
@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search..."
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1E1E1E),
            unfocusedContainerColor = Color(0xFF141414),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFFF59E0B)
        ),
        placeholder = {
            Text(placeholder, color = Color(0xFF71717A))
        },
        leadingIcon = {
            Icon(
                Icons.Search,
                contentDescription = null,
                tint = Color(0xFF71717A)
            )
        },
        singleLine = true
    )
}
```

---

## Implementacion Theme

```kotlin
// Color.kt
private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF141414),
    surfaceVariant = Color(0xFF1E1E1E),
    primary = Color(0xFFF59E0B),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF422006),
    secondary = Color(0xFFFBBF24),
    tertiary = Color(0xFFFB923C),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF3F3F46),
    outlineVariant = Color(0xFF27272A),
    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF4F4F5),
    primary = Color(0xFFD97706),
    onPrimary = Color(0xFFFFFFFF),
    onSurface = Color(0xFF18181B),
    onSurfaceVariant = Color(0xFF71717A),
    outline = Color(0xFFE4E4E7),
    error = Color(0xFFDC2626)
)

// Spacing.kt
object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
}

// Shape.kt
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

// Theme.kt
@Composable
fun PartyGalleryTheme(
    darkTheme: Boolean = true, // Dark mode por defecto
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
```

---

## Accesibilidad

### Contraste Minimo

| Tipo | Ratio Minimo | Cumplimiento |
|------|--------------|--------------|
| Texto normal sobre background | 7:1+ | WCAG AAA |
| Texto grande (18sp+) | 4.5:1+ | WCAG AA |
| Iconos y UI | 3:1+ | WCAG AA |
| Primary sobre background | 5.2:1 | WCAG AA |

### Touch Targets

- Minimo: 44dp x 44dp
- Recomendado: 48dp x 48dp
- Espaciado entre targets: 8dp minimo

### Motion

- Respetar `prefers-reduced-motion`
- Duracion maxima de animaciones: 400ms
- Evitar parpadeos rapidos

---

## Mood Tags (Party Vibes)

| Mood | Color | Emoji |
|------|-------|-------|
| HYPE | #EF4444 (Red) | 🔥 |
| CHILL | #3B82F6 (Blue) | 🌊 |
| WILD | #F59E0B (Amber) | 🎉 |
| ROMANTIC | #EC4899 (Pink) | 💕 |
| CRAZY | #8B5CF6 (Violet) | 🤪 |
| ELEGANT | #71717A (Gray) | ✨ |

---

## Party Status

| Status | Color | Indicator |
|--------|-------|-----------|
| PLANNED | #71717A | Icono reloj |
| LIVE | #EF4444 | Punto pulsante rojo |
| ENDED | #F59E0B | Icono camara |
| CANCELLED | #3F3F46 | Tachado |

---

*Party Gallery Design System v2.0 - Dark Mode First*
*Basado en mockup de MediaViewer*
