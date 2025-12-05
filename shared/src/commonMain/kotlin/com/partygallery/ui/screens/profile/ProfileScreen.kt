package com.partygallery.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partygallery.presentation.intent.ProfileIntent
import com.partygallery.presentation.intent.ProfileTab
import com.partygallery.presentation.state.ProfileMediaItem
import com.partygallery.presentation.state.ProfileState
import com.partygallery.presentation.store.ProfileStore

/**
 * Profile screen with avatar, stats, and media grid.
 *
 * S5-004: ProfileScreen con Avatar gradient y estadísticas
 */

// Dark Mode First color palette
private val PartyBackground = Color(0xFF0A0A0A)
private val PartySurface = Color(0xFF141414)
private val PartySurfaceVariant = Color(0xFF1E1E1E)
private val PartyPrimary = Color(0xFFF59E0B)
private val PartySecondary = Color(0xFFFBBF24)
private val PartyOnSurface = Color(0xFFFFFFFF)
private val PartyOnSurfaceVariant = Color(0xFFA1A1AA)
private val PartyOutline = Color(0xFF3F3F46)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    store: ProfileStore = remember { ProfileStore() },
    userId: String? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val state by store.state.collectAsState()

    LaunchedEffect(userId) {
        store.processIntent(ProfileIntent.LoadProfile(userId))
    }

    Scaffold(
        containerColor = PartyBackground,
        topBar = {
            ProfileTopBar(
                state = state,
                onBackClick = onNavigateBack,
                onSettingsClick = onNavigateToSettings,
                onEditClick = { store.processIntent(ProfileIntent.StartEditing) },
            )
        },
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = PartyPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Cover photo area
                CoverPhotoSection(
                    coverUrl = state.user?.coverPhotoUrl,
                    isCurrentUser = state.isCurrentUser,
                    onChangeCover = { store.processIntent(ProfileIntent.ShowCoverPicker) },
                )

                // Profile info section
                ProfileInfoSection(
                    state = state,
                    onFollowClick = {
                        if (state.isFollowing) {
                            store.processIntent(ProfileIntent.UnfollowUser)
                        } else {
                            store.processIntent(ProfileIntent.FollowUser)
                        }
                    },
                    onFollowersClick = { store.processIntent(ProfileIntent.ShowFollowers) },
                    onFollowingClick = { store.processIntent(ProfileIntent.ShowFollowing) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content tabs
                ProfileTabsSection(
                    selectedTab = state.selectedTab,
                    postsCount = state.postsCount,
                    partiesCount = state.partiesHostedCount,
                    onTabSelected = { store.processIntent(ProfileIntent.SelectTab(it)) },
                )

                // Content grid
                ProfileContentGrid(
                    state = state,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    state: ProfileState,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = state.user?.username ?: "",
                color = PartyOnSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            if (!state.isCurrentUser) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PartyOnSurface,
                    )
                }
            }
        },
        actions = {
            if (state.isCurrentUser) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = PartyOnSurface,
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = PartyOnSurface,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PartyBackground,
        ),
    )
}

@Composable
private fun CoverPhotoSection(
    coverUrl: String?,
    isCurrentUser: Boolean,
    onChangeCover: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(PartySurfaceVariant),
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PartyBackground.copy(alpha = 0.8f),
                        ),
                    ),
                ),
        )

        // Change cover button
        if (isCurrentUser) {
            IconButton(
                onClick = onChangeCover,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change Cover",
                    tint = PartyOnSurface.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoSection(
    state: ProfileState,
    onFollowClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-50).dp)
            .padding(horizontal = 16.dp),
    ) {
        // Avatar with gradient border
        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            ProfileAvatar(
                avatarUrl = state.user?.avatarUrl,
                initials = state.user?.initials ?: "?",
                isVerified = state.user?.isVerified ?: false,
                size = 100,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Display name
        Text(
            text = state.user?.displayName ?: "",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PartyOnSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        // Username with verified badge
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "@${state.user?.username ?: ""}",
                fontSize = 14.sp,
                color = PartyOnSurfaceVariant,
            )
            if (state.user?.isVerified == true) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified",
                    tint = PartyPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        // Bio
        state.user?.bio?.let { bio ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bio,
                fontSize = 14.sp,
                color = PartyOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem(
                value = state.postsCount.toString(),
                label = "Posts",
            )
            StatItem(
                value = formatCount(state.user?.followersCount ?: 0),
                label = "Followers",
                onClick = onFollowersClick,
            )
            StatItem(
                value = formatCount(state.user?.followingCount ?: 0),
                label = "Following",
                onClick = onFollowingClick,
            )
            StatItem(
                value = state.partiesHostedCount.toString(),
                label = "Parties",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Follow/Edit button
        if (!state.isCurrentUser) {
            FollowButton(
                isFollowing = state.isFollowing,
                isLoading = state.isFollowLoading,
                onClick = onFollowClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Social links
        if (state.user?.socialLinks?.hasAnyLink == true) {
            Spacer(modifier = Modifier.height(12.dp))
            SocialLinksRow(
                instagram = state.user?.socialLinks?.instagram,
                tiktok = state.user?.socialLinks?.tiktok,
                twitter = state.user?.socialLinks?.twitter,
                spotify = state.user?.socialLinks?.spotify,
            )
        }

        // Tags
        if (state.user?.tags?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.height(12.dp))
            TagsRow(tags = state.user?.tags ?: emptyList())
        }
    }
}

@Composable
private fun ProfileAvatar(
    avatarUrl: String?,
    initials: String,
    isVerified: Boolean,
    size: Int,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(PartyPrimary, PartySecondary),
                ),
                shape = CircleShape,
            )
            .padding(3.dp)
            .clip(CircleShape)
            .background(PartySurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        // Placeholder with initials (would use Coil/Kamel for actual image loading)
        Text(
            text = initials,
            fontSize = (size / 3).sp,
            fontWeight = FontWeight.Bold,
            color = PartyPrimary,
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    onClick: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(8.dp),
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PartyOnSurface,
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = PartyOnSurfaceVariant,
        )
    }
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isFollowing) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PartyOnSurface,
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, PartyOutline),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = PartyOnSurface,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Following")
            }
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PartyPrimary,
                contentColor = PartyBackground,
            ),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = PartyBackground,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Follow")
            }
        }
    }
}

