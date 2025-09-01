package com.partygallery.data.repository

import com.partygallery.domain.repository.ChatRepository
import com.partygallery.domain.model.chat.PartyChatRoom
import com.partygallery.domain.model.chat.PartyMessage
import com.partygallery.domain.model.chat.MessageType
import com.partygallery.data.datasource.remote.ChatRemoteDataSource
import com.partygallery.data.datasource.local.ChatLocalDataSource
import com.partygallery.data.mapper.ChatMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val remoteDataSource: ChatRemoteDataSource,
    private val localDataSource: ChatLocalDataSource,
    private val mapper: ChatMapper
) : ChatRepository {

    override suspend fun createChatRoom(chatRoom: PartyChatRoom): Result<PartyChatRoom> = runCatching {
        val remoteChatRoom = remoteDataSource.createChatRoom(mapper.toRemoteChatRoom(chatRoom)).getOrThrow()
        val localChatRoom = mapper.toDomainChatRoom(remoteChatRoom)
        localDataSource.saveChatRoom(localChatRoom)
        localChatRoom
    }

    override suspend fun getChatRoomById(chatRoomId: String): Result<PartyChatRoom?> = runCatching {
        // Try local first
        localDataSource.getChatRoomById(chatRoomId)?.let { return@runCatching it }
        
        // Fallback to remote
        val remoteChatRoom = remoteDataSource.getChatRoomById(chatRoomId).getOrThrow() ?: return@runCatching null
        val domainChatRoom = mapper.toDomainChatRoom(remoteChatRoom)
        localDataSource.saveChatRoom(domainChatRoom)
        domainChatRoom
    }

    override suspend fun updateChatRoom(chatRoom: PartyChatRoom): Result<PartyChatRoom> = runCatching {
        val remoteChatRoom = remoteDataSource.updateChatRoom(mapper.toRemoteChatRoom(chatRoom)).getOrThrow()
        val localChatRoom = mapper.toDomainChatRoom(remoteChatRoom)
        localDataSource.saveChatRoom(localChatRoom)
        localChatRoom
    }

    override suspend fun deleteChatRoom(chatRoomId: String): Result<Unit> = runCatching {
        remoteDataSource.deleteChatRoom(chatRoomId).getOrThrow()
        localDataSource.deleteChatRoom(chatRoomId)
    }

    override suspend fun getUserChatRooms(userId: String): Result<List<PartyChatRoom>> = runCatching {
        val remoteChatRooms = remoteDataSource.getUserChatRooms(userId).getOrThrow()
        val domainChatRooms = remoteChatRooms.map { mapper.toDomainChatRoom(it) }
        localDataSource.saveChatRooms(domainChatRooms)
        domainChatRooms
    }

    override suspend fun getPartyEventChatRoom(eventId: String): Result<PartyChatRoom?> = runCatching {
        val remoteChatRoom = remoteDataSource.getPartyEventChatRoom(eventId).getOrThrow() ?: return@runCatching null
        val domainChatRoom = mapper.toDomainChatRoom(remoteChatRoom)
        localDataSource.saveChatRoom(domainChatRoom)
        domainChatRoom
    }

    override suspend fun createPartyEventChatRoom(eventId: String, participants: List<String>): Result<PartyChatRoom> = runCatching {
        val remoteChatRoom = remoteDataSource.createPartyEventChatRoom(eventId, participants).getOrThrow()
        val domainChatRoom = mapper.toDomainChatRoom(remoteChatRoom)
        localDataSource.saveChatRoom(domainChatRoom)
        domainChatRoom
    }

    override suspend fun addParticipantToChatRoom(chatRoomId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.addParticipantToChatRoom(chatRoomId, userId).getOrThrow()
        localDataSource.addParticipantToChatRoom(chatRoomId, userId)
    }

    override suspend fun removeParticipantFromChatRoom(chatRoomId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.removeParticipantFromChatRoom(chatRoomId, userId).getOrThrow()
        localDataSource.removeParticipantFromChatRoom(chatRoomId, userId)
    }

    override suspend fun sendMessage(message: PartyMessage): Result<PartyMessage> = runCatching {
        // Save locally first for immediate UI update
        localDataSource.saveMessage(message)
        
        // Send to remote
        val remoteMessage = remoteDataSource.sendMessage(mapper.toRemoteMessage(message)).getOrThrow()
        val domainMessage = mapper.toDomainMessage(remoteMessage)
        
        // Update local with server response
        localDataSource.saveMessage(domainMessage)
        domainMessage
    }

    override suspend fun getMessageById(messageId: String): Result<PartyMessage?> = runCatching {
        // Try local first
        localDataSource.getMessageById(messageId)?.let { return@runCatching it }
        
        // Fallback to remote
        val remoteMessage = remoteDataSource.getMessageById(messageId).getOrThrow() ?: return@runCatching null
        val domainMessage = mapper.toDomainMessage(remoteMessage)
        localDataSource.saveMessage(domainMessage)
        domainMessage
    }

    override suspend fun updateMessage(message: PartyMessage): Result<PartyMessage> = runCatching {
        val remoteMessage = remoteDataSource.updateMessage(mapper.toRemoteMessage(message)).getOrThrow()
        val localMessage = mapper.toDomainMessage(remoteMessage)
        localDataSource.saveMessage(localMessage)
        localMessage
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> = runCatching {
        remoteDataSource.deleteMessage(messageId).getOrThrow()
        localDataSource.deleteMessage(messageId)
    }

    override suspend fun getChatRoomMessages(
        chatRoomId: String,
        limit: Int,
        beforeMessageId: String?
    ): Result<List<PartyMessage>> = runCatching {
        val remoteMessages = remoteDataSource.getChatRoomMessages(chatRoomId, limit, beforeMessageId).getOrThrow()
        val domainMessages = remoteMessages.map { mapper.toDomainMessage(it) }
        localDataSource.saveMessages(domainMessages)
        domainMessages
    }

    override suspend fun markMessageAsRead(messageId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.markMessageAsRead(messageId, userId).getOrThrow()
        localDataSource.markMessageAsRead(messageId, userId)
    }

    override suspend fun markChatRoomAsRead(chatRoomId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.markChatRoomAsRead(chatRoomId, userId).getOrThrow()
        localDataSource.markChatRoomAsRead(chatRoomId, userId)
    }

    override suspend fun getUnreadMessagesCount(chatRoomId: String, userId: String): Result<Int> = runCatching {
        remoteDataSource.getUnreadMessagesCount(chatRoomId, userId).getOrThrow()
    }

    override suspend fun addReactionToMessage(messageId: String, userId: String, emoji: String): Result<Unit> = runCatching {
        remoteDataSource.addReactionToMessage(messageId, userId, emoji).getOrThrow()
        localDataSource.addReactionToMessage(messageId, userId, emoji)
    }

    override suspend fun removeReactionFromMessage(messageId: String, userId: String, emoji: String): Result<Unit> = runCatching {
        remoteDataSource.removeReactionFromMessage(messageId, userId, emoji).getOrThrow()
        localDataSource.removeReactionFromMessage(messageId, userId, emoji)
    }

    override suspend fun sendMediaMessage(
        chatRoomId: String,
        senderId: String,
        mediaUrl: String,
        mediaType: MessageType,
        caption: String?
    ): Result<PartyMessage> = runCatching {
        val remoteMessage = remoteDataSource.sendMediaMessage(chatRoomId, senderId, mediaUrl, mediaType, caption).getOrThrow()
        val domainMessage = mapper.toDomainMessage(remoteMessage)
        localDataSource.saveMessage(domainMessage)
        domainMessage
    }

    override suspend fun forwardMessage(messageId: String, toChatRoomId: String, fromUserId: String): Result<PartyMessage> = runCatching {
        val remoteMessage = remoteDataSource.forwardMessage(messageId, toChatRoomId, fromUserId).getOrThrow()
        val domainMessage = mapper.toDomainMessage(remoteMessage)
        localDataSource.saveMessage(domainMessage)
        domainMessage
    }

    override fun observeChatRoom(chatRoomId: String): Flow<PartyChatRoom?> {
        return remoteDataSource.observeChatRoom(chatRoomId).map { remoteChatRoom ->
            remoteChatRoom?.let {
                val domainChatRoom = mapper.toDomainChatRoom(it)
                localDataSource.saveChatRoom(domainChatRoom)
                domainChatRoom
            }
        }
    }

    override fun observeChatRoomMessages(chatRoomId: String): Flow<List<PartyMessage>> {
        return remoteDataSource.observeChatRoomMessages(chatRoomId).map { remoteMessages ->
            val domainMessages = remoteMessages.map { mapper.toDomainMessage(it) }
            localDataSource.saveMessages(domainMessages)
            domainMessages
        }
    }

    override fun observeUserChatRooms(userId: String): Flow<List<PartyChatRoom>> {
        return remoteDataSource.observeUserChatRooms(userId).map { remoteChatRooms ->
            val domainChatRooms = remoteChatRooms.map { mapper.toDomainChatRoom(it) }
            localDataSource.saveChatRooms(domainChatRooms)
            domainChatRooms
        }
    }

    override fun observeUnreadMessagesCount(userId: String): Flow<Int> {
        return remoteDataSource.observeUnreadMessagesCount(userId)
    }
}