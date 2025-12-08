package com.partygallery.web

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

/**
 * Party Gallery Web App
 *
 * Dark Mode First design with Compose HTML
 * Colors: bg #0A0A0A, primary #F59E0B (amber)
 */

// Design System Colors
object PartyColors {
    val background = Color("#0A0A0A")
    val surface = Color("#141414")
    val surfaceVariant = Color("#1E1E1E")
    val primary = Color("#F59E0B")
    val primaryHover = Color("#D97706")
    val onBackground = Color("#FFFFFF")
    val onBackgroundVariant = Color("#A1A1AA")
    val onPrimary = Color("#0A0A0A")
    val error = Color("#EF4444")
    val success = Color("#22C55E")
    val divider = Color("#3F3F46")
}

enum class Screen {
    LOGIN,
    HOME,
}

fun main() {
    renderComposable(rootElementId = "root") {
        Style(AppStyleSheet)
        App()
    }
}

object AppStyleSheet : StyleSheet() {
    init {
        "body" style {
            backgroundColor(PartyColors.background)
            color(PartyColors.onBackground)
            fontFamily("Inter", "system-ui", "sans-serif")
            margin(0.px)
            padding(0.px)
        }
        "input" style {
            backgroundColor(PartyColors.surfaceVariant)
            color(PartyColors.onBackground)
            border(1.px, LineStyle.Solid, PartyColors.divider)
            borderRadius(12.px)
            padding(16.px)
            fontSize(16.px)
            width(100.percent)
            property("box-sizing", "border-box")
            outline("none")
        }
        "input:focus" style {
            border(1.px, LineStyle.Solid, PartyColors.primary)
        }
        "input::placeholder" style {
            color(PartyColors.onBackgroundVariant)
        }
    }
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var loggedInEmail by remember { mutableStateOf("") }

