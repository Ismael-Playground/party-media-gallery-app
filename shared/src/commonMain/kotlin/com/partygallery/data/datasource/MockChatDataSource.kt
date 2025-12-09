package com.partygallery.data.datasource

import com.partygallery.data.dto.ChatMessageDto
import com.partygallery.data.dto.ChatMessagePreviewDto
import com.partygallery.data.dto.ChatRoomDto
import com.partygallery.data.dto.UserSummaryDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Mock implementation of ChatDataSource for development and testing.
 * Will be replaced with FirebaseChatDataSource in production.
 *
 * S2.5-004: ChatRepositoryImpl con Firestore real-time
 */
class MockChatDataSource : ChatDataSource {

    // In-memory storage
    private val chatRooms = MutableStateFlow<Map<String, ChatRoomDto>>(createInitialChatRooms())
    private val messages = MutableStateFlow<Map<String, List<ChatMessageDto>>>(createInitialMessages())
    private val readStatus = MutableStateFlow<Map<String, Map<String, String>>>(
        emptyMap(),
    ) // roomId -> (userId -> lastReadMessageId)
    private val typingUsers = MutableStateFlow<Map<String, Set<String>>>(emptyMap()) // roomId -> Set<userId>

    // Simulate network delay
    private suspend fun simulateNetworkDelay() {
        delay(200)
    }

    // ============================================
    // Chat Room Operations
    // ============================================

    override suspend fun createChatRoom(
        partyId: String?,
        participants: List<String>,
        isEventChat: Boolean,
    ): ChatRoomDto {
        simulateNetworkDelay()
        val now = Clock.System.now().toEpochMilliseconds()
        val newRoom = ChatRoomDto(
            id = "room_${Clock.System.now().toEpochMilliseconds()}",
            partyEventId = partyId,
            participants = participants,
            participantDetails = participants.mapNotNull { userId -> getMockUserSummary(userId) },
            isEventChat = isEventChat,
            createdAt = now,
        )
        chatRooms.value = chatRooms.value + (newRoom.id to newRoom)
        messages.value = messages.value + (newRoom.id to emptyList())
        return newRoom
    }

    override suspend fun getChatRoomById(roomId: String): ChatRoomDto? {
        simulateNetworkDelay()
        return chatRooms.value[roomId]
    }

    override suspend fun getChatRoomForParty(partyId: String): ChatRoomDto? {
        simulateNetworkDelay()
        return chatRooms.value.values.find { it.partyEventId == partyId && it.isEventChat }
    }

    override suspend fun getPrivateChatRoom(userId1: String, userId2: String): ChatRoomDto? {
        simulateNetworkDelay()
        return chatRooms.value.values.find { room ->
            !room.isEventChat &&
                room.participants.size == 2 &&
                room.participants.containsAll(listOf(userId1, userId2))
        }
    }

    override suspend fun getUserChatRooms(userId: String): List<ChatRoomDto> {
        simulateNetworkDelay()
        return chatRooms.value.values
            .filter { it.participants.contains(userId) }
            .sortedByDescending { it.lastMessage?.sentAt ?: it.createdAt }
    }

    override suspend fun deleteChatRoom(roomId: String) {
        simulateNetworkDelay()
        chatRooms.value = chatRooms.value - roomId
        messages.value = messages.value - roomId
    }

    // ============================================
    // Message Operations
    // ============================================

