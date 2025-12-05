package com.partygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Button variants for Party Gallery
 */
enum class PartyButtonVariant {
    /** Primary amber button */
    PRIMARY,
    /** Secondary outlined button */
    SECONDARY,
    /** Ghost/text button */
    GHOST,
    /** Destructive/error button */
    DESTRUCTIVE
}

/**
 * Button sizes
 */
enum class PartyButtonSize {
    SMALL,
    MEDIUM,
    LARGE
}

/**
 * Party Gallery Button Component
 *
 * Primary interactive element following the Dark Mode First design system.
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for the button
 * @param variant Button style variant
 * @param size Button size
 * @param enabled Whether the button is enabled
 * @param leadingIcon Optional composable for leading icon
 * @param trailingIcon Optional composable for trailing icon
 */
@Composable
fun PartyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PartyButtonVariant = PartyButtonVariant.PRIMARY,
    size: PartyButtonSize = PartyButtonSize.MEDIUM,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val colors = Theme.colors
    val interactionSource = remember { MutableInteractionSource() }

    val (backgroundColor, contentColor, borderColor) = when (variant) {
        PartyButtonVariant.PRIMARY -> Triple(
            colors.primary,
            colors.onPrimary,
            Color.Transparent
        )
        PartyButtonVariant.SECONDARY -> Triple(
            Color.Transparent,
            colors.primary,
            colors.primary
        )
        PartyButtonVariant.GHOST -> Triple(
            Color.Transparent,
            colors.onBackground,
            Color.Transparent
        )
        PartyButtonVariant.DESTRUCTIVE -> Triple(
            colors.error,
            Color.White,
            Color.Transparent
        )
    }

    val (paddingHorizontal, paddingVertical, minHeight) = when (size) {
        PartyButtonSize.SMALL -> Triple(16.dp, 8.dp, 32.dp)
        PartyButtonSize.MEDIUM -> Triple(24.dp, 12.dp, 44.dp)
        PartyButtonSize.LARGE -> Triple(32.dp, 16.dp, 56.dp)
    }

    val textStyle = when (size) {
        PartyButtonSize.SMALL -> PartyGalleryTypography.labelSmall
        PartyButtonSize.MEDIUM -> PartyGalleryTypography.labelLarge
        PartyButtonSize.LARGE -> PartyGalleryTypography.titleSmall
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = minHeight)
            .clip(PartyGalleryShapes.button)
            .background(backgroundColor)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(color = contentColor.copy(alpha = 0.3f))
            )
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()

            androidx.compose.material3.Text(
                text = text,
                style = textStyle,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            trailingIcon?.invoke()
        }
    }
}
