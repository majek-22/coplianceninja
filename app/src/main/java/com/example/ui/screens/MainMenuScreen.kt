package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AvatarHelper
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.TextPrimary

@Composable
fun MainMenuScreen(
    currentUser: String?,
    userAvatarId: Int = 1,
    highScore: Int,
    currentLanguage: String,
    isAudioMuted: Boolean,
    onStartShift: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenGlossary: () -> Unit,
    onOpenProfile: () -> Unit = {},
    onToggleLanguage: () -> Unit,
    onToggleAudioMute: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menu_anim")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_title"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_button"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isLandscape = maxWidth > maxHeight

        // 1. Scenic Main Menu Background Art
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Translucent dark vignette layer for perfect legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x990A1128),
                            Color(0xBB0B132B),
                            Color(0xEE0B132B)
                        )
                    )
                )
        )

        // 3. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Officer info (opens Profile), Language toggle, Audio mute, Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Officer badge with avatar, clickable to open Profile
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x44000000),
                    border = BorderStroke(1.dp, Color(0x5564B5F6)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenProfile() }
                        .testTag("menu_profile_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarRes = AvatarHelper.getAvatarRes(userAvatarId, currentUser ?: "")
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0097D4))
                                .border(1.dp, GoldSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentUser ?: "Officer",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Controls group: neat ~30dp containers within minimum 48dp touch targets
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language toggle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onToggleLanguage)
                            .testTag("menu_lang_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x44000000))
                                .border(1.2.dp, GoldSecondary.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Crossfade(
                                targetState = currentLanguage,
                                animationSpec = tween(250),
                                label = "language_toggle_crossfade"
                            ) { lang ->
                                Text(
                                    text = if (lang == "in" || lang == "id") "ID" else "EN",
                                    color = GoldSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Audio mute toggle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onToggleAudioMute)
                            .testTag("menu_audio_mute_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x44000000))
                                .border(
                                    1.2.dp,
                                    if (isAudioMuted) Color(0x88FF5252) else Color(0x884FCB8F),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAudioMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Mute",
                                tint = if (isAudioMuted) Color(0xFFFF5252) else MintSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Logout button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onLogout)
                            .testTag("menu_logout_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x44FF5252))
                                .border(1.2.dp, Color(0x88FF5252), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = stringResource(R.string.auth_logout),
                                tint = Color(0xFFFF8A80),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Main Body Content
            if (isLandscape) {
                // Landscape split: Left = Title & Mascot/Stats, Right = Circular Action Item Nodes
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Branding, High Score, Mascot Emblem
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mascot_owl_transparent),
                                contentDescription = "Bang Patuh Ninja Mascot",
                                modifier = Modifier.size(86.dp),
                                contentScale = ContentScale.Fit
                            )

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "BANG PATUH",
                                    color = Color(0xFFBC851C),
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = stringResource(R.string.menu_title),
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 17.sp,
                                        lineHeight = 22.sp
                                    ),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.graphicsLayer { translationY = floatingOffset }
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // High Score Banner: Aligned directly underneath Compliance Ninja
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color(0x44000000), RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.menu_high_score_label).uppercase(),
                                        color = Color(0xFFB0BEC5),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = String.format("%06d", highScore),
                                        color = GoldSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    // Right Column: Circular Action Item Nodes (Start, Rankings, RULES - equal sizes, click & slash)
                    Row(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Start / Play Button (Round circular item - Click & Slash)
                        MenuCircleActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = stringResource(R.string.menu_start_shift),
                            primaryColor = Color(0xFFFFD54F),
                            secondaryColor = CoralPrimary,
                            onClick = onStartShift,
                            size = 68.dp,
                            iconSize = 34.dp,
                            isPrimary = true,
                            pulseScale = pulseScale,
                            testTag = "start_shift_button"
                        )

                        // 2. Rankings / Leaderboard Button (Round circular item - Click & Slash)
                        MenuCircleActionButton(
                            icon = Icons.Default.EmojiEvents,
                            label = stringResource(R.string.menu_leaderboard),
                            primaryColor = Color(0xFFFFD54F),
                            onClick = onOpenLeaderboard,
                            size = 68.dp,
                            iconSize = 34.dp,
                            isPrimary = false,
                            testTag = "menu_leaderboard_btn"
                        )

                        // 3. RULES / Glossary Button (Round circular item - Click & Slash)
                        MenuCircleActionButton(
                            icon = Icons.Default.Book,
                            label = stringResource(R.string.menu_glossary),
                            primaryColor = Color(0xFF64B5F6),
                            onClick = onOpenGlossary,
                            size = 68.dp,
                            iconSize = 34.dp,
                            isPrimary = false,
                            testTag = "menu_glossary_btn"
                        )
                    }
                }
            } else {
                // Portrait Layout
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mascot_owl_transparent),
                        contentDescription = "Bang Patuh Ninja Mascot",
                        modifier = Modifier.size(96.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "BANG PATUH",
                        color = Color(0xFFBC851C),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.menu_title),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 17.sp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    // High score pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .background(Color(0x44000000), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.menu_high_score_label).uppercase(),
                            color = Color(0xFFB0BEC5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("%06d", highScore),
                            color = GoldSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Round circular action items in Portrait (Start, Rankings, RULES - Equal sizes, Click & Slash)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MenuCircleActionButton(
                            icon = Icons.Default.PlayArrow,
                            label = stringResource(R.string.menu_start_shift),
                            primaryColor = Color(0xFFFFD54F),
                            secondaryColor = CoralPrimary,
                            onClick = onStartShift,
                            size = 68.dp,
                            iconSize = 34.dp,
                            isPrimary = true,
                            pulseScale = pulseScale,
                            testTag = "start_shift_button"
                        )

                        MenuCircleActionButton(
                            icon = Icons.Default.EmojiEvents,
                            label = stringResource(R.string.menu_leaderboard),
                            primaryColor = Color(0xFFFFD54F),
                            onClick = onOpenLeaderboard,
                            size = 68.dp,
                            iconSize = 34.dp,
                            isPrimary = false,
                            testTag = "menu_leaderboard_btn"
                        )

                        MenuCircleActionButton(
                            icon = Icons.Default.Book,
                            label = stringResource(R.string.menu_glossary),
                            primaryColor = Color(0xFF64B5F6),
                            onClick = onOpenGlossary,
                            size = 68.dp,
                            iconSize = 34.dp,
                            isPrimary = false,
                            testTag = "menu_glossary_btn"
                        )
                    }
                }
            }

            // Bottom subtle footer
            Text(
                text = stringResource(R.string.menu_footer_features),
                color = Color(0xFF90CAF9).copy(alpha = 0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun MenuCircleActionButton(
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    label: String,
    primaryColor: Color,
    secondaryColor: Color = primaryColor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp,
    iconSize: Dp = 34.dp,
    isPrimary: Boolean = false,
    pulseScale: Float = 1f,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .scale(pulseScale)
                .size(size)
                .clip(CircleShape)
                .background(
                    if (isPrimary) {
                        Brush.radialGradient(
                            colors = listOf(primaryColor, secondaryColor, Color(0xFFD97706))
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(Color(0xEE1E293B), Color(0xFA0F172A))
                        )
                    }
                )
                .border(
                    width = if (isPrimary) 2.5.dp else 1.8.dp,
                    brush = if (isPrimary) {
                        Brush.sweepGradient(listOf(primaryColor, secondaryColor, primaryColor))
                    } else {
                        SolidColor(primaryColor.copy(alpha = 0.85f))
                    },
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .pointerInput(onClick) {
                    detectDragGestures(
                        onDragStart = {
                            onClick()
                        },
                        onDrag = { _, _ -> }
                    )
                }
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isPrimary) BackgroundDark else primaryColor,
                    modifier = Modifier.size(iconSize)
                )
            } else if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = label,
                    tint = if (isPrimary) BackgroundDark else primaryColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isPrimary) primaryColor.copy(alpha = 0.22f) else Color(0x44000000),
            border = BorderStroke(1.dp, if (isPrimary) primaryColor.copy(alpha = 0.6f) else Color(0x44FFFFFF))
        ) {
            Text(
                text = label.uppercase(),
                color = if (isPrimary) primaryColor else TextPrimary,
                fontSize = if (isPrimary) 12.sp else 10.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.6.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

