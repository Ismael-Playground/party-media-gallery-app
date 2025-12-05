# Database Schema - Party Gallery

Esquema de base de datos para Party Gallery usando Firebase Firestore (NoSQL) y SQLDelight (local cache).

## Arquitectura de Datos

```
┌─────────────────────────────────────────────────────────┐
│                    Firebase Cloud                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐ │
│  │  Firestore  │  │   Storage   │  │ Cloud Functions │ │
│  │  (NoSQL DB) │  │   (Media)   │  │   (Backend)     │ │
│  └──────┬──────┘  └──────┬──────┘  └────────┬────────┘ │
└─────────┼────────────────┼──────────────────┼──────────┘
          │                │                  │
          └────────────────┼──────────────────┘
                           │
┌──────────────────────────┼──────────────────────────────┐
│         Party Gallery App (Kotlin Multiplatform)         │
│  ┌─────────────────────────────────────────────────────┐│
│  │               Repository Layer                       ││
│  │  ┌───────────────┐        ┌───────────────────────┐ ││
│  │  │ Remote Source │◄──────►│    Local Cache        │ ││
│  │  │  (Firestore)  │        │    (SQLDelight)       │ ││
│  │  └───────────────┘        └───────────────────────┘ ││
│  └─────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

---

## Firestore Collections

### Diagrama de Relaciones

```
┌───────────┐       ┌─────────────────┐       ┌───────────────┐
│   users   │──1:N──│  party_events   │──1:N──│ media_content │
└─────┬─────┘       └────────┬────────┘       └───────────────┘
      │                      │
      │1:N                   │1:N
      ▼                      ▼
┌─────────────┐       ┌──────────────┐
│ user_follows│       │  attendees   │
└─────────────┘       │ (subcoll)    │
                      └──────────────┘
      │
      │1:N
      ▼
┌─────────────┐       ┌──────────────┐
│ chat_rooms  │──1:N──│   messages   │
└─────────────┘       │  (subcoll)   │
                      └──────────────┘
```

---

## SQLDelight Schema (Local Cache)

### users.sq

```sql
CREATE TABLE UserEntity (
    id TEXT NOT NULL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    avatarUrl TEXT,
    bio TEXT,
    followersCount INTEGER NOT NULL DEFAULT 0,
    followingCount INTEGER NOT NULL DEFAULT 0,
    partiesHostedCount INTEGER NOT NULL DEFAULT 0,
    lastSyncedAt INTEGER NOT NULL,
    createdAt INTEGER NOT NULL
);

CREATE INDEX idx_user_username ON UserEntity(username);

-- Queries
selectById:
SELECT * FROM UserEntity WHERE id = ?;

selectByUsername:
SELECT * FROM UserEntity WHERE username = ?;

