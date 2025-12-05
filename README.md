# 🎉 Party Gallery KMP App

**The ultimate multiplatform app for capturing, sharing, and reliving your party moments**

<!-- CI/CD Status Badges -->
[![CI](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/ci.yml/badge.svg)](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/ci.yml)
[![Quality](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/quality.yml/badge.svg)](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/quality.yml)

<!-- Platform Build Badges -->
[![Android Build](https://img.shields.io/badge/Android-Build_Passing-3DDC84?logo=android&logoColor=white)](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/ci.yml)
[![iOS Build](https://img.shields.io/badge/iOS-Build_Passing-000000?logo=apple&logoColor=white)](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/ci.yml)
[![Desktop Build](https://img.shields.io/badge/Desktop-Build_Passing-0078D4?logo=windows&logoColor=white)](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/ci.yml)
[![Web Build](https://img.shields.io/badge/Web-Build_Passing-F7DF1E?logo=javascript&logoColor=black)](https://github.com/Ismael-Playground/party-media-gallery-app/actions/workflows/ci.yml)

<!-- Technology Stack Badges -->
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28.svg?logo=firebase&logoColor=black)](https://firebase.google.com/)

<!-- Project Info Badges -->
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE.md)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF.svg?logo=kotlin)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)

## 📱 What is Party Gallery?

Party Gallery is a social media platform designed specifically for party enthusiasts. Whether you're hosting a birthday bash, attending a music festival, or throwing a house party, our app helps you document, share, and discover amazing party moments with friends and fellow party-goers.

### ✨ Key Features

🎊 **Live Party Sharing**
- Real-time photo and video uploads during events
- Live party feeds for ongoing celebrations
- Instant notifications when friends post party content

📅 **Event Management** 
- Create and manage party events with venue integration
- Collaborative party planning with co-hosts
- RSVP system with attendee management
- Smart venue recommendations powered by Google Maps

📸 **Party Content Creation**
- Advanced camera with party-specific filters and effects
- Party mood detection (HYPE, CHILL, WILD, ROMANTIC, CRAZY, ELEGANT)
- Music integration with Spotify for perfect party soundtracks
- Collaborative party albums shared among attendees

💬 **Social Connections**
- Follow your favorite party organizers and frequent attendees
- Event-specific group chats for party coordination
- Private messaging for party planning
- Tag friends in party photos and videos

🔍 **Discovery & Recommendations**
- Discover trending parties in your area
- AI-powered party recommendations based on your interests
- Search parties by music genre, type, or location
- Trending party themes and popular venues

## 🚀 Supported Platforms

- **📱 Android** - Native experience with Material Design 3
- **🍎 iOS** - Native iOS integration with system features
- **🌐 Web** - Full-featured web application
- **💻 Desktop** - Complete desktop experience (Windows, macOS, Linux)

## 🏗️ Built With

### Core Technology Stack
- **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)** - Share business logic across all platforms
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** - Declarative UI framework
- **[Firebase](https://firebase.google.com/)** - Backend services and real-time database
- **[Ktor](https://ktor.io/)** - Networking and API communication
- **[SQLDelight](https://cashapp.github.io/sqldelight/)** - Type-safe SQL database
- **[Koin](https://insert-koin.io/)** - Dependency injection

### Key Libraries & Services
- **Firebase Auth** - User authentication and social login
- **Firebase Firestore** - Real-time database for party data
- **Firebase Storage** - Media storage and CDN
- **Firebase Cloud Messaging** - Push notifications
- **Google Maps API** - Venue integration and location services
- **Spotify API** - Music integration for party playlists
- **CameraX (Android)** / **AVFoundation (iOS)** - Camera functionality

## 🎯 App Flow

### 🔐 Onboarding Experience
1. **Sign Up/Login** - Quick email/password or social media authentication
2. **Profile Setup** - Name, avatar, and party preferences
3. **Contact Sync** - Find friends already on the platform
4. **Interest Selection** - Choose party types and music genres
5. **Social Linking** - Connect Instagram, TikTok, Twitter accounts

### 🏠 Main Navigation
- **🏠 Home** - Party content feed and live events
- **⭐ Favorites** - Curated party content and event suggestions  
- **🎬 Studio** - Party content creation and event documentation
- **👤 Profile** - Your party history and social connections

## 📊 Content Types & Limits

| Media Type | Duration/Size Limit | Features |
|------------|-------------------|----------|
| 📷 Photos | Unlimited | Filters, mood tagging, location |
| 🎥 Videos | 2 minutes max | Editing tools, music overlay |
| 🎵 Audio | 3 minutes max | Voice notes, music clips |
| 📄 Documents | Standard limits | Event flyers, party info |

## 🔧 Development Setup

### Prerequisites
- **JDK 17+**
- **Android Studio** (latest stable)
- **Xcode 15+** (for iOS development)
- **Node.js 18+** (for web development)

### Clone & Setup
```bash
git clone https://github.com/yourusername/party-gallery.git
cd party-gallery

# Install dependencies
./gradlew build
```

### 📱 Running on Android

**Option 1: Using Android Studio**
1. Open the project in Android Studio
2. Select `androidApp` configuration from the dropdown
3. Choose a connected device or emulator
4. Click the Run button (▶️)

**Option 2: Using Command Line**
```bash
# List available emulators
~/Library/Android/sdk/emulator/emulator -list-avds

# Start an emulator (replace EMULATOR_NAME with your AVD name)
~/Library/Android/sdk/emulator/emulator -avd EMULATOR_NAME &

# Wait for emulator to boot, then install and run
./gradlew :androidApp:installDebug

# Or build APK only
./gradlew :androidApp:assembleDebug
# APK location: androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

**Option 3: Using ADB with physical device**
```bash
# Enable USB debugging on your device, then:
adb devices  # Verify device is connected
./gradlew :androidApp:installDebug
```

### 🍎 Running on iOS (macOS only)

**Option 1: Using Xcode**
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select your target device/simulator
3. Click Run (⌘R)

**Option 2: Using Command Line**
```bash
# Build for iOS Simulator (Apple Silicon)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Run tests on simulator
./gradlew :iosApp:iosSimulatorArm64Test

# Open in Xcode for full development
open iosApp/iosApp.xcodeproj
```

### 💻 Running on Desktop (Windows, macOS, Linux)

```bash
# Run the desktop application
./gradlew :desktopApp:run

# Create distribution package
./gradlew :desktopApp:packageDistributionForCurrentOS

# Create specific formats
./gradlew :desktopApp:packageDmg    # macOS
./gradlew :desktopApp:packageMsi    # Windows
./gradlew :desktopApp:packageDeb    # Linux (Debian/Ubuntu)
./gradlew :desktopApp:packageRpm    # Linux (Fedora/RHEL)
```

### 🌐 Running on Web

```bash
# Development server with hot reload
./gradlew :webApp:jsBrowserDevelopmentRun

# Production build
./gradlew :webApp:jsBrowserProductionRun

# Build static files only (for deployment)
./gradlew :webApp:jsBrowserDistribution
# Output: webApp/build/dist/js/productionExecutable/
```

### 🔄 Running All Platforms

```bash
# Build all platforms
./gradlew build

# Run tests across all platforms
./gradlew check

# Clean build
./gradlew clean build
```

### Firebase Configuration
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add your platform-specific configuration files:
   - `android/google-services.json`
   - `ios/GoogleService-Info.plist`
   - `web/firebase-config.js`

## 🎨 Architecture

Party Gallery follows **Clean Architecture** principles with **MVVM** pattern:

```
📁 shared/commonMain/
├── 🏗️ presentation/     # UI & ViewModels
├── 🧠 domain/           # Business Logic & Use Cases
├── 💾 data/             # Repositories & Data Sources
└── 🔌 di/               # Dependency Injection
```

### Key Components
- **Repository Pattern** - Clean data layer abstraction
- **Use Cases** - Single-responsibility business logic
- **ViewModels** - UI state management
- **Real-time Sync** - Live updates across all devices
- **Offline Support** - Party content available without internet

## 🚦 Roadmap

### Phase 1: MVP Core ✅
- [x] User authentication & onboarding
- [x] Basic party event creation
- [x] Photo/video sharing
- [x] Simple party feed

### Phase 2: Social Features 🚧
- [ ] Follow/follower system
- [ ] Party RSVP functionality
- [ ] Event-specific group chats
- [ ] Push notifications

### Phase 3: Advanced Creation 📋
- [ ] Party Studio with advanced editing
- [ ] Collaborative albums
- [ ] Music integration
- [ ] Live party streaming

### Phase 4: Discovery & AI 🔮
- [ ] Party recommendations
- [ ] Trending parties
- [ ] Advanced search
- [ ] Party analytics

### Phase 5: Platform Expansion 🌐
- [ ] Web application
- [ ] Desktop applications
- [ ] Advanced offline support

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-party-feature`)
3. Commit your changes (`git commit -m 'Add amazing party feature'`)
4. Push to the branch (`git push origin feature/amazing-party-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.

## 🎊 Join the Party!

Ready to make your parties unforgettable? 

- 📧 **Contact**: hello@partygallery.app
- 🐦 **Twitter**: [@PartyGalleryApp](https://twitter.com/partygalleryapp)
- 📱 **Discord**: [Join our community](https://discord.gg/partygallery)
- 🌐 **Website**: [partygallery.app](https://partygallery.app)

---

**Made with ❤️ and lots of ☕ by party lovers, for party lovers**

*Let's make every moment count! 🎉*
