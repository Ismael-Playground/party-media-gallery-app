# API Reference - Party Gallery

Documentacion de la API de Party Gallery (Firebase-based).

## Arquitectura

Party Gallery usa **Firebase** como backend:
- **Firebase Auth**: Autenticacion
- **Firebase Firestore**: Base de datos en tiempo real
- **Firebase Storage**: Almacenamiento de media
- **Firebase Cloud Messaging**: Notificaciones push

Esta documentacion describe la estructura de datos y las operaciones disponibles.

---

## Colecciones de Firestore

### users

Perfil de usuario.

```typescript
interface User {
  id: string;                    // Auto-generated
  username: string;              // Unique
  email: string;                 // From Firebase Auth
  firstName: string;
  lastName: string;
  birthDate: Timestamp;
  avatarUrl: string | null;
  bio: string | null;
  socialLinks: SocialLink[];
  tags: string[];                // Intereses
  followersCount: number;
  followingCount: number;
  partiesHostedCount: number;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

interface SocialLink {
  platform: 'instagram' | 'tiktok' | 'twitter' | 'facebook' | 'pinterest';
  username: string;
  url: string;
}
```

**Indices:**
- `username` (unique)
- `email` (unique)
- `tags` (array-contains)

---

### party_events

Eventos de fiesta.

```typescript
interface PartyEvent {
  id: string;
  hostId: string;                // Ref: users
  coHosts: string[];             // Refs: users
  title: string;
  description: string;
  venue: Venue;
  startDateTime: Timestamp;
  endDateTime: Timestamp;
  coverImage: string | null;
  tags: string[];
  musicGenres: string[];
  dressCode: string | null;
  privacyType: 'public' | 'private' | 'invite_only';
  maxAttendees: number | null;
  status: 'planned' | 'live' | 'ended' | 'cancelled';
  attendeesCount: number;
  mediaCount: number;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

interface Venue {
  name: string;
  address: string;
  city: string;
  country: string;
  latitude: number;
  longitude: number;
  placeId: string | null;        // Google Places ID
}
```

**Indices:**
- `hostId`
- `status`
- `startDateTime` (DESC)
- `tags` (array-contains)
- Composite: `status + startDateTime`

---

### party_attendees

Subcollection: `party_events/{eventId}/attendees`

```typescript
interface PartyAttendee {
  id: string;                    // Same as userId
  userId: string;                // Ref: users
  status: 'going' | 'maybe' | 'not_going';
  respondedAt: Timestamp;
  checkedInAt: Timestamp | null;
}
```

---

### media_content

Contenido multimedia compartido.

```typescript
interface MediaContent {
  id: string;
  uploaderId: string;            // Ref: users
  partyEventId: string;          // Ref: party_events
  type: 'photo' | 'video' | 'audio';
  url: string;                   // Firebase Storage URL
  thumbnailUrl: string | null;
  partyMood: 'hype' | 'chill' | 'wild' | 'romantic' | 'crazy' | 'elegant';
  caption: string | null;
  taggedUsers: string[];         // Refs: users
  location: GeoPoint | null;
  duration: number | null;       // For video/audio in seconds
  width: number | null;
  height: number | null;
  likesCount: number;
  commentsCount: number;
  sharesCount: number;
  createdAt: Timestamp;
}
```

**Indices:**
- `partyEventId`
- `uploaderId`
- `partyMood`
- `createdAt` (DESC)

---

### chat_rooms

Salas de chat.

```typescript
interface ChatRoom {
  id: string;
  partyEventId: string | null;   // Null for private chats
  type: 'event' | 'private';
  participants: string[];        // Refs: users
  lastMessage: Message | null;
  lastMessageAt: Timestamp | null;
  createdAt: Timestamp;
}
```

---

### messages

Subcollection: `chat_rooms/{roomId}/messages`

