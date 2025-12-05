package com.partygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.partygallery.domain.model.PartyMood
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Mood colors mapped to PartyMood enum
 *
 * Following the Design System color definitions.
 */
object MoodColors {
    fun getColor(mood: PartyMood): Color {
        return when (mood) {
            PartyMood.HYPE -> PartyGalleryColors.MoodHype // #FF6B35 Orange
            PartyMood.CHILL -> PartyGalleryColors.MoodChill // #8B5CF6 Purple
            PartyMood.WILD -> PartyGalleryColors.MoodWild // #EC4899 Pink
            PartyMood.ROMANTIC -> PartyGalleryColors.MoodRomantic // #F43F5E Rose
            PartyMood.CRAZY -> PartyGalleryColors.MoodCrazy // #06B6D4 Cyan
            PartyMood.ELEGANT -> PartyGalleryColors.MoodElegant // #F59E0B Amber
        }
    }

    fun getEmoji(mood: PartyMood): String {
        return when (mood) {
            PartyMood.HYPE -> "\uD83D\uDD25" // 🔥
            PartyMood.CHILL -> "\uD83C\uDF0A" // 🌊
            PartyMood.WILD -> "\uD83C\uDF89" // 🎉
            PartyMood.ROMANTIC -> "\uD83D\uDC95" // 💕
            PartyMood.CRAZY -> "\uD83E\uDD2A" // 🤪
            PartyMood.ELEGANT -> "✨"
        }
    }
}

/**
 * Mood Tag Component
 *
 * Chip-style tag showing the party/media mood with color coding.
 *
 * @param mood The mood to display
 * @param showEmoji Whether to show the mood emoji
 * @param modifier Modifier
 */
@Composable
fun MoodTag(mood: PartyMood, showEmoji: Boolean = true, modifier: Modifier = Modifier) {
    val moodColor = MoodColors.getColor(mood)
    val emoji = MoodColors.getEmoji(mood)

    Row(
        modifier = modifier
            .clip(PartyGalleryShapes.moodBadge)
            .background(moodColor.copy(alpha = 0.2f))
            .padding(
                horizontal = PartyGallerySpacing.sm,
                vertical = PartyGallerySpacing.xxs,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showEmoji) {
            Text(
                text = emoji,
                style = Theme.typography.labelSmall,
            )
        }

        Text(
            text = mood.name.lowercase().replaceFirstChar { it.uppercase() },
            style = Theme.typography.labelSmall,
            color = moodColor,
        )
    }
}

/**
 * Mood Tag with custom text
 *
 * For cases where you want to show additional info.
 */
@Composable
fun MoodTagWithCount(mood: PartyMood, count: Int, modifier: Modifier = Modifier) {
    val moodColor = MoodColors.getColor(mood)
    val emoji = MoodColors.getEmoji(mood)

    Row(
        modifier = modifier
            .clip(PartyGalleryShapes.moodBadge)
            .background(moodColor.copy(alpha = 0.2f))
            .padding(
                horizontal = PartyGallerySpacing.sm,
                vertical = PartyGallerySpacing.xxs,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = emoji,
            style = Theme.typography.labelSmall,
        )

        Text(
            text = count.toString(),
            style = Theme.typography.labelSmall,
            color = moodColor,
        )
    }
}

/**
 * Selectable Mood Tag
 *
 * For filtering/selection purposes.
 */
@Composable
fun SelectableMoodTag(
    mood: PartyMood,
    isSelected: Boolean,
    onClick: () -> Unit,
    showEmoji: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val moodColor = MoodColors.getColor(mood)
    val emoji = MoodColors.getEmoji(mood)

    val backgroundColor = if (isSelected) {
        moodColor.copy(alpha = 0.3f)
    } else {
        moodColor.copy(alpha = 0.1f)
    }

    val textColor = if (isSelected) {
        moodColor
    } else {
        moodColor.copy(alpha = 0.7f)
    }

    Row(
        modifier = modifier
            .clip(PartyGalleryShapes.moodBadge)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = PartyGallerySpacing.sm,
                vertical = PartyGallerySpacing.xxs,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showEmoji) {
            Text(
                text = emoji,
                style = Theme.typography.labelSmall,
            )
        }

        Text(
            text = mood.name.lowercase().replaceFirstChar { it.uppercase() },
            style = Theme.typography.labelSmall,
            color = textColor,
        )
    }
}
