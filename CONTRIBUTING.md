# Contributing to Party Gallery

Thank you for your interest in contributing to **Party Gallery**! We appreciate your time and effort in helping improve our Kotlin Multiplatform party documentation app.

⚠️ **IMPORTANT**: This is a **proprietary software project**. By contributing, you agree to transfer ownership of your contributions to the project maintainer and comply with our strict contribution guidelines.

---

## 🔐 Legal Requirements

### **Before Contributing**

**ALL CONTRIBUTORS MUST:**

1. **Read and accept** the [LICENSE.md](LICENSE.md) - This is proprietary software
2. **Sign the Contributor License Agreement** (CLA) - Required before any PR review
3. **Acknowledge ownership transfer** - All contributions become proprietary
4. **Maintain confidentiality** - Do not share code or details publicly

### **Contributor License Agreement (CLA)**

Before your first contribution can be reviewed, you must sign our CLA by:

1. **Email the maintainer** at `[your-email@domain.com]`
2. **Subject**: "Party Gallery CLA - [Your Full Name]"
3. **Include**: Full legal name, email, GitHub username
4. **Wait for CLA document** to be sent to you
5. **Sign and return** the executed CLA

**❌ PRs from contributors without signed CLAs will be immediately closed.**

---

## 🎯 What We Accept

### **Welcome Contributions:**
- 🐛 **Bug fixes** - Critical issues and stability improvements
- 🔧 **Performance optimizations** - Code efficiency improvements
- 📚 **Documentation improvements** - Code comments, README updates
- 🎨 **UI/UX enhancements** - Design improvements within brand guidelines
- 🧪 **Test coverage** - Unit tests, integration tests
- 🌐 **Platform compatibility** - Android, iOS, Web, Desktop fixes
- 🛡️ **Security improvements** - Vulnerability fixes, security hardening

### **Generally NOT Accepted:**
- ❌ **New major features** without prior approval
- ❌ **Architecture changes** without design discussion
- ❌ **Dependencies additions** without justification
- ❌ **Breaking changes** to existing APIs
- ❌ **Cosmetic changes** without clear benefits
- ❌ **Refactoring** for personal preference

---

## 📋 Contribution Process

### **Step 1: Issue Creation (Recommended)**
1. **Search existing issues** to avoid duplicates
2. **Create a detailed issue** describing:
   - Problem or enhancement request
   - Expected vs actual behavior
   - Steps to reproduce (for bugs)
   - Screenshots/videos if applicable
   - Device/platform information

### **Step 2: Fork and Branch**
```bash
# Fork the repository on GitHub
git clone https://github.com/[your-username]/party-gallery.git
cd party-gallery

# Create a feature branch
git checkout -b feature/your-feature-name
# or
git checkout -b bugfix/issue-description
```

### **Step 3: Development Guidelines**
- ✅ **Follow existing code style** and architecture patterns
- ✅ **Write clear commit messages** (see format below)
- ✅ **Add tests** for new functionality
- ✅ **Update documentation** if needed
- ✅ **Test on multiple platforms** when possible
- ✅ **Keep changes focused** - one concern per PR

### **Step 4: Commit Format**
```
type(scope): brief description

Detailed description of what changed and why.
Reference any related issues.

Fixes #123
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`  
**Scopes**: `android`, `ios`, `web`, `desktop`, `shared`, `ui`, `data`, `domain`

**Examples:**
```
feat(android): add camera permission handling for party photos

fix(shared): resolve memory leak in media cache repository

docs(readme): update setup instructions for Firebase config
```

### **Step 5: Pull Request**
1. **Push your branch** to your fork
2. **Create Pull Request** with:
   - Clear, descriptive title
   - Detailed description of changes
   - Screenshots/videos for UI changes
   - Reference to related issues
   - Checklist completion (see template)

---

## 📝 Pull Request Template

When creating a PR, please include:

```markdown
## Description
Brief description of the changes and motivation.

## Type of Change
- [ ] Bug fix (non-breaking change)
- [ ] New feature (non-breaking change)
- [ ] Breaking change (fix/feature causing existing functionality to change)
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Security improvement

## Related Issues
Fixes #[issue_number]
Relates to #[issue_number]

## Testing
- [ ] Added unit tests for new functionality
- [ ] Added integration tests if applicable
- [ ] Tested on Android
- [ ] Tested on iOS
- [ ] Tested on Web
- [ ] Tested on Desktop
- [ ] All existing tests pass

## Screenshots/Videos
<!-- Add screenshots or videos demonstrating the changes -->

## Checklist
- [ ] My code follows the project's style guidelines
- [ ] I have performed a self-review of my own code
- [ ] I have commented my code, particularly hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged and published
- [ ] I have signed and submitted the required CLA

## Additional Notes
<!-- Any additional information, concerns, or context -->
```

---

## 🛠️ Development Setup

### **Prerequisites**
- **JDK 17+**
- **Android Studio** (latest stable)
- **Xcode 15+** (for iOS development, macOS only)
- **Node.js 18+** (for web development)

### **Initial Setup**
```bash
# Clone the repository
git clone https://github.com/[maintainer]/party-gallery.git
cd party-gallery

# Build shared module
./gradlew :shared:build

# Run tests
./gradlew check

# Platform-specific setup
./gradlew :androidApp:assembleDebug    # Android
./gradlew :iosApp:iosSimulatorArm64Test  # iOS (macOS only)
./gradlew :desktopApp:run              # Desktop
./gradlew :webApp:jsBrowserRun         # Web
```

