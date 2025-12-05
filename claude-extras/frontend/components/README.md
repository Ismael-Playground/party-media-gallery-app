# Componentes UI - Compose

Catalogo de componentes reutilizables.

## Buttons

### PrimaryButton

Boton principal para acciones destacadas.

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
)
```

**Uso:**
```kotlin
PrimaryButton(
    text = "Continuar",
    onClick = { viewModel.submit() },
    loading = state.isLoading
)
```

### SecondaryButton

Boton secundario con borde.

```kotlin
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
)
```

### TextButton

Boton de solo texto.

```kotlin
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

---

## Inputs

### AppTextField

Campo de texto con validacion.

```kotlin
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
)
```

**Uso:**
```kotlin
AppTextField(
    value = email,
    onValueChange = { email = it },
    label = "Email",
    error = emailError,
    keyboardType = KeyboardType.Email
)
```

### PasswordTextField

Campo para contrasena con toggle de visibilidad.

```kotlin
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null
)
```

---

## Cards

### ElevatedCard

Card con elevacion y sombra.

```kotlin
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
)
```

### ProductCard

Card para mostrar producto.

```kotlin
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Uso:**
```kotlin
ProductCard(
    product = product,
    onClick = { navigator.push(ProductDetail(product.id)) }
)
```

---

## Feedback

### LoadingContent

Indicador de carga centrado.

```kotlin
@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
)
```

### ErrorContent

Estado de error con retry.

```kotlin
@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
)
```

### EmptyContent

Estado vacio con icono y mensaje.

```kotlin
@Composable
fun EmptyContent(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
)
```

---

## Layout

### AppScaffold

Scaffold con configuracion de app.

```kotlin
@Composable
fun AppScaffold(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
)
```

### ResponsiveLayout

Layout adaptativo segun tamano de pantalla.

```kotlin
@Composable
fun ResponsiveLayout(
    compactContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
)
```

---

## Images

### OptimizedImage

Imagen con caching y placeholders.

```kotlin
@Composable
fun OptimizedImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
)
```

---

## Checklist para Nuevos Componentes

- [ ] Props documentados
- [ ] Modifier como primer opcional
- [ ] Preview function
- [ ] Dark mode compatible
- [ ] Accesibilidad (contentDescription)
- [ ] Estados manejados (loading, error, disabled)
