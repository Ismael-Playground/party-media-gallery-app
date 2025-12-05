package com.partygallery.domain.model

import kotlinx.datetime.Instant

/**
 * Type of chat message.
 */
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    SYSTEM,
    PARTY_MOMENT
}

/**
 * Message reaction from a user.
 */
data class MessageReaction(
    val emoji: String,
    val userId: String,
    val createdAt: Instant
)

/**
 * Chat room domain model.
 *
 * S1-010: Modelo ChatRoom (domain)
 */
data class ChatRoom(
    val id: String,
    val partyEventId: String? = null,
    val partyEvent: PartyEventSummary? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val participants: List<UserSummary> = emptyList(),
    val participantIds: List<String> = emptyList(),
    val isEventChat: Boolean = false,
    val isGroupChat: Boolean = false,
    val lastMessage: ChatMessage? = null,
    val unreadCount: Int = 0,
    val isMuted: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant? = null
) {
    val displayName: String
        get() = name ?: partyEvent?.title ?: participants.joinToString(", ") { it.displayName }

    val displayImage: String?
        get() = imageUrl ?: partyEvent?.coverImageUrl ?: participants.firstOrNull()?.avatarUrl
}

/**
 * Chat message domain model.
 *
 * S1-011: Modelo Message (domain)
 */
data class ChatMessage(
    val id: String,
    val chatRoomId: String,
    val senderId: String,
    val sender: UserSummary? = null,
    val type: MessageType = MessageType.TEXT,
    val content: String,
    val mediaUrl: String? = null,
    val mediaMetadata: MediaMetadata? = null,
    val replyToMessageId: String? = null,
    val replyToMessage: ChatMessage? = null,
    val isPartyMoment: Boolean = false,
    val reactions: List<MessageReaction> = emptyList(),
    val readByUserIds: List<String> = emptyList(),
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant? = null
) {
    val isFromCurrentUser: Boolean
        get() = false // Will be set by UI layer based on current user

    val hasMedia: Boolean
        get() = mediaUrl != null

    val isSystemMessage: Boolean
        get() = type == MessageType.SYSTEM

    val reactionSummary: Map<String, Int>
        get() = reactions.groupBy { it.emoji }.mapValues { it.value.size }

    val displayContent: String
        get() = when {
            isDeleted -> "This message was deleted"
            type == MessageType.IMAGE -> "\uD83D\uDCF7 Photo"
            type == MessageType.VIDEO -> "\uD83C\uDFA5 Video"
            type == MessageType.AUDIO -> "\uD83C\uDFA4 Voice message"
            type == MessageType.PARTY_MOMENT -> "\uD83C\uDF89 Party Moment"
            else -> content
        }
}

/**
 * Simplified chat room for list views.
 */
data class ChatRoomSummary(
    val id: String,
    val displayName: String,
    val displayImage: String?,
    val lastMessageContent: String?,
    val lastMessageTime: Instant?,
    val unreadCount: Int,
    val isEventChat: Boolean
)

/**
 * Extension to convert ChatRoom to summary.
 */
fun ChatRoom.toSummary(): ChatRoomSummary = ChatRoomSummary(
    id = id,
    displayName = displayName,
    displayImage = displayImage,
    lastMessageContent = lastMessage?.displayContent,
    lastMessageTime = lastMessage?.createdAt,
    unreadCount = unreadCount,
    isEventChat = isEventChat
)
