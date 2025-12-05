package com.partygallery.web

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

// Design System Colors (Dark Mode First)
object PartyColors {
    val background = Color("#0A0A0A")
    val surface = Color("#141414")
    val surfaceVariant = Color("#1E1E1E")
    val primary = Color("#F59E0B")
    val primaryHover = Color("#D97706")
    val secondary = Color("#FBBF24")
    val onSurface = Color("#FFFFFF")
    val onSurfaceVariant = Color("#A1A1AA")
    val outline = Color("#3F3F46")
    val error = Color("#EF4444")
    val success = Color("#22C55E")
}

fun main() {
    renderComposable(rootElementId = "root") {
        Style(PartyGalleryStyleSheet)
        PartyGalleryApp()
    }
}

object PartyGalleryStyleSheet : StyleSheet() {
    init {
        "body" style {
            margin(0.px)
            padding(0.px)
            backgroundColor(PartyColors.background)
            fontFamily("Inter", "system-ui", "sans-serif")
            color(PartyColors.onSurface)
            minHeight(100.vh)
        }

        "input" style {
            fontFamily("Inter", "system-ui", "sans-serif")
        }

        "button" style {
            fontFamily("Inter", "system-ui", "sans-serif")
            cursor("pointer")
        }

        "*" style {
            property("box-sizing", "border-box")
        }
    }
}

enum class Screen {
    LOGIN,
    HOME
}

