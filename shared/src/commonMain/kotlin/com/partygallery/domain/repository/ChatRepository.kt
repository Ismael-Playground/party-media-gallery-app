package com.partygallery.domain.repository

import com.partygallery.domain.model.ChatRoom
import com.partygallery.domain.model.ChatMessage
import com.partygallery.domain.model.MessageType
import com.partygallery.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Repository interface for Chat operations.
 *
 * S1-011: Interfaces base de repositorios
 */
interface ChatRepository {

    // ============================================
    // Chat Room Operations
    // ============================================

    suspend fun createChatRoom(
        partyId: String? = null,
        participants: List<String>,
        isEventChat: Boolean = false
    ): Result<ChatRoom>

    suspend fun getChatRoomById(roomId: String): Result<ChatRoom?>
    suspend fun getChatRoomForParty(partyId: String): Result<ChatRoom?>
    suspend fun getPrivateChatRoom(userId1: String, userId2: String): Result<ChatRoom?>
    suspend fun getUserChatRooms(userId: String): Result<List<ChatRoom>>
    suspend fun deleteChatRoom(roomId: String): Result<Unit>

    // ============================================
    // Message Operations
    // ============================================

    suspend fun sendMessage(
        roomId: String,
        senderId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        replyToId: String? = null
    ): Result<ChatMessage>

    suspend fun getMessages(
        roomId: String,
        limit: Int = 50,
        before: Instant? = null
    ): Result<List<ChatMessage>>

    suspend fun getMessage(messageId: String): Result<ChatMessage?>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    suspend fun editMessage(messageId: String, newContent: String): Result<ChatMessage>

    // ============================================
    // Reactions
    // ============================================

    suspend fun addReaction(messageId: String, userId: String, emoji: String): Result<Unit>
    suspend fun removeReaction(messageId: String, userId: String, emoji: String): Result<Unit>
    suspend fun getReactions(messageId: String): Result<Map<String, List<String>>>

    // ============================================
    // Read Status
    // ============================================

    suspend fun markAsRead(roomId: String, userId: String, messageId: String): Result<Unit>
    suspend fun getUnreadCount(roomId: String, userId: String): Result<Int>
    suspend fun getLastReadMessageId(roomId: String, userId: String): Result<String?>

    // ============================================
    // Participants
    // ============================================

    suspend fun addParticipant(roomId: String, userId: String): Result<Unit>
    suspend fun removeParticipant(roomId: String, userId: String): Result<Unit>
    suspend fun getParticipants(roomId: String): Result<List<UserSummary>>

    // ============================================
    // Typing Indicators
    // ============================================

    suspend fun setTyping(roomId: String, userId: String, isTyping: Boolean): Result<Unit>
    fun observeTypingUsers(roomId: String): Flow<List<String>>

    // ============================================
    // Observable Flows
    // ============================================

    fun observeMessages(roomId: String): Flow<List<ChatMessage>>
    fun observeNewMessages(roomId: String): Flow<ChatMessage>
    fun observeChatRooms(userId: String): Flow<List<ChatRoom>>
    fun observeUnreadCount(roomId: String, userId: String): Flow<Int>
    fun observeTotalUnreadCount(userId: String): Flow<Int>
}