    override suspend fun sendMessage(
        roomId: String,
        senderId: String,
        content: String,
        type: String,
        mediaUrl: String?,
        replyToId: String?,
    ): ChatMessageDto {
        simulateNetworkDelay()
        val now = Clock.System.now().toEpochMilliseconds()
        val sender = getMockUserSummary(senderId)

        val newMessage = ChatMessageDto(
            id = "msg_${Clock.System.now().toEpochMilliseconds()}",
            chatRoomId = roomId,
            senderId = senderId,
            sender = sender,
            content = content,
            type = type,
            mediaUrl = mediaUrl,
            replyToId = replyToId,
            createdAt = now,
        )

        // Add message to room
        val roomMessages = messages.value[roomId]?.toMutableList() ?: mutableListOf()
        roomMessages.add(0, newMessage) // Add at beginning (most recent first)
        messages.value = messages.value + (roomId to roomMessages)

        // Update room's last message
        chatRooms.value[roomId]?.let { room ->
            val updatedRoom = room.copy(
                lastMessage = ChatMessagePreviewDto(
                    text = content,
                    senderName = sender?.let { "${it.firstName} ${it.lastName}".trim() } ?: "",
                    sentAt = now,
                ),
                updatedAt = now,
            )
            chatRooms.value = chatRooms.value + (roomId to updatedRoom)
        }

        return newMessage
    }

    override suspend fun getMessages(roomId: String, limit: Int, beforeTimestamp: Long?): List<ChatMessageDto> {
        simulateNetworkDelay()
        val roomMessages = messages.value[roomId] ?: emptyList()
        return if (beforeTimestamp != null) {
            roomMessages.filter { it.createdAt < beforeTimestamp }.take(limit)
        } else {
            roomMessages.take(limit)
        }
    }

    override suspend fun getMessage(messageId: String): ChatMessageDto? {
        simulateNetworkDelay()
        return messages.value.values.flatten().find { it.id == messageId }
    }

    override suspend fun deleteMessage(messageId: String) {
        simulateNetworkDelay()
        for ((roomId, roomMessages) in messages.value) {
            val updatedMessages = roomMessages.filterNot { it.id == messageId }
            if (updatedMessages.size != roomMessages.size) {
                messages.value = messages.value + (roomId to updatedMessages)
                break
            }
        }
    }

    override suspend fun editMessage(messageId: String, newContent: String): ChatMessageDto {
        simulateNetworkDelay()
        val now = Clock.System.now().toEpochMilliseconds()

        for ((roomId, roomMessages) in messages.value) {
            val messageIndex = roomMessages.indexOfFirst { it.id == messageId }
            if (messageIndex >= 0) {
                val updatedMessage = roomMessages[messageIndex].copy(
                    content = newContent,
                    updatedAt = now,
                )
                val updatedMessages = roomMessages.toMutableList()
                updatedMessages[messageIndex] = updatedMessage
                messages.value = messages.value + (roomId to updatedMessages)
                return updatedMessage
            }
        }
        throw IllegalStateException("Message not found: $messageId")
    }

    // ============================================
    // Reactions
    // ============================================

    override suspend fun addReaction(messageId: String, userId: String, emoji: String) {
        simulateNetworkDelay()
        updateMessageReaction(messageId, emoji, userId, add = true)
    }

    override suspend fun removeReaction(messageId: String, userId: String, emoji: String) {
        simulateNetworkDelay()
        updateMessageReaction(messageId, emoji, userId, add = false)
    }

    private fun updateMessageReaction(messageId: String, emoji: String, userId: String, add: Boolean) {
        for ((roomId, roomMessages) in messages.value) {
            val messageIndex = roomMessages.indexOfFirst { it.id == messageId }
            if (messageIndex >= 0) {
                val message = roomMessages[messageIndex]
                val reactions = message.reactions.toMutableMap()
                val emojiUsers = reactions[emoji]?.toMutableList() ?: mutableListOf()

                if (add && userId !in emojiUsers) {
                    emojiUsers.add(userId)
                } else if (!add) {
                    emojiUsers.remove(userId)
                }

                if (emojiUsers.isEmpty()) {
                    reactions.remove(emoji)
                } else {
                    reactions[emoji] = emojiUsers
                }

                val updatedMessage = message.copy(reactions = reactions)
                val updatedMessages = roomMessages.toMutableList()
                updatedMessages[messageIndex] = updatedMessage
                messages.value = messages.value + (roomId to updatedMessages)
                break
            }
        }
    }

