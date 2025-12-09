package com.partygallery.data.repository

import com.partygallery.data.datasource.ChatDataSource
import com.partygallery.data.mapper.toDomain
import com.partygallery.data.mapper.toDto
import com.partygallery.domain.model.ChatMessage
import com.partygallery.domain.model.ChatRoom
import com.partygallery.domain.model.MessageType
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

/**
 * Implementation of ChatRepository using Firebase Firestore.
 *
 * S2.5-004: ChatRepositoryImpl con Firestore real-time
 */
class ChatRepositoryImpl(
    private val chatDataSource: ChatDataSource,
) : ChatRepository {

    // ============================================
    // Chat Room Operations
    // ============================================

    override suspend fun createChatRoom(
        partyId: String?,
        participants: List<String>,
        isEventChat: Boolean,
    ): Result<ChatRoom> {
        return try {
            val dto = chatDataSource.createChatRoom(partyId, participants, isEventChat)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatRoomById(roomId: String): Result<ChatRoom?> {
        return try {
            val dto = chatDataSource.getChatRoomById(roomId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatRoomForParty(partyId: String): Result<ChatRoom?> {
        return try {
            val dto = chatDataSource.getChatRoomForParty(partyId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPrivateChatRoom(userId1: String, userId2: String): Result<ChatRoom?> {
        return try {
            val dto = chatDataSource.getPrivateChatRoom(userId1, userId2)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserChatRooms(userId: String): Result<List<ChatRoom>> {
        return try {
            val dtos = chatDataSource.getUserChatRooms(userId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChatRoom(roomId: String): Result<Unit> {
        return try {
            chatDataSource.deleteChatRoom(roomId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Message Operations
    // ============================================

    override suspend fun sendMessage(
        roomId: String,
        senderId: String,
        content: String,
        type: MessageType,
        mediaUrl: String?,
        replyToId: String?,
    ): Result<ChatMessage> {
        return try {
            val dto = chatDataSource.sendMessage(
                roomId = roomId,
                senderId = senderId,
                content = content,
                type = type.name,
                mediaUrl = mediaUrl,
                replyToId = replyToId,
            )
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessages(roomId: String, limit: Int, before: Instant?): Result<List<ChatMessage>> {
        return try {
            val beforeTimestamp = before?.toEpochMilliseconds()
            val dtos = chatDataSource.getMessages(roomId, limit, beforeTimestamp)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessage(messageId: String): Result<ChatMessage?> {
        return try {
            val dto = chatDataSource.getMessage(messageId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            chatDataSource.deleteMessage(messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editMessage(messageId: String, newContent: String): Result<ChatMessage> {
        return try {
            val dto = chatDataSource.editMessage(messageId, newContent)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Reactions
    // ============================================

    override suspend fun addReaction(messageId: String, userId: String, emoji: String): Result<Unit> {
        return try {
            chatDataSource.addReaction(messageId, userId, emoji)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeReaction(messageId: String, userId: String, emoji: String): Result<Unit> {
        return try {
            chatDataSource.removeReaction(messageId, userId, emoji)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReactions(messageId: String): Result<Map<String, List<String>>> {
        return try {
            val reactions = chatDataSource.getReactions(messageId)
            Result.success(reactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Read Status
    // ============================================

    override suspend fun markAsRead(roomId: String, userId: String, messageId: String): Result<Unit> {
        return try {
            chatDataSource.markAsRead(roomId, userId, messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(roomId: String, userId: String): Result<Int> {
        return try {
            val count = chatDataSource.getUnreadCount(roomId, userId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLastReadMessageId(roomId: String, userId: String): Result<String?> {
        return try {
            val messageId = chatDataSource.getLastReadMessageId(roomId, userId)
            Result.success(messageId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Participants
    // ============================================

    override suspend fun addParticipant(roomId: String, userId: String): Result<Unit> {
        return try {
            chatDataSource.addParticipant(roomId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeParticipant(roomId: String, userId: String): Result<Unit> {
        return try {
            chatDataSource.removeParticipant(roomId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getParticipants(roomId: String): Result<List<UserSummary>> {
        return try {
            val dtos = chatDataSource.getParticipants(roomId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Typing Indicators
    // ============================================

    override suspend fun setTyping(roomId: String, userId: String, isTyping: Boolean): Result<Unit> {
        return try {
            chatDataSource.setTyping(roomId, userId, isTyping)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTypingUsers(roomId: String): Flow<List<String>> {
        return chatDataSource.observeTypingUsers(roomId)
    }

    // ============================================
    // Observable Flows
    // ============================================

    override fun observeMessages(roomId: String): Flow<List<ChatMessage>> {
        return chatDataSource.observeMessages(roomId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override fun observeNewMessages(roomId: String): Flow<ChatMessage> {
        return chatDataSource.observeNewMessages(roomId).map { dto ->
            dto.toDomain()
        }
    }

    override fun observeChatRooms(userId: String): Flow<List<ChatRoom>> {
        return chatDataSource.observeChatRooms(userId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override fun observeUnreadCount(roomId: String, userId: String): Flow<Int> {
        return chatDataSource.observeUnreadCount(roomId, userId)
    }

    override fun observeTotalUnreadCount(userId: String): Flow<Int> {
        return chatDataSource.observeTotalUnreadCount(userId)
    }
}