insertOrReplace:
INSERT OR REPLACE INTO UserEntity(
    id, username, email, firstName, lastName,
    avatarUrl, bio, followersCount, followingCount,
    partiesHostedCount, lastSyncedAt, createdAt
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

deleteById:
DELETE FROM UserEntity WHERE id = ?;

selectAll:
SELECT * FROM UserEntity ORDER BY username;
```

### party_events.sq

```sql
CREATE TABLE PartyEventEntity (
    id TEXT NOT NULL PRIMARY KEY,
    hostId TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    venueName TEXT NOT NULL,
    venueAddress TEXT NOT NULL,
    venueLatitude REAL NOT NULL,
    venueLongitude REAL NOT NULL,
    startDateTime INTEGER NOT NULL,
    endDateTime INTEGER NOT NULL,
    coverImageUrl TEXT,
    status TEXT NOT NULL DEFAULT 'planned',
    privacyType TEXT NOT NULL DEFAULT 'public',
    maxAttendees INTEGER,
    attendeesCount INTEGER NOT NULL DEFAULT 0,
    mediaCount INTEGER NOT NULL DEFAULT 0,
    lastSyncedAt INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (hostId) REFERENCES UserEntity(id)
);

CREATE INDEX idx_party_host ON PartyEventEntity(hostId);
CREATE INDEX idx_party_status ON PartyEventEntity(status);
CREATE INDEX idx_party_start ON PartyEventEntity(startDateTime DESC);

-- Queries
selectById:
SELECT * FROM PartyEventEntity WHERE id = ?;

selectByHost:
SELECT * FROM PartyEventEntity
WHERE hostId = ?
ORDER BY startDateTime DESC;

selectUpcoming:
SELECT * FROM PartyEventEntity
WHERE status = 'planned' AND startDateTime > ?
ORDER BY startDateTime ASC
LIMIT ?;

selectLive:
SELECT * FROM PartyEventEntity
WHERE status = 'live'
ORDER BY startDateTime DESC;

selectRecent:
SELECT * FROM PartyEventEntity
WHERE status IN ('planned', 'live')
ORDER BY createdAt DESC
LIMIT ?;

insertOrReplace:
INSERT OR REPLACE INTO PartyEventEntity(
    id, hostId, title, description, venueName, venueAddress,
    venueLatitude, venueLongitude, startDateTime, endDateTime,
    coverImageUrl, status, privacyType, maxAttendees,
    attendeesCount, mediaCount, lastSyncedAt, createdAt
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

deleteById:
DELETE FROM PartyEventEntity WHERE id = ?;

updateStatus:
UPDATE PartyEventEntity SET status = ?, lastSyncedAt = ? WHERE id = ?;
```

### party_tags.sq

```sql
CREATE TABLE PartyTagEntity (
    partyId TEXT NOT NULL,
    tag TEXT NOT NULL,
    PRIMARY KEY (partyId, tag),
    FOREIGN KEY (partyId) REFERENCES PartyEventEntity(id) ON DELETE CASCADE
);

CREATE INDEX idx_tag ON PartyTagEntity(tag);

-- Queries
selectByParty:
SELECT tag FROM PartyTagEntity WHERE partyId = ?;

insertTag:
INSERT OR IGNORE INTO PartyTagEntity(partyId, tag) VALUES (?, ?);

deleteByParty:
DELETE FROM PartyTagEntity WHERE partyId = ?;

searchByTag:
SELECT DISTINCT partyId FROM PartyTagEntity WHERE tag = ?;
```

### media_content.sq

```sql
CREATE TABLE MediaContentEntity (
    id TEXT NOT NULL PRIMARY KEY,
    uploaderId TEXT NOT NULL,
    partyEventId TEXT NOT NULL,
    type TEXT NOT NULL,
    url TEXT NOT NULL,
    thumbnailUrl TEXT,
    partyMood TEXT NOT NULL,
    caption TEXT,
    duration INTEGER,
    width INTEGER,
    height INTEGER,
    likesCount INTEGER NOT NULL DEFAULT 0,
    commentsCount INTEGER NOT NULL DEFAULT 0,
    localPath TEXT,
    uploadStatus TEXT NOT NULL DEFAULT 'uploaded',
    lastSyncedAt INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (uploaderId) REFERENCES UserEntity(id),
    FOREIGN KEY (partyEventId) REFERENCES PartyEventEntity(id)
);

CREATE INDEX idx_media_party ON MediaContentEntity(partyEventId);
CREATE INDEX idx_media_uploader ON MediaContentEntity(uploaderId);
CREATE INDEX idx_media_mood ON MediaContentEntity(partyMood);

-- Queries
selectById:
SELECT * FROM MediaContentEntity WHERE id = ?;

selectByParty:
SELECT * FROM MediaContentEntity
WHERE partyEventId = ?
ORDER BY createdAt DESC;

selectByUploader:
SELECT * FROM MediaContentEntity
WHERE uploaderId = ?
ORDER BY createdAt DESC;

selectPending:
SELECT * FROM MediaContentEntity
WHERE uploadStatus = 'pending'
ORDER BY createdAt ASC;

insertOrReplace:
INSERT OR REPLACE INTO MediaContentEntity(
    id, uploaderId, partyEventId, type, url, thumbnailUrl,
    partyMood, caption, duration, width, height,
    likesCount, commentsCount, localPath, uploadStatus,
    lastSyncedAt, createdAt
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

updateUploadStatus:
UPDATE MediaContentEntity SET uploadStatus = ?, url = ?, lastSyncedAt = ? WHERE id = ?;
```

### chat_messages.sq

```sql
CREATE TABLE ChatMessageEntity (
    id TEXT NOT NULL PRIMARY KEY,
    roomId TEXT NOT NULL,
    senderId TEXT NOT NULL,
    content TEXT NOT NULL,
    type TEXT NOT NULL DEFAULT 'text',
    mediaUrl TEXT,
    replyToId TEXT,
    isRead INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (senderId) REFERENCES UserEntity(id)
);

CREATE INDEX idx_message_room ON ChatMessageEntity(roomId);
CREATE INDEX idx_message_created ON ChatMessageEntity(roomId, createdAt DESC);

-- Queries
selectByRoom:
SELECT * FROM ChatMessageEntity
WHERE roomId = ?
ORDER BY createdAt DESC
LIMIT ?;

selectUnread:
SELECT * FROM ChatMessageEntity
WHERE roomId = ? AND isRead = 0
ORDER BY createdAt ASC;

insertMessage:
INSERT INTO ChatMessageEntity(
    id, roomId, senderId, content, type, mediaUrl, replyToId, isRead, createdAt
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);

markAsRead:
UPDATE ChatMessageEntity SET isRead = 1 WHERE roomId = ? AND isRead = 0;
```

### offline_actions.sq

```sql
CREATE TABLE OfflineActionEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    actionType TEXT NOT NULL,
    entityType TEXT NOT NULL,
    entityId TEXT NOT NULL,
    payload TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending',
    retryCount INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL
);

CREATE INDEX idx_action_status ON OfflineActionEntity(status);

-- Queries
selectPending:
SELECT * FROM OfflineActionEntity
WHERE status = 'pending'
ORDER BY createdAt ASC;

insert:
INSERT INTO OfflineActionEntity(
    actionType, entityType, entityId, payload, status, retryCount, createdAt
) VALUES (?, ?, ?, ?, ?, ?, ?);

updateStatus:
UPDATE OfflineActionEntity SET status = ?, retryCount = retryCount + 1 WHERE id = ?;

deleteCompleted:
DELETE FROM OfflineActionEntity WHERE status = 'completed';

deleteById:
DELETE FROM OfflineActionEntity WHERE id = ?;
```

---

## Mappers

```kotlin
// Domain -> Entity
fun User.toEntity(lastSyncedAt: Long): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = avatarUrl,
        bio = bio,
        followersCount = followersCount.toLong(),
        followingCount = followingCount.toLong(),
        partiesHostedCount = partiesHostedCount.toLong(),
        lastSyncedAt = lastSyncedAt,
        createdAt = createdAt.toEpochMilliseconds()
    )
}

