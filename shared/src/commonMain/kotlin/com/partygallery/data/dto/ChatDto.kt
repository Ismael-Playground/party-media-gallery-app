package com.partygallery.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for ChatRoom entity.
 * Used for Firebase Firestore serialization/deserialization.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */
@Serializable
data class ChatRoomDto(
    val id: String = "",
    @SerialName("party_event_id")
    val partyEventId: String? = null,
    val name: String? = null,
    // List of User IDs
    val participants: List<String> = emptyList(),
    @SerialName("participant_details")
    val participantDetails: List<UserSummaryDto> = emptyList(),
    @SerialName("is_event_chat")
    val isEventChat: Boolean = false,
    @SerialName("last_message")
    val lastMessage: ChatMessagePreviewDto? = null,
    @SerialName("unread_count")
    val unreadCount: Int = 0,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long? = null,
)

@Serializable
data class ChatMessagePreviewDto(
    val text: String = "",
    @SerialName("sender_name")
    val senderName: String = "",
    @SerialName("sent_at")
    val sentAt: Long = 0L,
)

/**
 * Data Transfer Object for ChatMessage entity.
 */
@Serializable
data class ChatMessageDto(
    val id: String = "",
    @SerialName("chat_room_id")
    val chatRoomId: String = "",
    @SerialName("sender_id")
    val senderId: String = "",
    val sender: UserSummaryDto? = null,
    val content: String = "",
    // Valid values: TEXT, IMAGE, VIDEO, AUDIO, SYSTEM
    val type: String = "TEXT",
    @SerialName("media_url")
    val mediaUrl: String? = null,
    @SerialName("media_thumbnail_url")
    val mediaThumbnailUrl: String? = null,
    @SerialName("is_party_moment")
    val isPartyMoment: Boolean = false,
    // Map of emoji -> list of user IDs
    val reactions: Map<String, List<String>> = emptyMap(),
    @SerialName("reply_to_id")
    val replyToId: String? = null,
    @SerialName("reply_preview")
    val replyPreview: String? = null,
    @SerialName("is_read")
    val isRead: Boolean = false,
    // User IDs who read this message
    @SerialName("read_by")
    val readBy: List<String> = emptyList(),
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long? = null,
)

/**
 * DTO for typing indicator
 */
@Serializable
data class TypingIndicatorDto(
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("chat_room_id")
    val chatRoomId: String = "",
    @SerialName("is_typing")
    val isTyping: Boolean = false,
    @SerialName("last_typed_at")
    val lastTypedAt: Long = 0L,
)
