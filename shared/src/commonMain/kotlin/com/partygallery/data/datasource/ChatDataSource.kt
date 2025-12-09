package com.partygallery.data.datasource

import com.partygallery.data.dto.ChatMessageDto
import com.partygallery.data.dto.ChatRoomDto
import com.partygallery.data.dto.UserSummaryDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for Chat operations.
 * Platform-specific implementations will provide the actual Firebase Firestore integration.
 *
 * S2.5-004: ChatRepositoryImpl con Firestore real-time
 */
interface ChatDataSource {

    // ============================================
    // Chat Room Operations
    // ============================================

    suspend fun createChatRoom(
        partyId: String?,
        participants: List<String>,
        isEventChat: Boolean,
    ): ChatRoomDto

    suspend fun getChatRoomById(roomId: String): ChatRoomDto?

    suspend fun getChatRoomForParty(partyId: String): ChatRoomDto?

    suspend fun getPrivateChatRoom(userId1: String, userId2: String): ChatRoomDto?

    suspend fun getUserChatRooms(userId: String): List<ChatRoomDto>

    suspend fun deleteChatRoom(roomId: String)

    // ============================================
    // Message Operations
    // ============================================

    suspend fun sendMessage(
        roomId: String,
        senderId: String,
        content: String,
        type: String,
        mediaUrl: String?,
        replyToId: String?,
    ): ChatMessageDto

    suspend fun getMessages(roomId: String, limit: Int, beforeTimestamp: Long?): List<ChatMessageDto>

    suspend fun getMessage(messageId: String): ChatMessageDto?

    suspend fun deleteMessage(messageId: String)

    suspend fun editMessage(messageId: String, newContent: String): ChatMessageDto

    // ============================================
    // Reactions
    // ============================================

    suspend fun addReaction(messageId: String, userId: String, emoji: String)

    suspend fun removeReaction(messageId: String, userId: String, emoji: String)

    suspend fun getReactions(messageId: String): Map<String, List<String>>

    // ============================================
    // Read Status
    // ============================================

    suspend fun markAsRead(roomId: String, userId: String, messageId: String)

    suspend fun getUnreadCount(roomId: String, userId: String): Int

    suspend fun getLastReadMessageId(roomId: String, userId: String): String?

    // ============================================
    // Participants
    // ============================================

    suspend fun addParticipant(roomId: String, userId: String)

    suspend fun removeParticipant(roomId: String, userId: String)

    suspend fun getParticipants(roomId: String): List<UserSummaryDto>

    // ============================================
    // Typing Indicators
    // ============================================

    suspend fun setTyping(roomId: String, userId: String, isTyping: Boolean)

    fun observeTypingUsers(roomId: String): Flow<List<String>>

    // ============================================
    // Real-time Observers
    // ============================================

    fun observeMessages(roomId: String): Flow<List<ChatMessageDto>>

    fun observeNewMessages(roomId: String): Flow<ChatMessageDto>

    fun observeChatRooms(userId: String): Flow<List<ChatRoomDto>>

    fun observeUnreadCount(roomId: String, userId: String): Flow<Int>

    fun observeTotalUnreadCount(userId: String): Flow<Int>
}