@Composable
fun PartyGalleryApp() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var userEmail by remember { mutableStateOf("") }

    when (currentScreen) {
        Screen.LOGIN -> LoginScreen(
            onLoginSuccess = { email ->
                userEmail = email
                currentScreen = Screen.HOME
            }
        )
        Screen.HOME -> HomeScreen(
            userEmail = userEmail,
            onLogout = {
                currentScreen = Screen.LOGIN
                userEmail = ""
            }
        )
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun validateEmail(e: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return e.matches(regex)
    }

    fun handleLogin() {
        errorMessage = null
        successMessage = null

        if (!validateEmail(email)) {
            errorMessage = "Please enter a valid email"
            return
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return
        }

        isLoading = true
        window.setTimeout({
            isLoading = false
            successMessage = "Welcome! Login successful"
            window.setTimeout({
                onLoginSuccess(email)
            }, 800)
        }, 1500)
    }

    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            minHeight(100.vh)
            padding(24.px)
        }
    }) {
        // Logo
        Div({
            style {
                textAlign("center")
                marginBottom(32.px)
            }
        }) {
            H1({
                style {
                    fontSize(48.px)
                    fontWeight(700)
                    color(PartyColors.primary)
                    margin(0.px)
                    property("letter-spacing", "-2px")
                }
            }) { Text("Party") }
            H2({
                style {
                    fontSize(36.px)
                    fontWeight(600)
                    color(PartyColors.onSurface)
                    margin(0.px)
                    marginTop((-8).px)
                }
            }) { Text("Gallery") }
            P({
                style {
                    color(PartyColors.onSurfaceVariant)
                    fontSize(16.px)
                    marginTop(8.px)
                }
            }) { Text("Capture and share party moments") }
        }

        // Login Card
        Div({
            style {
                backgroundColor(PartyColors.surface)
                borderRadius(16.px)
                padding(32.px)
                width(100.percent)
                maxWidth(400.px)
                property("box-shadow", "0 4px 24px rgba(0,0,0,0.3)")
            }
        }) {
            // Success Message
            successMessage?.let { msg ->
                Div({
                    style {
                        backgroundColor(Color("#22C55E20"))
                        border(1.px, LineStyle.Solid, PartyColors.success)
                        borderRadius(8.px)
                        padding(12.px)
                        marginBottom(16.px)
                    }
                }) {
                    Text(msg)
                }
            }

            // Error Message
            errorMessage?.let { msg ->
                Div({
                    style {
                        backgroundColor(Color("#EF444420"))
                        border(1.px, LineStyle.Solid, PartyColors.error)
                        borderRadius(8.px)
                        padding(12.px)
                        marginBottom(16.px)
                        color(PartyColors.error)
                    }
                }) {
                    Text(msg)
                }
            }

            // Email Field
            Div({ style { marginBottom(16.px) } }) {
                Div({
                    style {
                        display(DisplayStyle.Block)
                        fontSize(14.px)
                        fontWeight(500)
                        marginBottom(8.px)
                        color(PartyColors.onSurfaceVariant)
                    }
                }) { Text("Email") }
                Input(InputType.Email) {
                    value(email)
                    onInput { email = it.value }
                    attr("placeholder", "Enter your email")
                    style {
                        width(100.percent)
                        padding(12.px, 16.px)
                        backgroundColor(PartyColors.surfaceVariant)
                        border(1.px, LineStyle.Solid, PartyColors.outline)
                        borderRadius(8.px)
                        fontSize(16.px)
                        color(PartyColors.onSurface)
                        property("outline", "none")
                    }
                }
            }

            // Password Field
            Div({ style { marginBottom(24.px) } }) {
                Div({
                    style {
                        display(DisplayStyle.Block)
                        fontSize(14.px)
                        fontWeight(500)
                        marginBottom(8.px)
                        color(PartyColors.onSurfaceVariant)
                    }
                }) { Text("Password") }
                Input(InputType.Password) {
                    value(password)
                    onInput { password = it.value }
                    attr("placeholder", "Enter your password")
                    style {
                        width(100.percent)
                        padding(12.px, 16.px)
                        backgroundColor(PartyColors.surfaceVariant)
                        border(1.px, LineStyle.Solid, PartyColors.outline)
                        borderRadius(8.px)
                        fontSize(16.px)
                        color(PartyColors.onSurface)
                        property("outline", "none")
                    }
                }
            }

            // Sign In Button
            Button({
                onClick { handleLogin() }
                if (isLoading) attr("disabled", "true")
                style {
                    width(100.percent)
                    padding(14.px)
                    backgroundColor(if (isLoading) PartyColors.primaryHover else PartyColors.primary)
                    color(Color("#000000"))
                    border(0.px, LineStyle.None, Color.transparent)
                    borderRadius(8.px)
                    fontSize(16.px)
                    fontWeight(600)
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
                    margin(24.px, 0.px)
                }
            }) {
                Div({ style {
                    flexGrow(1)
                    height(1.px)
                    backgroundColor(PartyColors.outline)
                } })
                Span({
                    style {
                        padding(0.px, 16.px)
                        color(PartyColors.onSurfaceVariant)
                        fontSize(14.px)
                    }
                }) { Text("or continue with") }
                Div({ style {
                    flexGrow(1)
                    height(1.px)
                    backgroundColor(PartyColors.outline)
                } })
            }

            // Social Buttons
            Div({
                style {
                    display(DisplayStyle.Flex)
                    gap(12.px)
                }
            }) {
                Button({
                    style {
                        flexGrow(1)
                        padding(12.px)
                        backgroundColor(PartyColors.surfaceVariant)
                        color(PartyColors.onSurface)
                        border(1.px, LineStyle.Solid, PartyColors.outline)
                        borderRadius(8.px)
                        fontSize(14.px)
                        fontWeight(500)
                    }
                }) { Text("Google") }
                Button({
                    style {
                        flexGrow(1)
                        padding(12.px)
                        backgroundColor(PartyColors.surfaceVariant)
                        color(PartyColors.onSurface)
                        border(1.px, LineStyle.Solid, PartyColors.outline)
                        borderRadius(8.px)
                        fontSize(14.px)
                        fontWeight(500)
                    }
                }) { Text("Apple") }
            }
        }

        // Sign Up Link
        Div({
            style {
                marginTop(24.px)
                color(PartyColors.onSurfaceVariant)
            }
        }) {
            Text("Don't have an account? ")
            Span({
                style {
                    color(PartyColors.primary)
                    cursor("pointer")
                    fontWeight(500)
                }
            }) { Text("Sign Up") }
        }
    }
}