    override suspend fun getReactions(messageId: String): Map<String, List<String>> {
        simulateNetworkDelay()
        val message = messages.value.values.flatten().find { it.id == messageId }
        return message?.reactions ?: emptyMap()
    }

    // ============================================
    // Read Status
    // ============================================

    override suspend fun markAsRead(roomId: String, userId: String, messageId: String) {
        simulateNetworkDelay()
        val roomReadStatus = readStatus.value[roomId]?.toMutableMap() ?: mutableMapOf()
        roomReadStatus[userId] = messageId
        readStatus.value = readStatus.value + (roomId to roomReadStatus)

        // Update message read_by
        val roomMessages = messages.value[roomId]
        if (roomMessages != null) {
            val messageIndex = roomMessages.indexOfFirst { it.id == messageId }
            if (messageIndex >= 0) {
                val message = roomMessages[messageIndex]
                if (userId !in message.readBy) {
                    val updatedReadBy = message.readBy + userId
                    val updatedMessage = message.copy(readBy = updatedReadBy, isRead = true)
                    val updatedMessages = roomMessages.toMutableList()
                    updatedMessages[messageIndex] = updatedMessage
                    messages.value = messages.value + (roomId to updatedMessages)
                }
            }
        }
    }

    override suspend fun getUnreadCount(roomId: String, userId: String): Int {
        simulateNetworkDelay()
        val lastReadMessageId = readStatus.value[roomId]?.get(userId)
        val roomMessages = messages.value[roomId] ?: return 0

        if (lastReadMessageId == null) {
            return roomMessages.count { it.senderId != userId }
        }

        val lastReadIndex = roomMessages.indexOfFirst { it.id == lastReadMessageId }
        if (lastReadIndex < 0) return 0

        return roomMessages.take(lastReadIndex).count { it.senderId != userId }
    }

    override suspend fun getLastReadMessageId(roomId: String, userId: String): String? {
        simulateNetworkDelay()
        return readStatus.value[roomId]?.get(userId)
    }

    // ============================================
    // Participants
    // ============================================

    override suspend fun addParticipant(roomId: String, userId: String) {
        simulateNetworkDelay()
        chatRooms.value[roomId]?.let { room ->
            if (userId !in room.participants) {
                val updatedParticipants = room.participants + userId
                val updatedDetails = room.participantDetails + listOfNotNull(getMockUserSummary(userId))
                val updatedRoom = room.copy(
                    participants = updatedParticipants,
                    participantDetails = updatedDetails,
                )
                chatRooms.value = chatRooms.value + (roomId to updatedRoom)
            }
        }
    }

    override suspend fun removeParticipant(roomId: String, userId: String) {
        simulateNetworkDelay()
        chatRooms.value[roomId]?.let { room ->
            val updatedParticipants = room.participants - userId
            val updatedDetails = room.participantDetails.filterNot { it.id == userId }
            val updatedRoom = room.copy(
                participants = updatedParticipants,
                participantDetails = updatedDetails,
            )
            chatRooms.value = chatRooms.value + (roomId to updatedRoom)
        }
    }

    override suspend fun getParticipants(roomId: String): List<UserSummaryDto> {
        simulateNetworkDelay()
        return chatRooms.value[roomId]?.participantDetails ?: emptyList()
    }

    // ============================================
    // Typing Indicators
    // ============================================

    override suspend fun setTyping(roomId: String, userId: String, isTyping: Boolean) {
        simulateNetworkDelay()
        val roomTyping = typingUsers.value[roomId]?.toMutableSet() ?: mutableSetOf()
        if (isTyping) {
            roomTyping.add(userId)
        } else {
            roomTyping.remove(userId)
        }
        typingUsers.value = typingUsers.value + (roomId to roomTyping)
    }

    override fun observeTypingUsers(roomId: String): Flow<List<String>> {
        return typingUsers.map { it[roomId]?.toList() ?: emptyList() }
    }

