package com.partygallery.data.repository

import com.partygallery.domain.repository.ContactRepository
import com.partygallery.domain.model.user.Contact
import com.partygallery.domain.model.user.User
import com.partygallery.data.datasource.local.ContactLocalDataSource
import com.partygallery.data.datasource.remote.ContactRemoteDataSource
import com.partygallery.data.datasource.local.UserLocalDataSource
import com.partygallery.data.mapper.ContactMapper
import kotlinx.coroutines.flow.Flow

class ContactRepositoryImpl(
    private val localDataSource: ContactLocalDataSource,
    private val remoteDataSource: ContactRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val mapper: ContactMapper
) : ContactRepository {

    override suspend fun syncDeviceContacts(): Result<List<Contact>> = runCatching {
        val deviceContacts = localDataSource.getDeviceContacts().getOrThrow()
        val processedContacts = deviceContacts.map { mapper.toDomainContact(it) }
        
        // Save to local storage
        localDataSource.saveContacts(processedContacts).getOrThrow()
        
        // Send to backend for user matching
        remoteDataSource.syncContacts(processedContacts).getOrThrow()
        
        processedContacts
    }

    override suspend fun getDeviceContacts(): Result<List<Contact>> = runCatching {
        localDataSource.getDeviceContacts().getOrThrow().map { mapper.toDomainContact(it) }
    }

    override suspend fun searchDeviceContacts(query: String): Result<List<Contact>> = runCatching {
        localDataSource.searchContacts(query).getOrThrow().map { mapper.toDomainContact(it) }
    }

    override suspend fun findRegisteredUsers(contacts: List<Contact>): Result<List<User>> = runCatching {
        val remoteContacts = contacts.map { mapper.toRemoteContact(it) }
        remoteDataSource.findRegisteredUsers(remoteContacts).getOrThrow()
    }

    override suspend fun matchContactsWithUsers(contacts: List<Contact>): Result<Map<Contact, User?>> = runCatching {
        val remoteContacts = contacts.map { mapper.toRemoteContact(it) }
        val matches = remoteDataSource.matchContactsWithUsers(remoteContacts).getOrThrow()
        
        // Convert back to domain models
        contacts.associateWith { contact ->
            val remoteContact = mapper.toRemoteContact(contact)
            matches[remoteContact]
        }
    }

    override suspend fun saveContactSyncPermission(granted: Boolean): Result<Unit> = runCatching {
        localDataSource.saveContactSyncPermission(granted).getOrThrow()
    }

    override suspend fun getContactSyncPermission(): Result<Boolean> = runCatching {
        localDataSource.getContactSyncPermission().getOrThrow()
    }

    override fun observeRegisteredContacts(): Flow<List<User>> {
        return localDataSource.observeRegisteredContacts()
    }

    override suspend fun inviteContactToApp(contact: Contact, message: String?): Result<Unit> = runCatching {
        val remoteContact = mapper.toRemoteContact(contact)
        remoteDataSource.inviteContact(remoteContact, message).getOrThrow()
        localDataSource.markContactAsInvited(contact).getOrThrow()
    }

    override suspend fun getInvitedContacts(): Result<List<Contact>> = runCatching {
        localDataSource.getInvitedContacts().getOrThrow().map { mapper.toDomainContact(it) }
    }
}