@Composable
fun HomeScreen(userEmail: String, onLogout: () -> Unit) {
    val userName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

    Div({
        style {
            minHeight(100.vh)
            backgroundColor(PartyColors.background)
        }
    }) {
        // Top Bar
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                padding(16.px, 24.px)
                backgroundColor(PartyColors.surface)
                property("border-bottom", "1px solid ${PartyColors.outline}")
            }
        }) {
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
            Button({
                onClick { onLogout() }
                style {
                    padding(8.px, 16.px)
                    backgroundColor(Color.transparent)
                    color(PartyColors.onSurfaceVariant)
                    border(1.px, LineStyle.Solid, PartyColors.outline)
                    borderRadius(6.px)
                    fontSize(14.px)
                }
            }) { Text("Logout") }
        }

        // Welcome Section
        Div({
            style {
                padding(32.px, 24.px)
                textAlign("center")
            }
        }) {
            H2({
                style {
                    fontSize(32.px)
                    fontWeight(600)
                    margin(0.px)
                }
            }) { Text("Welcome, $userName!") }
            P({
                style {
                    color(PartyColors.onSurfaceVariant)
                    fontSize(16.px)
                    marginTop(8.px)
                }
            }) { Text("Ready to capture some party moments?") }
        }

        // Party Feed Placeholder
        val partyNames = listOf(
            "Summer Beach Bash",
            "Rooftop Vibes",
            "Neon Nights",
            "House Party",
            "Club Night",
            "Pool Party"
        )
        val venues = listOf(
            "Skybar Rooftop",
            "Club Paradiso",
            "Beach House",
            "The Warehouse",
            "Downtown Loft",
            "Garden Terrace"
        )

        Div({
            style {
                display(DisplayStyle.Grid)
                property("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
                gap(16.px)
                padding(0.px, 24.px, 100.px)
                maxWidth(1200.px)
                property("margin", "0 auto")
            }
        }) {
            // Mock party cards
            partyNames.forEachIndexed { index, title ->
                PartyCard(
                    title = title,
                    venue = venues[index],
                    attendees = (20..200).random(),
                    isLive = index == 0
                )
            }
        }

        // Bottom Nav (Placeholder)
        Div({
            style {
                position(Position.Fixed)
                property("bottom", "0")
                property("left", "0")
                property("right", "0")
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceAround)
                padding(16.px)
                backgroundColor(PartyColors.surface)
                property("border-top", "1px solid ${PartyColors.outline}")
            }
        }) {
            listOf("Home", "Favorites", "Studio", "Profile").forEachIndexed { idx, label ->
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                        alignItems(AlignItems.Center)
                        color(if (idx == 0) PartyColors.primary else PartyColors.onSurfaceVariant)
                        cursor("pointer")
                    }
                }) {
                    Span({ style { fontSize(12.px) } }) { Text(label) }
                }
            }
        }
    }
}

@Composable
fun PartyCard(title: String, venue: String, attendees: Int, isLive: Boolean) {
    Div({
        style {
            backgroundColor(PartyColors.surface)
            borderRadius(12.px)
            overflow("hidden")
            property("box-shadow", "0 2px 8px rgba(0,0,0,0.2)")
        }
    }) {
        // Image placeholder
        Div({
            style {
                height(160.px)
                backgroundColor(PartyColors.surfaceVariant)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                position(Position.Relative)
            }
        }) {
            Span({
                style {
                    color(PartyColors.onSurfaceVariant)
                    fontSize(14.px)
                }
            }) { Text("Party Image") }

            if (isLive) {
                Div({
                    style {
                        position(Position.Absolute)
                        property("top", "12px")
                        property("left", "12px")
                        backgroundColor(PartyColors.error)
                        color(PartyColors.onSurface)
                        padding(4.px, 8.px)
                        borderRadius(4.px)
                        fontSize(12.px)
                        fontWeight(600)
                    }
                }) { Text("LIVE") }
            }
        }

        // Content
        Div({
            style {
                padding(16.px)
            }
        }) {
            H3({
                style {
                    fontSize(18.px)
                    fontWeight(600)
                    margin(0.px)
                    marginBottom(4.px)
                }
            }) { Text(title) }
            P({
                style {
                    fontSize(14.px)
                    color(PartyColors.onSurfaceVariant)
                    margin(0.px)
                    marginBottom(8.px)
                }
            }) { Text(venue) }
            Div({
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                }
            }) {
                Span({
                    style {
                        fontSize(14.px)
                        color(PartyColors.secondary)
                    }
                }) { Text("$attendees attending") }
                Button({
                    style {
                        padding(6.px, 12.px)
                        backgroundColor(PartyColors.primary)
                        color(Color("#000000"))
                        border(0.px, LineStyle.None, Color.transparent)
                        borderRadius(6.px)
                        fontSize(12.px)
                        fontWeight(600)
                    }
                }) { Text("Join") }
            }
        }
    }
}
