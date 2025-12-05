package com.partygallery.ui.navigation

/**
 * Main navigation tabs for Party Gallery.
 *
 * S3-001: Navigation routes
 * S3-003: Sealed class PartyScreen rutas
 *
 * Design: Dark Mode First (bg #0A0A0A, active #F59E0B)
 */
sealed class MainTab(
    val route: String,
    val index: Int,
    val title: String,
) {
    data object Home : MainTab(
        route = "home",
        index = 0,
        title = "Home",
    )

    data object Favorites : MainTab(
        route = "favorites",
        index = 1,
        title = "Favorites",
    )

    data object Studio : MainTab(
        route = "studio",
        index = 2,
        title = "Studio",
    )

    data object Profile : MainTab(
        route = "profile",
        index = 3,
        title = "Profile",
    )

    companion object {
        fun fromIndex(index: Int): MainTab = when (index) {
            0 -> Home
            1 -> Favorites
            2 -> Studio
            3 -> Profile
            else -> Home
        }

        val all = listOf(Home, Favorites, Studio, Profile)
    }
}

/**
 * Navigation item data for BottomNavBar.
 */
data class NavItem(
    val tab: MainTab,
    val label: String,
    val selectedIcon: String,
    val unselectedIcon: String,
)

/**
 * List of all main navigation items.
 */
val mainNavItems = listOf(
    NavItem(
        tab = MainTab.Home,
        label = "Home",
        selectedIcon = "home_filled",
        unselectedIcon = "home_outline",
    ),
    NavItem(
        tab = MainTab.Favorites,
        label = "Favorites",
        selectedIcon = "star_filled",
        unselectedIcon = "star_outline",
    ),
    NavItem(
        tab = MainTab.Studio,
        label = "Studio",
        selectedIcon = "camera_filled",
        unselectedIcon = "camera_outline",
    ),
    NavItem(
        tab = MainTab.Profile,
        label = "Profile",
        selectedIcon = "person_filled",
        unselectedIcon = "person_outline",
    ),
)
