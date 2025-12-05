package com.partygallery.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Party Gallery Design System - Color Palette
 *
 * Philosophy: Dark Mode First
 * Primary accent: Amber (#F59E0B)
 *
 * All colors are designed with dark backgrounds as the primary use case,
 * with light mode being a secondary consideration.
 */
object PartyGalleryColors {

    // ============================================
    // BRAND COLORS
    // ============================================

    /** Primary brand color - Amber 500 */
    val Primary = Color(0xFFF59E0B)

    /** Secondary brand color - Amber 400 */
    val Secondary = Color(0xFFFBBF24)

    /** Tertiary accent - Amber 300 */
    val Tertiary = Color(0xFFFCD34D)

    // ============================================
    // DARK THEME BACKGROUNDS (Primary)
    // ============================================

    /** Main background - Near black */
    val DarkBackground = Color(0xFF0A0A0A)

    /** Surface color - Elevated background */
    val DarkSurface = Color(0xFF141414)

    /** Surface variant - Cards, sheets */
    val DarkSurfaceVariant = Color(0xFF1E1E1E)

    /** Surface container - Containers, dialogs */
    val DarkSurfaceContainer = Color(0xFF262626)

    /** Surface container high - Elevated elements */
    val DarkSurfaceContainerHigh = Color(0xFF2E2E2E)

    // ============================================
    // LIGHT THEME BACKGROUNDS (Secondary)
    // ============================================

    /** Light background */
    val LightBackground = Color(0xFFFAFAFA)

    /** Light surface */
    val LightSurface = Color(0xFFFFFFFF)

    /** Light surface variant */
    val LightSurfaceVariant = Color(0xFFF5F5F5)

    /** Light surface container */
    val LightSurfaceContainer = Color(0xFFEEEEEE)

    // ============================================
    // TEXT COLORS - DARK THEME
    // ============================================

    /** Primary text on dark - High emphasis */
    val DarkOnBackground = Color(0xFFFFFFFF)

    /** Secondary text on dark - Medium emphasis */
    val DarkOnBackgroundVariant = Color(0xB3FFFFFF) // 70% white

    /** Tertiary text on dark - Low emphasis */
    val DarkOnBackgroundDisabled = Color(0x66FFFFFF) // 40% white

    /** Text on primary (amber) color */
    val OnPrimary = Color(0xFF1F1F1F)

    // ============================================
    // TEXT COLORS - LIGHT THEME
    // ============================================

    /** Primary text on light */
    val LightOnBackground = Color(0xFF1F1F1F)

    /** Secondary text on light */
    val LightOnBackgroundVariant = Color(0xB31F1F1F) // 70% black

    /** Tertiary text on light */
    val LightOnBackgroundDisabled = Color(0x661F1F1F) // 40% black

    // ============================================
    // SEMANTIC COLORS
    // ============================================

    /** Error / Destructive actions */
    val Error = Color(0xFFEF4444)

    /** Error variant */
    val ErrorVariant = Color(0xFFDC2626)

    /** Success / Positive feedback */
    val Success = Color(0xFF22C55E)

    /** Warning / Attention needed */
    val Warning = Color(0xFFF59E0B) // Same as Primary for cohesion

    /** Info / Informational */
    val Info = Color(0xFF3B82F6)

    // ============================================
    // MOOD COLORS (Media tagging)
    // ============================================

    /** Hype mood - Energetic orange */
    val MoodHype = Color(0xFFFF6B35)

    /** Chill mood - Relaxed purple */
    val MoodChill = Color(0xFF8B5CF6)

    /** Wild mood - Vibrant pink */
    val MoodWild = Color(0xFFEC4899)

    /** Romantic mood - Soft rose */
    val MoodRomantic = Color(0xFFF43F5E)

    /** Crazy mood - Electric cyan */
    val MoodCrazy = Color(0xFF06B6D4)

    /** Elegant mood - Refined gold */
    val MoodElegant = Color(0xFFF59E0B)

    // ============================================
    // OVERLAY & SCRIM
    // ============================================

    /** Dark overlay for modals */
    val Scrim = Color(0xCC000000) // 80% black

    /** Light scrim for contrast */
    val ScrimLight = Color(0x66000000) // 40% black

    // ============================================
    // BORDER & DIVIDER
    // ============================================

    /** Border on dark surfaces */
    val DarkBorder = Color(0xFF2E2E2E)

    /** Divider on dark surfaces */
    val DarkDivider = Color(0x33FFFFFF) // 20% white

    /** Border on light surfaces */
    val LightBorder = Color(0xFFE5E5E5)

    /** Divider on light surfaces */
    val LightDivider = Color(0x1F000000) // 12% black
}
