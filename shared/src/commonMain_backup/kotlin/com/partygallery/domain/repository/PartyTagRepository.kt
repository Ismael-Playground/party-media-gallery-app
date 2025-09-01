package com.partygallery.domain.repository

import com.partygallery.domain.model.party.PartyTag
import kotlinx.coroutines.flow.Flow

interface PartyTagRepository {
    suspend fun createPartyTag(tag: PartyTag): Result<PartyTag>
    suspend fun getPartyTagById(tagId: String): Result<PartyTag?>
    suspend fun getPartyTagByName(name: String): Result<PartyTag?>
    suspend fun updatePartyTag(tag: PartyTag): Result<PartyTag>
    suspend fun deletePartyTag(tagId: String): Result<Unit>
    
    suspend fun getAllPartyTags(): Result<List<PartyTag>>
    suspend fun getPopularPartyTags(limit: Int = 50): Result<List<PartyTag>>
    suspend fun getTrendingPartyTags(limit: Int = 20): Result<List<PartyTag>>
    suspend fun searchPartyTags(query: String, limit: Int = 20): Result<List<PartyTag>>
    
    suspend fun getPartyTagsByCategory(category: String): Result<List<PartyTag>>
    suspend fun getMusicGenreTags(): Result<List<PartyTag>>
    suspend fun getPartyTypeTags(): Result<List<PartyTag>>
    
    suspend fun incrementTagUsage(tagId: String): Result<Unit>
    suspend fun getTagUsageCount(tagId: String): Result<Int>
    
    suspend fun getUserPartyTags(userId: String): Result<List<PartyTag>>
    suspend fun saveUserPartyTags(userId: String, tagIds: List<String>): Result<Unit>
    
    fun observePopularPartyTags(): Flow<List<PartyTag>>
    fun observeTrendingPartyTags(): Flow<List<PartyTag>>
    fun observeUserPartyTags(userId: String): Flow<List<PartyTag>>
}