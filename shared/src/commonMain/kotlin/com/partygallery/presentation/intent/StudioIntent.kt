package com.partygallery.presentation.intent

import com.partygallery.presentation.state.StudioTab

/**
 * Studio screen intents.
 *
 * S3-NEW-003: StudioIntent sealed class for MVI pattern
 *
 * The Studio is where users create and manage their party content.
 */
sealed class StudioIntent {
    data object LoadStudio : StudioIntent()
    data object RefreshStudio : StudioIntent()
    data class SelectTab(val tab: StudioTab) : StudioIntent()

    // Content creation
    data object OpenCamera : StudioIntent()
    data object OpenGallery : StudioIntent()
    data object CreateParty : StudioIntent()
    data object StartLiveStream : StudioIntent()

    // Draft management
    data class DeleteDraft(val draftId: String) : StudioIntent()
    data class EditDraft(val draftId: String) : StudioIntent()
    data class PublishDraft(val draftId: String) : StudioIntent()

    // Content management
    data class OpenContent(val contentId: String) : StudioIntent()
    data class DeleteContent(val contentId: String) : StudioIntent()

    // Party management
    data class OpenParty(val partyId: String) : StudioIntent()
    data class EditParty(val partyId: String) : StudioIntent()

    data object DismissError : StudioIntent()
}
