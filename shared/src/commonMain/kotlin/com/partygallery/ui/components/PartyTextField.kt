package com.partygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Party Gallery Text Field Component
 *
 * Custom styled text input following the Dark Mode First design system.
 *
 * @param value Current text value
 * @param onValueChange Value change callback
 * @param modifier Modifier for the text field
 * @param label Optional label text
 * @param placeholder Optional placeholder text
 * @param leadingIcon Optional leading icon composable
 * @param trailingIcon Optional trailing icon composable
 * @param isError Whether the field is in error state
 * @param errorMessage Error message to display
 * @param enabled Whether the field is enabled
 * @param singleLine Whether the field is single line
 * @param maxLines Maximum number of lines
 * @param keyboardOptions Keyboard options
 * @param keyboardActions Keyboard actions
 * @param visualTransformation Visual transformation (e.g., password masking)
 */
@Composable
fun PartyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colors = Theme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        isError -> colors.error
        isFocused -> colors.primary
        else -> colors.border
    }

    val backgroundColor = colors.surfaceVariant

    Column(modifier = modifier) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = PartyGalleryTypography.labelMedium,
                color = if (isError) colors.error else colors.onBackgroundVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Text field container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PartyGalleryShapes.inputField)
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = PartyGalleryShapes.inputField,
                )
                .padding(PartyGallerySpacing.inputPadding),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Leading icon
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Text input
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    // Placeholder
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = PartyGalleryTypography.bodyLarge,
                            color = colors.onBackgroundDisabled,
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        textStyle = PartyGalleryTypography.bodyLarge.copy(
                            color = colors.onBackground,
                        ),
                        singleLine = singleLine,
                        maxLines = maxLines,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource,
                        cursorBrush = SolidColor(colors.primary),
                    )
                }

                // Trailing icon
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    trailingIcon()
                }
            }
        }

        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = PartyGalleryTypography.labelSmall,
                color = colors.error,
            )
        }
    }
}

/**
 * Search field variant with search icon and clear button
 */
@Composable
fun PartySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onClear: () -> Unit = { onValueChange("") },
    enabled: Boolean = true,
) {
    val colors = Theme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(PartyGalleryShapes.searchBar)
            .background(colors.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Search icon placeholder (would be an actual icon in production)
            Text(
                text = "🔍",
                style = PartyGalleryTypography.bodyMedium,
                color = colors.onBackgroundVariant,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = PartyGalleryTypography.bodyMedium,
                        color = colors.onBackgroundDisabled,
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    textStyle = PartyGalleryTypography.bodyMedium.copy(
                        color = colors.onBackground,
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(colors.primary),
                )
            }

            // Clear button
            if (value.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✕",
                    style = PartyGalleryTypography.bodyMedium,
                    color = colors.onBackgroundVariant,
                    modifier = Modifier
                        .clip(PartyGalleryShapes.full)
                        .padding(4.dp),
                )
            }
        }
    }
}