@Composable
private fun SocialLinksRow(
    instagram: String?,
    tiktok: String?,
    twitter: String?,
    spotify: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        instagram?.let {
            SocialChip(platform = "IG", handle = it)
            Spacer(modifier = Modifier.width(8.dp))
        }
        tiktok?.let {
            SocialChip(platform = "TT", handle = it)
            Spacer(modifier = Modifier.width(8.dp))
        }
        twitter?.let {
            SocialChip(platform = "X", handle = it)
            Spacer(modifier = Modifier.width(8.dp))
        }
        spotify?.let {
            SocialChip(platform = "SP", handle = it)
        }
    }
}

@Composable
private fun SocialChip(
    platform: String,
    handle: String,
) {
    Row(
        modifier = Modifier
            .background(PartySurfaceVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = platform,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PartyPrimary,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "@$handle",
            fontSize = 12.sp,
            color = PartyOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TagsRow(tags: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(tags) { tag ->
            Text(
                text = tag,
                fontSize = 12.sp,
                color = PartyPrimary,
                modifier = Modifier
                    .border(1.dp, PartyPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun ProfileTabsSection(
    selectedTab: ProfileTab,
    postsCount: Int,
    partiesCount: Int,
    onTabSelected: (ProfileTab) -> Unit,
) {
    val tabs = ProfileTab.entries

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor = PartyBackground,
        contentColor = PartyOnSurface,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)])
                    .height(2.dp)
                    .background(PartyPrimary),
            )
        },
        divider = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PartyOutline),
            )
        },
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.label,
                        color = if (tab == selectedTab) PartyPrimary else PartyOnSurfaceVariant,
                        fontWeight = if (tab == selectedTab) FontWeight.Bold else FontWeight.Normal,
                    )
                },
            )
        }
    }
}

@Composable
private fun ProfileContentGrid(
    state: ProfileState,
    modifier: Modifier = Modifier,
) {
    val items = when (state.selectedTab) {
        ProfileTab.Posts -> state.posts
        ProfileTab.Parties -> emptyList() // Parties use different layout
        ProfileTab.Tagged -> state.taggedMedia
        ProfileTab.Saved -> state.savedMedia
    }

    if (state.selectedTab == ProfileTab.Parties) {
        // Party list view
        if (state.parties.isEmpty()) {
            EmptyTabContent(
                tab = state.selectedTab,
                isCurrentUser = state.isCurrentUser,
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.height(400.dp),
            ) {
                items(state.parties) { party ->
                    PartyGridItem(
                        title = party.title,
                        venueName = party.venue.name,
                        attendeesCount = party.attendeesCount,
                    )
                }
            }
        }
    } else {
        // Media grid view
        if (items.isEmpty()) {
            EmptyTabContent(
                tab = state.selectedTab,
                isCurrentUser = state.isCurrentUser,
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = modifier.height(400.dp),
            ) {
                items(items) { item ->
                    MediaGridItem(item = item)
                }
            }
        }
    }
}

@Composable
private fun MediaGridItem(item: ProfileMediaItem) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(PartySurfaceVariant)
            .clickable { /* Navigate to media detail */ },
    ) {
        // Placeholder for image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PartySurfaceVariant,
                            PartySurface,
                        ),
                    ),
                ),
        )

        // Video indicator
        if (item.isVideo) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Video",
                tint = PartyOnSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(20.dp),
            )
        }
    }
}

@Composable
private fun PartyGridItem(
    title: String,
    venueName: String,
    attendeesCount: Int,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(PartySurfaceVariant, RoundedCornerShape(12.dp))
            .clickable { /* Navigate to party detail */ },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PartyOnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Column {
                Text(
                    text = venueName,
                    fontSize = 12.sp,
                    color = PartyOnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$attendeesCount attending",
                    fontSize = 11.sp,
                    color = PartyPrimary,
                )
            }
        }
    }
}

@Composable
private fun EmptyTabContent(
    tab: ProfileTab,
    isCurrentUser: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = when (tab) {
                    ProfileTab.Posts -> if (isCurrentUser) "Share your first moment" else "No posts yet"
                    ProfileTab.Parties -> if (isCurrentUser) "Host your first party" else "No parties hosted"
                    ProfileTab.Tagged -> "No tagged photos"
                    ProfileTab.Saved -> "No saved items"
                },
                fontSize = 16.sp,
                color = PartyOnSurfaceVariant,
            )

            if (isCurrentUser && (tab == ProfileTab.Posts || tab == ProfileTab.Parties)) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Navigate to create */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PartyPrimary,
                        contentColor = PartyBackground,
                    ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (tab == ProfileTab.Posts) "Create Post" else "Create Party")
                }
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val value = count / 1_000_000.0
            val rounded = kotlin.math.round(value * 10) / 10
            "${if (rounded == rounded.toLong().toDouble()) rounded.toLong() else rounded}M"
        }
        count >= 1_000 -> {
            val value = count / 1_000.0
            val rounded = kotlin.math.round(value * 10) / 10
            "${if (rounded == rounded.toLong().toDouble()) rounded.toLong() else rounded}K"
        }
        else -> count.toString()
    }
}