### **Firebase Configuration**
1. **Request access** to Firebase project from maintainer
2. **Download configuration files**:
   - `android/google-services.json`
   - `ios/GoogleService-Info.plist`
3. **Configure web environment** (maintainer will provide keys)

---

## 📐 Code Style Guidelines

### **Kotlin Style**
- Follow **official Kotlin coding conventions**
- Use **ktlint** for formatting: `./gradlew ktlintFormat`
- **4 spaces** indentation, no tabs
- **Line length**: 100 characters maximum
- **Import organization**: Android Studio default

### **Architecture Patterns**
- **MVVM** with ViewModels and Compose UI
- **Repository pattern** for data layer
- **Use Cases** for complex business logic
- **Koin** for dependency injection
- **Coroutines + Flow** for async operations

### **Naming Conventions**
```kotlin
// Classes: PascalCase
class PartyRepository

// Functions/Variables: camelCase
fun createPartyEvent()
val userName: String

// Constants: SCREAMING_SNAKE_CASE
const val MAX_PARTY_DURATION = 8

// Compose functions: PascalCase
@Composable
fun PartyCard()
```

---

## 🧪 Testing Requirements

### **Required Test Coverage**
- **Unit tests** for ViewModels and Use Cases
- **Repository tests** with mocked data sources
- **UI tests** for critical user flows
- **Integration tests** for API interactions

### **Test Organization**
```
src/
  commonTest/     # Shared business logic tests
  androidTest/    # Android-specific tests
  iosTest/        # iOS-specific tests
  jsTest/         # Web-specific tests
  jvmTest/        # Desktop-specific tests
```

### **Running Tests**
```bash
# Run all tests
./gradlew check

# Platform-specific tests
./gradlew :shared:testDebugUnitTest        # Shared tests
./gradlew :androidApp:testDebugUnitTest    # Android tests
./gradlew :iosApp:iosSimulatorArm64Test    # iOS tests
```

---

## 📱 Platform-Specific Guidelines

### **Android**
- **Target SDK**: Latest stable
- **Min SDK**: 24 (Android 7.0)
- **Material Design 3** components
- **CameraX** for camera functionality
- **WorkManager** for background tasks

### **iOS**
- **iOS 14.0+** minimum deployment target
- **SwiftUI integration** through Compose Multiplatform
- **Native camera** integration
- **Background tasks** compliance

### **Web**
- **Modern browsers** support (Chrome 90+, Firefox 88+, Safari 14+)
- **Responsive design** for different screen sizes
- **Progressive Web App** features
- **File API** for media handling

### **Desktop**
- **JVM 17+** requirement
- **Cross-platform** file handling
- **Native look and feel** per OS
- **System integration** (notifications, tray)

---

## 🔄 Review Process

### **Review Timeline**
- **Initial response**: Within 48-72 hours
- **Detailed review**: Within 1 week for small PRs
- **Complex changes**: May require multiple review cycles
- **Approval**: Only by project maintainer

### **Review Criteria**
1. **Legal compliance** - CLA signed, ownership clear
2. **Code quality** - Clean, readable, well-documented
3. **Architecture consistency** - Follows project patterns
4. **Test coverage** - Adequate tests included
5. **Platform compatibility** - Works across target platforms
6. **Performance impact** - No significant regressions
7. **Security considerations** - No vulnerabilities introduced

### **Review Feedback**
- **Requested changes** must be addressed before approval
- **Discussions** should be resolved constructively
- **Multiple iterations** may be required
- **Final decision** rests with maintainer

---

## 📞 Communication

### **Getting Help**
- **Create an issue** for questions about contribution
- **Email maintainer** for legal/licensing questions
- **GitHub discussions** for general development topics

### **Response Times**
- **Issues**: 48-72 hours
- **PRs**: 1 week for initial review
- **Email**: 24-48 hours for urgent matters
- **CLA questions**: Same day response

### **Professional Communication**
- Be **respectful and constructive**
- **Stay on topic** in discussions
- **Follow up** reasonably on pending items
- **Accept decisions** gracefully

---

## ❌ Unacceptable Behavior

The following behaviors will result in **immediate rejection** of contributions:

- **Sharing proprietary code** outside approved channels
- **Bypassing CLA** requirements
- **Harassing** maintainers or other contributors
- **Submitting malicious code** or vulnerabilities
- **Violating third-party licenses** or terms
- **Ignoring feedback** repeatedly
- **Creating spam** issues or PRs

---

## 🎉 Recognition

### **Contributor Recognition**
- **Accepted contributors** will be acknowledged in releases
- **Significant contributions** may be highlighted in documentation
- **Long-term contributors** may be offered collaboration opportunities

### **But Remember**
- **Code ownership** transfers to the project
- **Commercial rights** remain with the maintainer
- **Proprietary nature** means limited public recognition

---

## ℹ️ Contact Information

### **Primary Contact**
- **Email**: `[your-email@domain.com]`
- **GitHub**: `@[your-username]`
- **Response Time**: 48-72 hours

### **Legal/CLA Questions**
- **Email**: `[legal-email@domain.com]`
- **Subject**: "Party Gallery Legal - [Topic]"

### **Technical Questions**
- **Create GitHub Issue** with `question` label
- **Email**: `[your-email@domain.com]`
- **Subject**: "Party Gallery Technical - [Topic]"

---

**Thank you for contributing to Party Gallery! 🎉**

*Remember: This is proprietary software. All contributions become property of the project maintainer upon acceptance.*