    // ============================================
    // Real-time Observers
    // ============================================

    override fun observeMessages(roomId: String): Flow<List<ChatMessageDto>> {
        return messages.map { it[roomId] ?: emptyList() }
    }

    override fun observeNewMessages(roomId: String): Flow<ChatMessageDto> {
        return flow {
            var lastCount = messages.value[roomId]?.size ?: 0
            messages.collect { allMessages ->
                val roomMessages = allMessages[roomId] ?: emptyList()
                if (roomMessages.size > lastCount && roomMessages.isNotEmpty()) {
                    emit(roomMessages.first())
                }
                lastCount = roomMessages.size
            }
        }
    }

    override fun observeChatRooms(userId: String): Flow<List<ChatRoomDto>> {
        return chatRooms.map { rooms ->
            rooms.values
                .filter { it.participants.contains(userId) }
                .sortedByDescending { it.lastMessage?.sentAt ?: it.createdAt }
        }
    }

    override fun observeUnreadCount(roomId: String, userId: String): Flow<Int> {
        return flow {
            emit(getUnreadCount(roomId, userId))
        }
    }

    override fun observeTotalUnreadCount(userId: String): Flow<Int> {
        return flow {
            val rooms = getUserChatRooms(userId)
            val total = rooms.sumOf { getUnreadCount(it.id, userId) }
            emit(total)
        }
    }

    // ============================================
    // Mock Data Helpers
    // ============================================