    Div({
        style {
            minHeight(100.vh)
            backgroundColor(PartyColors.background)
        }
    }) {
        when (currentScreen) {
            Screen.LOGIN -> LoginScreen(
                onLoginSuccess = { email ->
                    loggedInEmail = email
                    currentScreen = Screen.HOME
                },
            )
            Screen.HOME -> HomeScreen(
                userEmail = loggedInEmail,
                onLogout = {
                    currentScreen = Screen.LOGIN
                    loggedInEmail = ""
                },
            )
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun validateEmail(e: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regex.matches(e)
    }

    fun signIn() {
        emailError = null
        passwordError = null
        errorMessage = null
        successMessage = null

        if (!validateEmail(email)) {
            emailError = "Please enter a valid email"
            return
        }
        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            return
        }

        isLoading = true

        scope.launch {
            delay(1500)
            isLoading = false
            successMessage = "Welcome! Login successful"
            delay(800)
            onLoginSuccess(email)
        }
    }

    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            minHeight(100.vh)
            padding(24.px)
            property("box-sizing", "border-box")
        }
    }) {
        // Container
        Div({
            style {
                width(100.percent)
                maxWidth(400.px)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                gap(24.px)
            }
        }) {
            // Logo
            Div({
                style {
                    textAlign("center")
                    marginBottom(16.px)
                }
            }) {
                H1({
                    style {
                        color(PartyColors.primary)
                        fontSize(48.px)
                        fontWeight(700)
                        margin(0.px)
                        letterSpacing((-1).px)
                    }
                }) { Text("Party") }
                H2({
                    style {
                        color(PartyColors.onBackground)
                        fontSize(36.px)
                        fontWeight(600)
                        margin(0.px)
                    }
                }) { Text("Gallery") }
                P({
                    style {
                        color(PartyColors.onBackgroundVariant)
                        fontSize(16.px)
                        marginTop(8.px)
                    }
                }) { Text("Capture and share party moments") }
            }

            // Success message
            successMessage?.let { msg ->
                Div({
                    style {
                        backgroundColor(Color("#22C55E33"))
                        color(PartyColors.success)
                        padding(16.px)
                        borderRadius(8.px)
                        fontSize(14.px)
                    }
                }) { Text(msg) }
            }

            // Error message
            errorMessage?.let { msg ->
                Div({
                    style {
                        backgroundColor(Color("#EF444433"))
                        color(PartyColors.error)
                        padding(16.px)
                        borderRadius(8.px)
                        fontSize(14.px)
                    }
                }) { Text(msg) }
            }

            // Email field
            Div({
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    gap(8.px)
                }
            }) {
                Span({
                    style {
                        color(PartyColors.onBackgroundVariant)
                        fontSize(14.px)
                        fontWeight(500)
                    }
                }) { Text("Email") }
                Input(InputType.Email) {
                    value(email)
                    placeholder("Enter your email")
                    onInput { email = it.value; emailError = null }
                }
                emailError?.let { err ->
                    Span({
                        style {
                            color(PartyColors.error)
                            fontSize(12.px)
                        }
                    }) { Text(err) }
                }
            }

            // Password field
            Div({
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    gap(8.px)
                }
            }) {
                Span({
                    style {
                        color(PartyColors.onBackgroundVariant)
                        fontSize(14.px)
                        fontWeight(500)
                    }
                }) { Text("Password") }
                Input(InputType.Password) {
                    value(password)
                    placeholder("Enter your password")
                    onInput { password = it.value; passwordError = null }
                }
                passwordError?.let { err ->
                    Span({
                        style {
                            color(PartyColors.error)
                            fontSize(12.px)
                        }
                    }) { Text(err) }
                }
            }

            // Sign In button
            Button({
                onClick { signIn() }
                if (isLoading) disabled()
                style {
                    backgroundColor(if (isLoading) PartyColors.primaryHover else PartyColors.primary)
                    color(PartyColors.onPrimary)
                    border(0.px)
                    borderRadius(12.px)
                    padding(16.px, 24.px)
                    fontSize(16.px)
                    fontWeight(600)
                    cursor("pointer")
                    width(100.percent)
                    property("transition", "background-color 0.2s")
                }
            }) {
                Text(if (isLoading) "Signing in..." else "Sign In")
            }

            // Divider
            Div({
                style {
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                    gap(16.px)
                }
            }) {
                Div({
                    style {
                        flex(1)
                        height(1.px)
                        backgroundColor(PartyColors.divider)
                    }
                })
                Span({
                    style {
                        color(PartyColors.onBackgroundVariant)
                        fontSize(14.px)
                    }
                }) { Text("or continue with") }
                Div({
                    style {
                        flex(1)
                        height(1.px)
                        backgroundColor(PartyColors.divider)
                    }
                })
            }

            // Social buttons
            Div({
                style {
                    display(DisplayStyle.Flex)
                    gap(16.px)
                }
            }) {
                Button({
                    onClick { errorMessage = "Google Sign-In coming soon" }
                    style {
                        flex(1)
                        backgroundColor(PartyColors.surfaceVariant)
                        color(PartyColors.onBackground)
                        border(1.px, LineStyle.Solid, PartyColors.divider)
                        borderRadius(12.px)
                        padding(12.px, 24.px)
                        fontSize(14.px)
                        fontWeight(500)
                        cursor("pointer")
                    }
                }) { Text("Google") }
                Button({
                    onClick { errorMessage = "Apple Sign-In coming soon" }
                    style {
                        flex(1)
                        backgroundColor(PartyColors.surfaceVariant)
                        color(PartyColors.onBackground)
                        border(1.px, LineStyle.Solid, PartyColors.divider)
                        borderRadius(12.px)
                        padding(12.px, 24.px)
                        fontSize(14.px)
                        fontWeight(500)
                        cursor("pointer")
                    }
                }) { Text("Apple") }
            }

            // Sign up link
            Div({
                style {
                    textAlign("center")
                    marginTop(16.px)
                }
            }) {
                Span({
                    style {
                        color(PartyColors.onBackgroundVariant)
                        fontSize(14.px)
                    }
                }) { Text("Don't have an account? ") }
                A(href = "#", {
                    onClick { it.preventDefault(); errorMessage = "Sign Up coming soon" }
                    style {
                        color(PartyColors.primary)
                        fontSize(14.px)
                        fontWeight(600)
                        property("text-decoration", "none")
                    }
                }) { Text("Sign Up") }
            }
        }
    }
}

// Mood colors for party tags
object MoodColors {
    val HYPE = Color("#EF4444")     // Red
    val CHILL = Color("#3B82F6")    // Blue
    val WILD = Color("#8B5CF6")     // Purple
    val ROMANTIC = Color("#EC4899") // Pink
    val CRAZY = Color("#F97316")    // Orange
    val ELEGANT = Color("#F59E0B")  // Amber
}

// Mock party data
data class PartyItem(
    val id: String,
    val title: String,
    val hostName: String,
    val venueName: String,
    val attendeesCount: Int,
    val isLive: Boolean,
    val mood: String,
)

