package com.partygallery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Party Gallery Design System - Theme
 *
 * Dark Mode First approach - dark theme is the primary design,
 * light theme is provided as an alternative.
 */

// ============================================
// COLOR SCHEME
// ============================================

data class PartyGalleryColorScheme(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val onBackground: Color,
    val onBackgroundVariant: Color,
    val onBackgroundDisabled: Color,
    val onPrimary: Color,
    val error: Color,
    val errorVariant: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val border: Color,
    val divider: Color,
    val scrim: Color,
    val isDark: Boolean,
)

val DarkColorScheme = PartyGalleryColorScheme(
    primary = PartyGalleryColors.Primary,
    secondary = PartyGalleryColors.Secondary,
    tertiary = PartyGalleryColors.Tertiary,
    background = PartyGalleryColors.DarkBackground,
    surface = PartyGalleryColors.DarkSurface,
    surfaceVariant = PartyGalleryColors.DarkSurfaceVariant,
    surfaceContainer = PartyGalleryColors.DarkSurfaceContainer,
    surfaceContainerHigh = PartyGalleryColors.DarkSurfaceContainerHigh,
    onBackground = PartyGalleryColors.DarkOnBackground,
    onBackgroundVariant = PartyGalleryColors.DarkOnBackgroundVariant,
    onBackgroundDisabled = PartyGalleryColors.DarkOnBackgroundDisabled,
    onPrimary = PartyGalleryColors.OnPrimary,
    error = PartyGalleryColors.Error,
    errorVariant = PartyGalleryColors.ErrorVariant,
    success = PartyGalleryColors.Success,
    warning = PartyGalleryColors.Warning,
    info = PartyGalleryColors.Info,
    border = PartyGalleryColors.DarkBorder,
    divider = PartyGalleryColors.DarkDivider,
    scrim = PartyGalleryColors.Scrim,
    isDark = true,
)

val LightColorScheme = PartyGalleryColorScheme(
    primary = PartyGalleryColors.Primary,
    secondary = PartyGalleryColors.Secondary,
    tertiary = PartyGalleryColors.Tertiary,
    background = PartyGalleryColors.LightBackground,
    surface = PartyGalleryColors.LightSurface,
    surfaceVariant = PartyGalleryColors.LightSurfaceVariant,
    surfaceContainer = PartyGalleryColors.LightSurfaceContainer,
    surfaceContainerHigh = PartyGalleryColors.LightSurfaceVariant,
    onBackground = PartyGalleryColors.LightOnBackground,
    onBackgroundVariant = PartyGalleryColors.LightOnBackgroundVariant,
    onBackgroundDisabled = PartyGalleryColors.LightOnBackgroundDisabled,
    onPrimary = PartyGalleryColors.OnPrimary,
    error = PartyGalleryColors.Error,
    errorVariant = PartyGalleryColors.ErrorVariant,
    success = PartyGalleryColors.Success,
    warning = PartyGalleryColors.Warning,
    info = PartyGalleryColors.Info,
    border = PartyGalleryColors.LightBorder,
    divider = PartyGalleryColors.LightDivider,
    scrim = PartyGalleryColors.ScrimLight,
    isDark = false,
)

// ============================================
// COMPOSITION LOCALS
// ============================================

val LocalPartyGalleryColors = staticCompositionLocalOf { DarkColorScheme }
val LocalPartyGalleryTypography = staticCompositionLocalOf { PartyGalleryTypography }
val LocalPartyGalleryShapes = staticCompositionLocalOf { PartyGalleryShapes }
val LocalPartyGallerySpacing = staticCompositionLocalOf { PartyGallerySpacing }

// ============================================
// THEME COMPOSABLE
// ============================================

/**
 * Party Gallery Theme
 *
 * @param darkTheme Whether to use dark theme. Defaults to system preference.
 * @param content The content to be themed.
 */
@Composable
fun PartyGalleryTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalPartyGalleryColors provides colorScheme,
        LocalPartyGalleryTypography provides PartyGalleryTypography,
        LocalPartyGalleryShapes provides PartyGalleryShapes,
        LocalPartyGallerySpacing provides PartyGallerySpacing,
        content = content,
    )
}

// ============================================
// THEME ACCESSOR OBJECT
// ============================================

/**
 * Accessor for theme values.
 *
 * Usage:
 * ```
 * PartyGalleryTheme {
 *     Box(
 *         modifier = Modifier.background(Theme.colors.background)
 *     ) {
 *         Text(
 *             text = "Hello",
 *             style = Theme.typography.titleLarge,
 *             color = Theme.colors.onBackground
 *         )
 *     }
 * }
 * ```
 */
object Theme {
    val colors: PartyGalleryColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalPartyGalleryColors.current

    val typography: PartyGalleryTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPartyGalleryTypography.current

    val shapes: PartyGalleryShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalPartyGalleryShapes.current

    val spacing: PartyGallerySpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalPartyGallerySpacing.current
}
