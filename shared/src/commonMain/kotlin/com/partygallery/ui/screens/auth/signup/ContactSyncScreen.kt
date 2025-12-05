package com.partygallery.ui.screens.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.state.ContactMatch
import com.partygallery.presentation.store.SignUpStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Contact Sync Screen - Step 3 of SignUp Flow
 *
 * S2-013: Third step of signup flow
 *
 * Allows user to:
 * - Request contacts permission
 * - See matched contacts who are already on the app
 * - Select contacts to follow
 * - Skip this step
 *
 * Design: Dark Mode First
 */
@Composable
fun ContactSyncScreen(signUpStore: SignUpStore, onBackPressed: () -> Unit = {}) {
    val state by signUpStore.state.collectAsState()
    val colors = Theme.colors

    // Sync contacts on first composition if permission granted
    LaunchedEffect(state.hasContactsPermission) {
        if (state.hasContactsPermission && state.contactMatches.isEmpty() && !state.isSyncingContacts) {
            signUpStore.processIntent(SignUpIntent.SyncContacts)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PartyGallerySpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            SignUpStepHeader(
                stepNumber = 3,
                totalSteps = 6,
                title = "Find Your Friends",
                subtitle = "Connect with people you already know",
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!state.hasContactsPermission) {
                // Permission request UI
                PermissionRequestContent(
                    onRequestPermission = {
                        signUpStore.processIntent(SignUpIntent.RequestContactsPermission)
                    },
                )
            } else if (state.isSyncingContacts) {
                // Loading state
                SyncingContent()
            } else if (state.contactMatches.isEmpty()) {
                // No matches found
                NoMatchesContent()
            } else {
                // Show matched contacts
                ContactMatchesList(
                    contacts = state.contactMatches,
                    selectedContacts = state.selectedContacts,
                    onToggleContact = { contactId ->
                        signUpStore.processIntent(SignUpIntent.ToggleContactSelection(contactId))
                    },
                    onSelectAll = {
                        signUpStore.processIntent(SignUpIntent.SelectAllContacts)
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = !state.hasContactsPermission))

            // Selected count
            if (state.hasContactsPermission && state.contactMatches.isNotEmpty()) {
                Text(
                    text = "${state.selectedContacts.size} of ${state.contactMatches.size} selected",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.onBackgroundVariant,
                )
                Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            }

            // Continue Button
            PartyButton(
                text = when {
                    state.selectedContacts.isNotEmpty() -> "Follow ${state.selectedContacts.size} & Continue"
                    else -> "Continue"
                },
                onClick = { signUpStore.processIntent(SignUpIntent.SubmitContacts) },
                modifier = Modifier.fillMaxWidth(),
                variant = PartyButtonVariant.PRIMARY,
                size = PartyButtonSize.LARGE,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Skip Button
            Text(
                text = "Skip for now",
                style = PartyGalleryTypography.labelMedium,
                color = colors.onBackgroundVariant,
                modifier = Modifier
                    .clickable { signUpStore.processIntent(SignUpIntent.SkipStep) }
                    .padding(PartyGallerySpacing.md),
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

/**
 * Content shown when contacts permission is not granted
 */
@Composable
private fun PermissionRequestContent(onRequestPermission: () -> Unit) {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "👥",
                style = PartyGalleryTypography.displayMedium,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Find Friends on Party Gallery",
            style = PartyGalleryTypography.titleLarge,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        Text(
            text = "Allow access to your contacts to find friends who are already using Party Gallery",
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant,
            modifier = Modifier.padding(horizontal = PartyGallerySpacing.xl),
        )

        Spacer(modifier = Modifier.height(32.dp))

        PartyButton(
            text = "Allow Contacts Access",
            onClick = onRequestPermission,
            variant = PartyButtonVariant.SECONDARY,
            size = PartyButtonSize.MEDIUM,
        )
    }
}

/**
 * Content shown while syncing contacts
 */
@Composable
private fun SyncingContent() {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = colors.primary,
            strokeWidth = 3.dp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Finding your friends...",
            style = PartyGalleryTypography.titleMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        Text(
            text = "This may take a moment",
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant,
        )
    }
}

/**
 * Content shown when no matches are found
 */
@Composable
private fun NoMatchesContent() {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🔍",
                style = PartyGalleryTypography.displayMedium,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Friends Found Yet",
            style = PartyGalleryTypography.titleLarge,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        Text(
            text = "None of your contacts are on Party Gallery yet. Invite them to join the party!",
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant,
            modifier = Modifier.padding(horizontal = PartyGallerySpacing.xl),
        )

        Spacer(modifier = Modifier.height(24.dp))

        PartyButton(
            text = "Invite Friends",
            onClick = { /* TODO: Open share sheet */ },
            variant = PartyButtonVariant.SECONDARY,
            size = PartyButtonSize.MEDIUM,
        )
    }
}

/**
 * List of matched contacts
 */
@Composable
private fun ContactMatchesList(
    contacts: List<ContactMatch>,
    selectedContacts: Set<String>,
    onToggleContact: (String) -> Unit,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = Theme.colors
    val allSelected = selectedContacts.size == contacts.size

    Column(modifier = modifier) {
        // Select all header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectAll() }
                .padding(vertical = PartyGallerySpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${contacts.size} friends found",
                style = PartyGalleryTypography.titleSmall,
                color = colors.onBackground,
            )
            Text(
                text = if (allSelected) "Deselect All" else "Select All",
                style = PartyGalleryTypography.labelMedium,
                color = colors.primary,
            )
        }

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(contacts, key = { it.userId }) { contact ->
                ContactMatchItem(
                    contact = contact,
                    isSelected = selectedContacts.contains(contact.userId),
                    onToggle = { onToggleContact(contact.userId) },
                )
            }
        }
    }
}

/**
 * Single contact match item
 */
@Composable
private fun ContactMatchItem(contact: ContactMatch, isSelected: Boolean, onToggle: () -> Unit) {
    val colors = Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.1f) else colors.surfaceVariant)
            .border(
                width = 1.dp,
                color = if (isSelected) colors.primary else colors.divider,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable { onToggle() }
            .padding(PartyGallerySpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = contact.displayName.take(1).uppercase(),
                style = PartyGalleryTypography.titleMedium,
                color = colors.primary,
            )
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.md))

        // Name and username
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.displayName,
                style = PartyGalleryTypography.titleSmall,
                color = colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "@${contact.username}",
                style = PartyGalleryTypography.bodySmall,
                color = colors.onBackgroundVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        // Checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.onBackgroundVariant,
                checkmarkColor = colors.background,
            ),
        )
    }
}