val mockParties = listOf(
    PartyItem("1", "Neon Night Bash", "DJ Alex", "Club Vertex", 245, true, "HYPE"),
    PartyItem("2", "Rooftop Vibes", "Maria G.", "Sky Lounge", 89, false, "CHILL"),
    PartyItem("3", "Underground Rave", "Techno Crew", "The Bunker", 320, true, "WILD"),
    PartyItem("4", "Valentine's Night", "Rose Events", "Garden Hall", 156, false, "ROMANTIC"),
    PartyItem("5", "Carnival Madness", "Party Kings", "Main Plaza", 412, true, "CRAZY"),
    PartyItem("6", "Black Tie Gala", "Elite Club", "Grand Hotel", 78, false, "ELEGANT"),
)

@Composable
fun HomeScreen(userEmail: String, onLogout: () -> Unit) {
    val displayName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }

    Div({
        style {
            minHeight(100.vh)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
    }) {
        // Header
        Div({
            style {
                backgroundColor(PartyColors.surface)
                padding(16.px, 24.px)
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                property("border-bottom", "1px solid #3F3F46")
            }
        }) {
            Div {
                H1({
                    style {
                        fontSize(24.px)
                        fontWeight(700)
                        margin(0.px)
                    }
                }) {
                    Span({ style { color(PartyColors.primary) } }) { Text("Party") }
                    Text(" Gallery")
                }
                P({
                    style {
                        color(PartyColors.onBackgroundVariant)
                        fontSize(14.px)
                        margin(4.px, 0.px, 0.px, 0.px)
                    }
                }) { Text("Discover the best parties") }
            }
            Button({
                onClick { onLogout() }
                style {
                    backgroundColor(Color.transparent)
                    color(PartyColors.onBackgroundVariant)
                    border(1.px, LineStyle.Solid, PartyColors.divider)
                    borderRadius(8.px)
                    padding(8.px, 16.px)
                    fontSize(14.px)
                    cursor("pointer")
                }
            }) { Text("Logout") }
        }

        // Filter tabs
        Div({
            style {
                display(DisplayStyle.Flex)
                gap(8.px)
                padding(16.px, 24.px)
                property("overflow-x", "auto")
            }
        }) {
            listOf("All", "Live", "HYPE", "CHILL", "WILD", "ROMANTIC", "CRAZY", "ELEGANT").forEach { filter ->
                Div({
                    onClick { selectedFilter = filter }
                    style {
                        backgroundColor(if (selectedFilter == filter) PartyColors.primary else PartyColors.surfaceVariant)
                        color(if (selectedFilter == filter) PartyColors.onPrimary else PartyColors.onBackground)
                        padding(8.px, 16.px)
                        borderRadius(20.px)
                        fontSize(14.px)
                        fontWeight(if (selectedFilter == filter) 600 else 400)
                        cursor("pointer")
                        property("white-space", "nowrap")
                        property("transition", "all 0.2s")
                    }
                }) { Text(filter) }
            }
        }

        // Main content - Party Feed
        Div({
            style {
                flex(1)
                padding(0.px, 24.px, 24.px, 24.px)
                property("overflow-y", "auto")
            }
        }) {
            // Filter parties based on selection
            val filteredParties = mockParties.filter { party ->
                when (selectedFilter) {
                    "All" -> true
                    "Live" -> party.isLive
                    else -> party.mood == selectedFilter
                }
            }

            if (filteredParties.isEmpty()) {
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                        alignItems(AlignItems.Center)
                        justifyContent(JustifyContent.Center)
                        padding(48.px)
                    }
                }) {
                    H3({
                        style {
                            color(PartyColors.onBackground)
                            margin(0.px)
                        }
                    }) { Text("No parties found") }
                    P({
                        style {
                            color(PartyColors.onBackgroundVariant)
                            marginTop(8.px)
                        }
                    }) { Text("Try a different filter!") }
                }
            } else {
                // Party grid
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                        gap(16.px)
                    }
                }) {
                    filteredParties.forEach { party ->
                        PartyCard(party)
                    }
                }
            }
        }

        // Bottom navigation
        Div({
            style {
                backgroundColor(PartyColors.surface)
                padding(12.px, 16.px)
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceAround)
                property("border-top", "1px solid #3F3F46")
            }
        }) {
            listOf(
                "Home" to "home",
                "Favorites" to "heart",
                "Studio" to "camera",
                "Profile" to "user",
            ).forEachIndexed { index, (label, _) ->
                Div({
                    onClick { selectedTab = index }
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                        alignItems(AlignItems.Center)
                        gap(4.px)
                        color(if (selectedTab == index) PartyColors.primary else PartyColors.onBackgroundVariant)
                        cursor("pointer")
                        padding(8.px, 16.px)
                    }
                }) {
                    // Icon placeholder (emoji for now)
                    Span({
                        style {
                            fontSize(20.px)
                        }
                    }) {
                        Text(
                            when (index) {
                                0 -> "🏠"
                                1 -> "❤️"
                                2 -> "📷"
                                else -> "👤"
                            },
                        )
                    }
                    Span({
                        style {
                            fontSize(12.px)
                            fontWeight(if (selectedTab == index) 600 else 400)
                        }
                    }) { Text(label) }
                }
            }
        }
    }
}

