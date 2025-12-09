package com.partygallery.data.mapper

import com.partygallery.data.dto.ChatMessageDto
import com.partygallery.data.dto.ChatMessagePreviewDto
import com.partygallery.data.dto.ChatRoomDto
import com.partygallery.data.dto.TypingIndicatorDto
import com.partygallery.domain.model.ChatMessage
import com.partygallery.domain.model.ChatRoom
import com.partygallery.domain.model.MediaMetadata
import com.partygallery.domain.model.MessageReaction
import com.partygallery.domain.model.MessageType
import kotlinx.datetime.Instant

/**
 * Mapper extensions for Chat DTO <-> Domain conversions.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */

// DTO -> Domain

fun ChatRoomDto.toDomain(): ChatRoom = ChatRoom(
    id = id,
    partyEventId = partyEventId,
    name = name,
    participants = participantDetails.map { it.toDomain() },
    participantIds = participants,
    isEventChat = isEventChat,
    isGroupChat = participants.size > 2,
    lastMessage = lastMessage?.toDomain(id),
    unreadCount = unreadCount,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it) },
)

fun ChatMessagePreviewDto.toDomain(chatRoomId: String): ChatMessage = ChatMessage(
    id = "", // Preview doesn't have full ID
    chatRoomId = chatRoomId,
    senderId = "",
    content = text,
    createdAt = Instant.fromEpochMilliseconds(sentAt),
)

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    chatRoomId = chatRoomId,
    senderId = senderId,
    sender = sender?.toDomain(),
    type = parseMessageType(type),
    content = content,
    mediaUrl = mediaUrl,
    mediaMetadata = mediaThumbnailUrl?.let {
        MediaMetadata() // Basic metadata, could be expanded
    },
    replyToMessageId = replyToId,
    isPartyMoment = isPartyMoment,
    reactions = parseReactions(reactions),
    readByUserIds = readBy,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it) },
)

// Domain -> DTO

fun ChatRoom.toDto(): ChatRoomDto = ChatRoomDto(
    id = id,
    partyEventId = partyEventId,
    name = name,
    participants = participantIds,
    participantDetails = participants.map { it.toDto() },
    isEventChat = isEventChat,
    lastMessage = lastMessage?.toPreviewDto(),
    unreadCount = unreadCount,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt?.toEpochMilliseconds(),
)

fun ChatMessage.toPreviewDto(): ChatMessagePreviewDto = ChatMessagePreviewDto(
    text = displayContent,
    senderName = sender?.displayName ?: "",
    sentAt = createdAt.toEpochMilliseconds(),
)

fun ChatMessage.toDto(): ChatMessageDto = ChatMessageDto(
    id = id,
    chatRoomId = chatRoomId,
    senderId = senderId,
    sender = sender?.toDto(),
    content = content,
    type = type.name,
    mediaUrl = mediaUrl,
    mediaThumbnailUrl = null, // Would need to be set separately
    isPartyMoment = isPartyMoment,
    reactions = reactionsToMap(reactions),
    replyToId = replyToMessageId,
    replyPreview = replyToMessage?.displayContent,
    isRead = readByUserIds.isNotEmpty(),
    readBy = readByUserIds,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt?.toEpochMilliseconds(),
)

// Typing indicator helpers

data class TypingIndicator(
    val userId: String,
    val chatRoomId: String,
    val isTyping: Boolean,
    val lastTypedAt: Instant,
)

fun TypingIndicatorDto.toDomain(): TypingIndicator = TypingIndicator(
    userId = userId,
    chatRoomId = chatRoomId,
    isTyping = isTyping,
    lastTypedAt = Instant.fromEpochMilliseconds(lastTypedAt),
)

fun TypingIndicator.toDto(): TypingIndicatorDto = TypingIndicatorDto(
    userId = userId,
    chatRoomId = chatRoomId,
    isTyping = isTyping,
    lastTypedAt = lastTypedAt.toEpochMilliseconds(),
)

// Helper functions

private fun parseMessageType(type: String): MessageType {
    return try {
        MessageType.valueOf(type)
    } catch (e: Exception) {
        MessageType.TEXT
    }
}

private fun parseReactions(reactions: Map<String, List<String>>): List<MessageReaction> {
    val result = mutableListOf<MessageReaction>()
    for ((emoji, userIds) in reactions) {
        for (userId in userIds) {
            result.add(
                MessageReaction(
                    emoji = emoji,
                    userId = userId,
                    createdAt = Instant.fromEpochMilliseconds(0), // Not stored in DTO
                )
            )
        }
    }
    return result
}

private fun reactionsToMap(reactions: List<MessageReaction>): Map<String, List<String>> {
    return reactions.groupBy({ it.emoji }, { it.userId })
}