    private fun getMockUserSummary(userId: String): UserSummaryDto? {
        return when (userId) {
            "user1" -> UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
                avatarUrl = "https://i.pravatar.cc/150?u=user1",
                isVerified = true,
            )
            "user2" -> UserSummaryDto(
                id = "user2",
                username = "djmaster",
                firstName = "DJ",
                lastName = "Master",
                avatarUrl = "https://i.pravatar.cc/150?u=user2",
            )
            "user3" -> UserSummaryDto(
                id = "user3",
                username = "clubqueen",
                firstName = "Club",
                lastName = "Queen",
                avatarUrl = "https://i.pravatar.cc/150?u=user3",
            )
            "user4" -> UserSummaryDto(
                id = "user4",
                username = "nightowl",
                firstName = "Night",
                lastName = "Owl",
                avatarUrl = "https://i.pravatar.cc/150?u=user4",
            )
            else -> null
        }
    }

    // ============================================
    // Initial Mock Data
    // ============================================

    private fun createInitialChatRooms(): Map<String, ChatRoomDto> {
        val now = Clock.System.now().toEpochMilliseconds()
        val hourInMillis = 60 * 60 * 1000L

        val room1 = ChatRoomDto(
            id = "room1",
            partyEventId = "party2",
            name = "Techno Night Chat",
            participants = listOf("user1", "user2", "user3"),
            participantDetails = listOf(
                getMockUserSummary("user1")!!,
                getMockUserSummary("user2")!!,
                getMockUserSummary("user3")!!,
            ),
            isEventChat = true,
            lastMessage = ChatMessagePreviewDto(
                text = "This party is lit!",
                senderName = "DJ Master",
                sentAt = now - (30 * 60 * 1000L),
            ),
            unreadCount = 3,
            createdAt = now - (24 * hourInMillis),
        )

        val room2 = ChatRoomDto(
            id = "room2",
            participants = listOf("user1", "user2"),
            participantDetails = listOf(
                getMockUserSummary("user1")!!,
                getMockUserSummary("user2")!!,
            ),
            isEventChat = false,
            lastMessage = ChatMessagePreviewDto(
                text = "See you at the party!",
                senderName = "DJ Master",
                sentAt = now - hourInMillis,
            ),
            unreadCount = 1,
            createdAt = now - (48 * hourInMillis),
        )

        val room3 = ChatRoomDto(
            id = "room3",
            partyEventId = "party1",
            name = "Rooftop Bash Squad",
            participants = listOf("user1", "user3", "user4"),
            participantDetails = listOf(
                getMockUserSummary("user1")!!,
                getMockUserSummary("user3")!!,
                getMockUserSummary("user4")!!,
            ),
            isEventChat = true,
            lastMessage = ChatMessagePreviewDto(
                text = "Can't wait for tonight!",
                senderName = "Night Owl",
                sentAt = now - (2 * hourInMillis),
            ),
            createdAt = now - (72 * hourInMillis),
        )

        return mapOf(
            room1.id to room1,
            room2.id to room2,
            room3.id to room3,
        )
    }

    private fun createInitialMessages(): Map<String, List<ChatMessageDto>> {
        val now = Clock.System.now().toEpochMilliseconds()
        val minuteInMillis = 60 * 1000L

        // Messages for room1 (Techno Night Chat)
        val room1Messages = listOf(
            ChatMessageDto(
                id = "msg1",
                chatRoomId = "room1",
                senderId = "user2",
                sender = getMockUserSummary("user2"),
                content = "This party is lit!",
                type = "TEXT",
                reactions = mapOf("fire" to listOf("user1", "user3")),
                createdAt = now - (30 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg2",
                chatRoomId = "room1",
                senderId = "user3",
                sender = getMockUserSummary("user3"),
                content = "Best techno set I've heard in ages!",
                type = "TEXT",
                createdAt = now - (35 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg3",
                chatRoomId = "room1",
                senderId = "user1",
                sender = getMockUserSummary("user1"),
                content = "Who's coming to the afterparty?",
                type = "TEXT",
                reactions = mapOf("thumbsup" to listOf("user2")),
                createdAt = now - (40 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg4",
                chatRoomId = "room1",
                senderId = "user2",
                sender = getMockUserSummary("user2"),
                content = "Check out this moment!",
                type = "IMAGE",
                mediaUrl = "https://picsum.photos/seed/msg4/800/600",
                isPartyMoment = true,
                createdAt = now - (45 * minuteInMillis),
            ),
        )

        // Messages for room2 (Private chat)
        val room2Messages = listOf(
            ChatMessageDto(
                id = "msg5",
                chatRoomId = "room2",
                senderId = "user2",
                sender = getMockUserSummary("user2"),
                content = "See you at the party!",
                type = "TEXT",
                createdAt = now - (60 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg6",
                chatRoomId = "room2",
                senderId = "user1",
                sender = getMockUserSummary("user1"),
                content = "Yeah! What time are you arriving?",
                type = "TEXT",
                createdAt = now - (65 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg7",
                chatRoomId = "room2",
                senderId = "user2",
                sender = getMockUserSummary("user2"),
                content = "Around 11pm, doors open at 10",
                type = "TEXT",
                createdAt = now - (70 * minuteInMillis),
            ),
        )

        // Messages for room3 (Rooftop Bash Squad)
        val room3Messages = listOf(
            ChatMessageDto(
                id = "msg8",
                chatRoomId = "room3",
                senderId = "user4",
                sender = getMockUserSummary("user4"),
                content = "Can't wait for tonight!",
                type = "TEXT",
                createdAt = now - (2 * 60 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg9",
                chatRoomId = "room3",
                senderId = "user3",
                sender = getMockUserSummary("user3"),
                content = "I heard they're bringing a special guest DJ",
                type = "TEXT",
                reactions = mapOf("heart" to listOf("user1", "user4")),
                createdAt = now - (3 * 60 * minuteInMillis),
            ),
            ChatMessageDto(
                id = "msg10",
                chatRoomId = "room3",
                senderId = "user1",
                sender = getMockUserSummary("user1"),
                content = "Let's meet at the entrance at 9pm",
                type = "TEXT",
                createdAt = now - (4 * 60 * minuteInMillis),
            ),
        )

        return mapOf(
            "room1" to room1Messages,
            "room2" to room2Messages,
            "room3" to room3Messages,
        )
    }
}