@Composable
fun PartyCard(party: PartyItem) {
    val moodColor = when (party.mood) {
        "HYPE" -> MoodColors.HYPE
        "CHILL" -> MoodColors.CHILL
        "WILD" -> MoodColors.WILD
        "ROMANTIC" -> MoodColors.ROMANTIC
        "CRAZY" -> MoodColors.CRAZY
        "ELEGANT" -> MoodColors.ELEGANT
        else -> PartyColors.primary
    }

    Div({
        style {
            backgroundColor(PartyColors.surface)
            borderRadius(16.px)
            property("overflow", "hidden")
            cursor("pointer")
            property("transition", "transform 0.2s")
        }
    }) {
        // Image placeholder with gradient
        Div({
            style {
                height(180.px)
                property(
                    "background",
                    "linear-gradient(135deg, ${PartyColors.surfaceVariant} 0%, ${PartyColors.surface} 100%)",
                )
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                position(Position.Relative)
            }
        }) {
            // Party emoji
            Span({
                style {
                    fontSize(48.px)
                    property("opacity", "0.5")
                }
            }) { Text("🎉") }

            // Live badge
            if (party.isLive) {
                Div({
                    style {
                        position(Position.Absolute)
                        property("top", "12px")
                        property("left", "12px")
                        backgroundColor(PartyColors.error)
                        color(Color.white)
                        padding(4.px, 12.px)
                        borderRadius(20.px)
                        fontSize(12.px)
                        fontWeight(700)
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        gap(6.px)
                    }
                }) {
                    // Pulsing dot
                    Div({
                        style {
                            width(8.px)
                            height(8.px)
                            backgroundColor(Color.white)
                            borderRadius(50.percent)
                        }
                    })
                    Text("LIVE")
                }
            }
        }

        // Content
        Div({
            style {
                padding(16.px)
            }
        }) {
            // Title
            H3({
                style {
                    fontSize(18.px)
                    fontWeight(700)
                    color(PartyColors.onBackground)
                    margin(0.px)
                }
            }) { Text(party.title) }

            // Host info
            Div({
                style {
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                    gap(8.px)
                    marginTop(8.px)
                }
            }) {
                // Avatar placeholder
                Div({
                    style {
                        width(24.px)
                        height(24.px)
                        backgroundColor(PartyColors.primary)
                        borderRadius(50.percent)
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        justifyContent(JustifyContent.Center)
                        fontSize(12.px)
                        fontWeight(600)
                        color(PartyColors.onPrimary)
                    }
                }) {
                    Text(party.hostName.first().toString())
                }
                Span({
                    style {
                        fontSize(14.px)
                        color(PartyColors.onBackground)
                    }
                }) { Text(party.hostName) }
            }

            // Venue and attendees
            Div({
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                    marginTop(12.px)
                }
            }) {
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        gap(4.px)
                        color(PartyColors.onBackgroundVariant)
                        fontSize(13.px)
                    }
                }) {
                    Text("📍 ${party.venueName}")
                }
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        gap(4.px)
                        color(PartyColors.onBackgroundVariant)
                        fontSize(13.px)
                    }
                }) {
                    Text("👥 ${party.attendeesCount} going")
                }
            }

            // Mood tag
            Div({
                style {
                    marginTop(12.px)
                }
            }) {
                Span({
                    style {
                        backgroundColor(moodColor)
                        color(Color.white)
                        padding(4.px, 12.px)
                        borderRadius(12.px)
                        fontSize(12.px)
                        fontWeight(600)
                    }
                }) { Text(party.mood) }
            }
        }
    }
}