```typescript
interface Message {
  id: string;
  senderId: string;              // Ref: users
  content: string;
  type: 'text' | 'image' | 'video' | 'party_moment';
  mediaUrl: string | null;
  replyToId: string | null;
  reactions: Record<string, string[]>;  // emoji -> userIds
  readBy: string[];              // UserIds who read
  createdAt: Timestamp;
}
```

---

### user_follows

Relaciones de follow.

```typescript
interface UserFollow {
  id: string;                    // `${followerId}_${followingId}`
  followerId: string;            // Ref: users
  followingId: string;           // Ref: users
  createdAt: Timestamp;
}
```

**Indices:**
- `followerId`
- `followingId`

---

### notifications

Subcollection: `users/{userId}/notifications`

```typescript
interface Notification {
  id: string;
  type: 'follow' | 'rsvp' | 'chat' | 'tag' | 'event_reminder' | 'event_live';
  title: string;
  body: string;
  data: Record<string, any>;
  read: boolean;
  createdAt: Timestamp;
}
```

---

## Firebase Storage Structure

```
/users/{userId}/
  avatar.jpg

/parties/{partyId}/
  cover.jpg
  media/
    {mediaId}.jpg
    {mediaId}.mp4
    {mediaId}_thumb.jpg

/chat/{roomId}/
  {messageId}.jpg
```

---

## Security Rules (Firestore)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }

    // Party Events
    match /party_events/{eventId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth.uid == resource.data.hostId
                            || request.auth.uid in resource.data.coHosts;

      // Attendees subcollection
      match /attendees/{attendeeId} {
        allow read: if request.auth != null;
        allow write: if request.auth.uid == attendeeId;
      }
    }

    // Media Content
    match /media_content/{mediaId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth.uid == resource.data.uploaderId;
    }

    // Chat Rooms
    match /chat_rooms/{roomId} {
      allow read: if request.auth.uid in resource.data.participants;

      match /messages/{messageId} {
        allow read: if request.auth.uid in get(/databases/$(database)/documents/chat_rooms/$(roomId)).data.participants;
        allow create: if request.auth.uid in get(/databases/$(database)/documents/chat_rooms/$(roomId)).data.participants;
      }
    }

    // User Follows
    match /user_follows/{followId} {
      allow read: if request.auth != null;
      allow create: if request.auth.uid == request.resource.data.followerId;
      allow delete: if request.auth.uid == resource.data.followerId;
    }
  }
}
```

---

## Cloud Functions

### Triggers

| Trigger | Funcion |
|---------|---------|
| `users.onCreate` | Inicializar contadores, enviar email bienvenida |
| `party_events.onUpdate` | Notificar cambios a asistentes |
| `party_events.onUpdate(status=live)` | Notificar inicio de evento |
| `user_follows.onCreate` | Incrementar contadores, notificar |
| `user_follows.onDelete` | Decrementar contadores |
| `messages.onCreate` | Enviar push notification |
| `media_content.onCreate` | Generar thumbnail, notificar tags |

### Callable Functions

```typescript
// Buscar usuarios
exports.searchUsers = functions.https.onCall(async (data, context) => {
  const { query, limit = 20 } = data;
  // Implementar busqueda
});

// Obtener recomendaciones
exports.getRecommendations = functions.https.onCall(async (data, context) => {
  const { userId, limit = 10 } = data;
  // Implementar algoritmo de recomendacion
});

// Reportar contenido
exports.reportContent = functions.https.onCall(async (data, context) => {
  const { contentId, contentType, reason } = data;
  // Guardar reporte para moderacion
});
```

---

## Limites

| Recurso | Limite |
|---------|--------|
| Video duration | 2 minutos |
| Audio duration | 3 minutos |
| Image size | 10 MB |
| Video size | 100 MB |
| Party title | 100 caracteres |
| Party description | 2000 caracteres |
| Bio | 500 caracteres |
| Tags per party | 10 |
| Co-hosts per party | 5 |

---

*API Reference - Party Gallery*
*Version: 1.0*
*Fecha: 2025-12-04*
