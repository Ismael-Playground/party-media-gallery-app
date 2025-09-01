package com.partygallery.domain.repository

import com.partygallery.domain.model.user.Contact
import com.partygallery.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun syncDeviceContacts(): Result<List<Contact>>
    suspend fun getDeviceContacts(): Result<List<Contact>>
    suspend fun searchDeviceContacts(query: String): Result<List<Contact>>
    
    suspend fun findRegisteredUsers(contacts: List<Contact>): Result<List<User>>
    suspend fun matchContactsWithUsers(contacts: List<Contact>): Result<Map<Contact, User?>>
    
    suspend fun saveContactSyncPermission(granted: Boolean): Result<Unit>
    suspend fun getContactSyncPermission(): Result<Boolean>
    
    fun observeRegisteredContacts(): Flow<List<User>>
    
    suspend fun inviteContactToApp(contact: Contact, message: String? = null): Result<Unit>
    suspend fun getInvitedContacts(): Result<List<Contact>>
}