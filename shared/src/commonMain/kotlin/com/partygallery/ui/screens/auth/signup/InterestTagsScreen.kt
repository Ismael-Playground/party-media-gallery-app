package com.partygallery.ui.screens.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.state.PartyTag
import com.partygallery.presentation.state.TagCategory
import com.partygallery.presentation.store.SignUpStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Interest Tags Screen - Step 4 of SignUp Flow
 *
 * S2-014: Fourth step of signup flow
 *
 * Allows user to select party interest tags:
 * - Music genres
 * - Party types
 * - Vibes
 *
 * Minimum 3 tags required
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestTagsScreen(signUpStore: SignUpStore, onBackPressed: () -> Unit = {}) {
    val state by signUpStore.state.collectAsState()
    val colors = Theme.colors

    // Load tags on first composition
    LaunchedEffect(Unit) {
        signUpStore.processIntent(SignUpIntent.LoadTags)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PartyGallerySpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            SignUpStepHeader(
                stepNumber = 4,
                totalSteps = 6,
                title = "What's Your Vibe?",
                subtitle = "Select at least 3 interests to personalize your experience",
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selection count
            Text(
                text = "${state.selectedTags.size} selected (min 3)",
                style = PartyGalleryTypography.labelMedium,
                color = if (state.selectedTags.size >= 3) colors.success else colors.onBackgroundVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoadingTags) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = colors.primary,
                )
            } else {
                // Group tags by category
                val tagsByCategory = state.availableTags.groupBy { it.category }

                TagCategory.entries.forEach { category ->
                    val tags = tagsByCategory[category] ?: emptyList()
                    if (tags.isNotEmpty()) {
                        TagCategorySection(
                            category = category,
                            tags = tags,
                            selectedTags = state.selectedTags,
                            onToggleTag = { signUpStore.processIntent(SignUpIntent.ToggleTag(it)) },
                        )
                        Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            PartyButton(
                text = "Continue",
                onClick = { signUpStore.processIntent(SignUpIntent.SubmitTags) },
                modifier = Modifier.fillMaxWidth(),
                variant = PartyButtonVariant.PRIMARY,
                size = PartyButtonSize.LARGE,
                enabled = state.selectedTags.size >= 3,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Back Button
            Text(
                text = "Back",
                style = PartyGalleryTypography.labelMedium,
                color = colors.primary,
                modifier = Modifier
                    .clickable {
                        signUpStore.processIntent(SignUpIntent.PreviousStep)
                        onBackPressed()
                    }
                    .padding(PartyGallerySpacing.md),
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagCategorySection(
    category: TagCategory,
    tags: List<PartyTag>,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit,
) {
    val colors = Theme.colors

    Column {
        Text(
            text = when (category) {
                TagCategory.MUSIC_GENRE -> "Music"
                TagCategory.PARTY_TYPE -> "Party Types"
                TagCategory.VIBE -> "Vibes"
                TagCategory.ACTIVITY -> "Activities"
            },
            style = PartyGalleryTypography.titleMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tags.forEach { tag ->
                TagChip(
                    tag = tag,
                    isSelected = selectedTags.contains(tag.id),
                    onClick = { onToggleTag(tag.id) },
                )
            }
        }
    }
}

@Composable
private fun TagChip(tag: PartyTag, isSelected: Boolean, onClick: () -> Unit) {
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) colors.primary else colors.surfaceVariant)
            .border(
                width = 1.dp,
                color = if (isSelected) colors.primary else colors.divider,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = "${tag.emoji} ${tag.name}",
            style = PartyGalleryTypography.labelMedium,
            color = if (isSelected) colors.onPrimary else colors.onBackground,
        )
    }
}
