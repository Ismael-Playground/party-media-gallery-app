package com.partygallery.presentation.intent

import com.partygallery.presentation.state.StudioTab

/**
 * Studio Screen Intents
 *
 * S3-NEW-003: StudioIntent sealed class for MVI pattern
 *
 * Represents all user actions that can occur on the studio screen.
 * Each intent triggers a state change in the StudioStore.
 */
sealed class StudioIntent {

    // ============================================
    // Loading Actions
    // ============================================

    data object LoadStudioContent : StudioIntent()
    data object RefreshStudioContent : StudioIntent()

    // ============================================
    // Tab Navigation
    // ============================================

    data class SelectTab(val tab: StudioTab) : StudioIntent()

    // ============================================
    // Content Actions
    // ============================================

    data class DeleteContent(val contentId: String) : StudioIntent()
    data class EditContent(val contentId: String) : StudioIntent()
    data class ViewContentDetails(val contentId: String) : StudioIntent()

    // ============================================
    // Draft Actions
    // ============================================

    data class PublishDraft(val draftId: String) : StudioIntent()
    data class EditDraft(val draftId: String) : StudioIntent()
    data class DeleteDraft(val draftId: String) : StudioIntent()

    // ============================================
    // Create Actions
    // ============================================

    data object StartCreateContent : StudioIntent()
    data class CreateContentForParty(val partyId: String) : StudioIntent()

    // ============================================
    // State Management
    // ============================================

    data object DismissError : StudioIntent()
}
