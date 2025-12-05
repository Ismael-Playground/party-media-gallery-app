package com.partygallery.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Party Gallery Design System - Shapes
 *
 * Consistent corner radius values across the app.
 * Modern, rounded aesthetic fitting the party/social context.
 */
object PartyGalleryShapes {

    // ============================================
    // CORNER RADII
    // ============================================

    /** Extra small - Chips, small elements */
    val extraSmall = RoundedCornerShape(4.dp)

    /** Small - Buttons, input fields */
    val small = RoundedCornerShape(8.dp)

    /** Medium - Cards, dialogs */
    val medium = RoundedCornerShape(12.dp)

    /** Large - Bottom sheets, large cards */
    val large = RoundedCornerShape(16.dp)

    /** Extra large - Modal dialogs */
    val extraLarge = RoundedCornerShape(20.dp)

    /** Full - Pills, circular elements */
    val full = RoundedCornerShape(50)

    // ============================================
    // SPECIFIC SHAPES
    // ============================================

    /** Media card shape */
    val mediaCard = RoundedCornerShape(12.dp)

    /** Profile avatar shape */
    val avatar = RoundedCornerShape(50)

    /** Button shape */
    val button = RoundedCornerShape(12.dp)

    /** Chip shape */
    val chip = RoundedCornerShape(8.dp)

    /** Input field shape */
    val inputField = RoundedCornerShape(12.dp)

    /** Bottom sheet shape */
    val bottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    /** Modal dialog shape */
    val dialog = RoundedCornerShape(20.dp)

    /** Floating action button shape */
    val fab = RoundedCornerShape(16.dp)

    /** Tab indicator shape */
    val tabIndicator = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 4.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    /** Search bar shape */
    val searchBar = RoundedCornerShape(24.dp)

    /** Navigation bar item indicator */
    val navBarIndicator = RoundedCornerShape(50)

    /** Story/highlight shape */
    val story = RoundedCornerShape(50)

    /** Mood badge shape */
    val moodBadge = RoundedCornerShape(8.dp)
}
