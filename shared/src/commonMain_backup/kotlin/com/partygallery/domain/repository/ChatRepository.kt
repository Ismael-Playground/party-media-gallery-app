package com.partygallery.domain.repository

import com.partygallery.domain.model.chat.PartyChatRoom
import com.partygallery.domain.model.chat.PartyMessage
import com.partygallery.domain.model.chat.MessageType
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createChatRoom(chatRoom: PartyChatRoom): Result<PartyChatRoom>
    suspend fun getChatRoomById(chatRoomId: String): Result<PartyChatRoom?>
    suspend fun updateChatRoom(chatRoom: PartyChatRoom): Result<PartyChatRoom>
    suspend fun deleteChatRoom(chatRoomId: String): Result<Unit>
    
    suspend fun getUserChatRooms(userId: String): Result<List<PartyChatRoom>>
    suspend fun getPartyEventChatRoom(eventId: String): Result<PartyChatRoom?>
    suspend fun createPartyEventChatRoom(eventId: String, participants: List<String>): Result<PartyChatRoom>
    
    suspend fun addParticipantToChatRoom(chatRoomId: String, userId: String): Result<Unit>
    suspend fun removeParticipantFromChatRoom(chatRoomId: String, userId: String): Result<Unit>
    
    suspend fun sendMessage(message: PartyMessage): Result<PartyMessage>
    suspend fun getMessageById(messageId: String): Result<PartyMessage?>
    suspend fun updateMessage(message: PartyMessage): Result<PartyMessage>
    suspend fun deleteMessage(messageId: String): Result<Unit>
    
    suspend fun getChatRoomMessages(
        chatRoomId: String,
        limit: Int = 50,
        beforeMessageId: String? = null
    ): Result<List<PartyMessage>>
    
    suspend fun markMessageAsRead(messageId: String, userId: String): Result<Unit>
    suspend fun markChatRoomAsRead(chatRoomId: String, userId: String): Result<Unit>
    suspend fun getUnreadMessagesCount(chatRoomId: String, userId: String): Result<Int>
    
    suspend fun addReactionToMessage(messageId: String, userId: String, emoji: String): Result<Unit>
    suspend fun removeReactionFromMessage(messageId: String, userId: String, emoji: String): Result<Unit>
    
    suspend fun sendMediaMessage(
        chatRoomId: String,
        senderId: String,
        mediaUrl: String,
        mediaType: MessageType,
        caption: String? = null
    ): Result<PartyMessage>
    
    suspend fun forwardMessage(messageId: String, toChatRoomId: String, fromUserId: String): Result<PartyMessage>
    
    fun observeChatRoom(chatRoomId: String): Flow<PartyChatRoom?>
    fun observeChatRoomMessages(chatRoomId: String): Flow<List<PartyMessage>>
    fun observeUserChatRooms(userId: String): Flow<List<PartyChatRoom>>
    fun observeUnreadMessagesCount(userId: String): Flow<Int>
}