// Entity -> Domain
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = avatarUrl,
        bio = bio,
        followersCount = followersCount.toInt(),
        followingCount = followingCount.toInt(),
        partiesHostedCount = partiesHostedCount.toInt(),
        createdAt = Instant.fromEpochMilliseconds(createdAt)
    )
}
```

---

## Sync Strategy

```kotlin
class PartyEventRepositoryImpl(
    private val remoteDataSource: PartyEventRemoteDataSource,
    private val localDataSource: PartyEventLocalDataSource,
    private val networkMonitor: NetworkMonitor
) : PartyEventRepository {

    override suspend fun getRecentParties(): Result<List<PartyEvent>> {
        return if (networkMonitor.isOnline()) {
            // Fetch from remote and cache
            remoteDataSource.getRecentParties()
                .onSuccess { parties ->
                    localDataSource.cacheParties(parties)
                }
        } else {
            // Return cached data
            localDataSource.getRecentParties()
        }
    }

    override suspend fun createParty(party: PartyEvent): Result<PartyEvent> {
        return if (networkMonitor.isOnline()) {
            remoteDataSource.createParty(party)
                .onSuccess { created ->
                    localDataSource.cacheParty(created)
                }
        } else {
            // Queue for later sync
            localDataSource.queueOfflineAction(
                OfflineAction.Create(entityType = "party", payload = party)
            )
            Result.success(party.copy(id = UUID.randomUUID().toString()))
        }
    }
}
```

---

*Database Schema - Party Gallery*
*Version: 1.0*
*Fecha: 2025-12-04*
