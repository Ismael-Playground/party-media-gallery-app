package com.partygallery.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.partygallery.ui.theme.*

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Party Gallery - Design System Preview",
        state = rememberWindowState(width = 420.dp, height = 800.dp)
    ) {
        PartyGalleryTheme(darkTheme = true) {
            DesignSystemPreview()
        }
    }
}

@Composable
fun DesignSystemPreview() {
    val colors = Theme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(PartyGallerySpacing.screenHorizontal)
    ) {
        // Header
        Text(
            text = "Party Gallery",
            style = PartyGalleryTypography.displaySmall,
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Dark Mode First Design System",
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Color Palette Section
        SectionTitle("Color Palette")

        ColorRow("Background", colors.background, "#0A0A0A")
        ColorRow("Surface", colors.surface, "#141414")
        ColorRow("Surface Variant", colors.surfaceVariant, "#1E1E1E")
        ColorRow("Primary (Amber)", colors.primary, "#F59E0B")
        ColorRow("Secondary", colors.secondary, "#FBBF24")
        ColorRow("Error", colors.error, "#EF4444")
        ColorRow("Success", colors.success, "#22C55E")

        Spacer(modifier = Modifier.height(24.dp))

        // Mood Colors
        SectionTitle("Mood Colors")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodChip("HYPE", PartyGalleryColors.MoodHype)
            MoodChip("CHILL", PartyGalleryColors.MoodChill)
            MoodChip("WILD", PartyGalleryColors.MoodWild)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodChip("ROMANTIC", PartyGalleryColors.MoodRomantic)
            MoodChip("CRAZY", PartyGalleryColors.MoodCrazy)
            MoodChip("ELEGANT", PartyGalleryColors.MoodElegant)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Typography Section
        SectionTitle("Typography")

        Text(
            text = "Display Large",
            style = PartyGalleryTypography.displayLarge,
            color = colors.onBackground
        )
        Text(
            text = "Headline Medium",
            style = PartyGalleryTypography.headlineMedium,
            color = colors.onBackground
        )
        Text(
            text = "Title Large",
            style = PartyGalleryTypography.titleLarge,
            color = colors.onBackground
        )
        Text(
            text = "Body Large - Main content text",
            style = PartyGalleryTypography.bodyLarge,
            color = colors.onBackground
        )
        Text(
            text = "Body Medium - Secondary text",
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant
        )
        Text(
            text = "Label Small - Metadata",
            style = PartyGalleryTypography.labelSmall,
            color = colors.onBackgroundDisabled
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cards Section
        SectionTitle("Cards")

        // Simulated Party Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(PartyGalleryShapes.mediaCard)
                .background(colors.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.primary)
                    )

                    // Live badge
                    Box(
                        modifier = Modifier
                            .clip(PartyGalleryShapes.chip)
                            .background(colors.error)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "● LIVE",
                            style = PartyGalleryTypography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }

                // Bottom content
                Column {
                    Text(
                        text = "Summer Rooftop Party",
                        style = PartyGalleryTypography.partyTitle,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Downtown • 127 guests",
                        style = PartyGalleryTypography.bodySmall,
                        color = colors.onBackgroundVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons Section
        SectionTitle("Buttons")

        // Primary Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PartyGalleryShapes.button)
                .background(colors.primary)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Join Party",
                style = PartyGalleryTypography.labelLarge,
                color = colors.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PartyGalleryShapes.button)
                .background(colors.surfaceVariant)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Browse Events",
                style = PartyGalleryTypography.labelLarge,
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input Field
        SectionTitle("Input Field")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PartyGalleryShapes.searchBar)
                .background(colors.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                text = "🔍  Search parties, venues...",
                style = PartyGalleryTypography.bodyMedium,
                color = colors.onBackgroundDisabled
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Text(
            text = "Design System v1.0",
            style = PartyGalleryTypography.labelSmall,
            color = colors.onBackgroundDisabled,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = PartyGalleryTypography.titleMedium,
        color = Theme.colors.primary
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun ColorRow(name: String, color: androidx.compose.ui.graphics.Color, hex: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(PartyGalleryShapes.small)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = PartyGalleryTypography.bodyMedium,
            color = Theme.colors.onBackground,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = hex,
            style = PartyGalleryTypography.labelSmall,
            color = Theme.colors.onBackgroundVariant
        )
    }
}

@Composable
fun MoodChip(mood: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(PartyGalleryShapes.moodBadge)
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = mood,
            style = PartyGalleryTypography.labelSmall,
            color = color
        )
    }
}
