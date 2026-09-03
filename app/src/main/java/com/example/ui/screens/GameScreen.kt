package com.example.ui.screens

import android.app.Activity
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BadgeShape
import com.example.data.ComplianceCategory
import com.example.data.FloatingPopup
import com.example.data.GameItem
import com.example.data.SliceTrailPoint
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundGradientEnd
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.CardLegitimateEnd
import com.example.ui.theme.CardLegitimateStart
import com.example.ui.theme.CardViolationEnd
import com.example.ui.theme.CardViolationStart
import com.example.ui.theme.ComboBadgeStyle
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassWhite05
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite20
import com.example.ui.theme.GlassWhite40
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.HeaderBorderBottom
import com.example.ui.theme.HeaderGlassBg
import com.example.ui.theme.HudLabelStyle
import com.example.ui.theme.HudScoreDigitsStyle
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.SheetContainerBg
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TimerDigitsStyle
import com.example.ui.viewmodel.GameUiState
import com.example.ui.viewmodel.GameViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    onOpenGlossary: () -> Unit,
    onReturnToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconPainters = rememberCategoryPainters()
    val sliceTrail = remember { mutableStateListOf<SliceTrailPoint>() }

    val context = LocalContext.current
    val window = (context as? Activity)?.window

    // Immersive full-screen mode during active gameplay so notifications/bars don't interfere with slicing
    DisposableEffect(window) {
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())

            onDispose {
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }
        } else {
            onDispose { }
        }
    }

    val popupPaint = remember {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
    }

    LaunchedEffect(uiState.isPaused) {
        if (!uiState.isPaused) {
            var lastFrameNanos = withFrameNanos { it }
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    val dt = (frameTimeNanos - lastFrameNanos) / 1_000_000_000f
                    lastFrameNanos = frameTimeNanos
                    viewModel.updateFrame(dt)

                    val now = System.nanoTime()
                    val threshold = 180_000_000L
                    sliceTrail.removeAll { (now - it.timestampNanos) > threshold }
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shake_anim")
    val shakeFactor by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(60, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    val shakeOffsetX = (shakeFactor * uiState.screenShakeIntensity * 0.8f).toInt()
    val shakeOffsetY = (shakeFactor * uiState.screenShakeIntensity * 0.5f).toInt()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(shakeOffsetX, shakeOffsetY) }
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        LaunchedEffect(widthPx, heightPx) {
            viewModel.setScreenDimensions(widthPx, heightPx)
        }

        // 1. FULLSCREEN GAMEPLAY BACKGROUND IMAGE (DYNAMIC PER MISSION)
        Image(
            painter = painterResource(id = uiState.selectedLevel.backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Safe-Zone Debug Overlay (Feature 3)
        if (uiState.debugOverlayEnabled) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val left = viewModel.engine.safeZoneLeft
                val right = viewModel.engine.safeZoneRight
                val top = viewModel.engine.safeZoneTop
                val bottom = viewModel.engine.safeZoneBottom

                drawRect(
                    color = Color(0x1800E5FF),
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top)
                )
                drawRect(
                    color = Color(0x8800E5FF),
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    style = Stroke(width = 2.5f)
                )
                drawLine(
                    color = Color(0xFFFFD54F),
                    start = Offset(0f, top),
                    end = Offset(size.width, top),
                    strokeWidth = 2f
                )
            }
        }

        // 2. ACTIVE FLYING ITEMS: Rendered as discrete Compose composables driven by SnapshotStateList
        for (item in viewModel.engine.activeItems) {
            key(item.id) {
                FlyingItemComposable(
                    item = item,
                    painter = iconPainters[item.category]
                )
            }
        }

        // 3. FOREGROUND INTERACTIVE CANVAS (Slice Trail, Particles, Popups, Pointer Input)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("playfield_canvas")
                .pointerInput(uiState.isPaused) {
                    if (!uiState.isPaused) {
                        var lastX = 0f
                        var lastY = 0f
                        detectDragGestures(
                            onDragStart = { offset ->
                                lastX = offset.x
                                lastY = offset.y
                                val now = System.nanoTime()
                                sliceTrail.clear()
                                sliceTrail.add(SliceTrailPoint(lastX, lastY, now))
                                viewModel.onSliceStart()
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val curX = change.position.x
                                val curY = change.position.y
                                val now = System.nanoTime()

                                sliceTrail.add(SliceTrailPoint(curX, curY, now))
                                viewModel.onSliceSegment(lastX, lastY, curX, curY)

                                lastX = curX
                                lastY = curY
                            },
                            onDragEnd = {
                                sliceTrail.clear()
                                viewModel.onSliceEnd()
                            },
                            onDragCancel = {
                                sliceTrail.clear()
                                viewModel.onSliceEnd()
                            }
                        )
                    }
                }
        ) {
            // Draw particle bursts
            val particles = viewModel.engine.particles.toList()
            for (p in particles) {
                drawCircle(
                    color = Color(p.color).copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(p.x, p.y)
                )
            }

            // Draw slice trail
            drawSliceTrail(sliceTrail)

            // Draw floating popups
            val popups = viewModel.engine.popups.toList()
            for (popup in popups) {
                drawFloatingPopup(popup, popupPaint)
            }
        }

        // 2. TOP HUD BAR (Time and Volume to the left of lives, no pause button, clean flat background)
        TopHudBar(
            levelNumber = uiState.selectedLevel.levelNumber,
            score = uiState.score,
            lives = uiState.lives,
            comboMultiplier = uiState.comboMultiplier,
            elapsedSeconds = uiState.timeRemaining,
            isAudioMuted = uiState.isAudioMuted,
            onToggleAudioMute = { viewModel.toggleAudioMute() },
            isPaused = uiState.isPaused,
            onTogglePause = { if (uiState.isPaused) viewModel.resumeGame() else viewModel.pauseGame() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        )

        // Pause overlay dialog
        if (uiState.isPaused) {
            AlertDialog(
                onDismissRequest = { viewModel.resumeGame() },
                title = {
                    Text(
                        text = stringResource(R.string.dialog_paused_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(text = stringResource(R.string.menu_subtitle))
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.resumeGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                    ) {
                        Text(stringResource(R.string.dialog_resume))
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = onReturnToMenu
                    ) {
                        Text(stringResource(R.string.dialog_quit_to_menu))
                    }
                }
            )
        }

        // 3. SLICE ALERT / FEEDBACK BANNER
        AnimatedVisibility(
            visible = uiState.feedbackMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 76.dp, start = 20.dp, end = 20.dp)
        ) {
            uiState.feedbackMessage?.let { msg ->
                val bannerBorder = if (uiState.feedbackIsPositive) MintSuccess else CrimsonDanger
                val bannerBg = if (uiState.feedbackIsPositive) Color(0xEE143627) else Color(0xEE3F1218)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = bannerBg,
                    border = BorderStroke(1.5.dp, bannerBorder),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.feedbackIsPositive) Icons.Default.Favorite else Icons.Default.Warning,
                            contentDescription = null,
                            tint = bannerBorder,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }
            }
        }

        // 4. FULLSCREEN IMPACT FLASH OVERLAY
        uiState.flashOverlayColor?.let { flashColor ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(flashColor))
            )
        }

        // 5. READY COUNTDOWN OVERLAY (3 Seconds Orange Banner)
        if (uiState.readyCountdown > 0f) {
            val countdownInt = (uiState.readyCountdown.toInt() + 1).coerceIn(1, 3)
            val readyTransition = rememberInfiniteTransition(label = "ready_pulse")
            val readyScale by readyTransition.animateFloat(
                initialValue = 0.94f,
                targetValue = 1.06f,
                animationSpec = infiniteRepeatable(
                    animation = tween(450, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "ready_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.scale(readyScale)
                ) {
                    Text(
                        text = "Ready...",
                        color = Color(0xFFFF9800), // Orange
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color(0xCC000000),
                                offset = Offset(4f, 4f),
                                blurRadius = 14f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FF9800))
                            .border(2.dp, Color(0xFFFF9800), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$countdownInt",
                            color = Color(0xFFFFB74D),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // 6. GAME OVER BANNER (Red)
        if (uiState.isGameOverBannerShowing) {
            val goTransition = rememberInfiniteTransition(label = "game_over_pulse")
            val goScale by goTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "go_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.scale(goScale)
                ) {
                    Text(
                        text = "GAME OVER",
                        color = Color(0xFFFF2A2A), // Bold Red
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color(0xFF000000),
                                offset = Offset(6f, 6f),
                                blurRadius = 18f
                            )
                        )
                    )
                }
            }
        }
    }
}

// =========================================================================
// CANVAS RENDERING HELPERS
// =========================================================================

@Composable
private fun FlyingItemComposable(
    item: GameItem,
    painter: Painter?,
    modifier: Modifier = Modifier
) {
    if (painter == null) return

    val categoryColor = when {
        item.category.isBonus -> Color(0xFF00E5FF)
        item.category.isTrap -> Color(0xFFFF7043)
        item.isViolation -> Color(0xFFFFC857)
        else -> Color(0xFF64B5F6)
    }

    val discBgColor = when {
        item.category.isBonus -> Color(0xFF043842)
        item.category.isTrap -> Color(0xFF45190C)
        item.isViolation -> Color(0xFF3E2805)
        else -> Color(0xFF0C2C4D)
    }

    if (!item.sliced) {
        val density = LocalDensity.current
        val diameterDp = with(density) { (item.radius * 2f).toDp() }

        Box(
            modifier = modifier
                .offset {
                    IntOffset(
                        (item.x - item.radius).toInt(),
                        (item.y - item.radius).toInt()
                    )
                }
                .size(diameterDp)
                .graphicsLayer {
                    alpha = item.alpha
                }
        ) {
            // Rotating vector token & neon aura
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = item.rotation
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.width / 2f

                    // 1. Radiant outer neon aura matching RULES category color
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                categoryColor.copy(alpha = 0.75f),
                                categoryColor.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            radius = r * 1.45f,
                            center = Offset(r, r)
                        ),
                        radius = r * 1.45f,
                        center = Offset(r, r)
                    )

                    // 2. Vibrant colored background disc matching RULES (not pitch black!)
                    drawCircle(
                        color = discBgColor,
                        radius = r,
                        center = Offset(r, r)
                    )

                    // Inner soft highlight disc
                    drawCircle(
                        color = categoryColor.copy(alpha = 0.25f),
                        radius = r * 0.88f,
                        center = Offset(r, r)
                    )

                    // 3. Draw original vector icon in full colors centered inside the category disc
                    val iconDrawSize = r * 1.52f
                    val iconPadding = (r * 2f - iconDrawSize) / 2f
                    withTransform({
                        translate(left = iconPadding, top = iconPadding)
                    }) {
                        with(painter) {
                            draw(size = Size(iconDrawSize, iconDrawSize))
                        }
                    }

                    // 4. Accessible, crisp rim border ring matching RULES
                    drawCircle(
                        color = categoryColor,
                        radius = r * 0.95f,
                        center = Offset(r, r),
                        style = Stroke(width = 3.5f)
                    )
                }
            }

            // Unrotated floating label badge below token for instant readability (Clean & compact bold label)
            val rawText = androidx.compose.ui.res.stringResource(item.category.displayNameRes)
            val words = rawText.split(" ").filter { it.isNotBlank() }
            val formattedText = if (words.size >= 2) words.joinToString("\n") else rawText

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 9.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xF0050D18))
                    .border(
                        width = 1.dp,
                        color = categoryColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 1.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedText.uppercase(),
                    fontSize = 7.sp,
                    lineHeight = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                    textAlign = TextAlign.Center,
                    color = categoryColor,
                    maxLines = 3,
                    softWrap = true
                )
            }
        }
    } else {
        // Sliced halves rendered on playfield
        Canvas(modifier = modifier.fillMaxSize()) {
            drawSlicedHalves(item, painter)
        }
    }
}

private fun DrawScope.drawSlicedHalves(
    item: GameItem,
    painter: Painter
) {
    val rad = item.sliceAngle * (PI.toFloat() / 180f)
    val dirX = cos(rad)
    val dirY = sin(rad)
    val normX = -sin(rad)
    val normY = cos(rad)

    val half1Center = Offset(
        item.x + item.half1OffsetX,
        item.y + item.half1OffsetY
    )
    drawSlicedHalf(
        center = half1Center,
        rotation = item.rotation + item.halfRotation1,
        radius = item.radius,
        alpha = item.alpha,
        painter = painter,
        category = item.category,
        isFirstHalf = true,
        sliceAngle = item.sliceAngle,
        dirX = dirX,
        dirY = dirY,
        normX = normX,
        normY = normY
    )

    val half2Center = Offset(
        item.x + item.half2OffsetX,
        item.y + item.half2OffsetY
    )
    drawSlicedHalf(
        center = half2Center,
        rotation = item.rotation + item.halfRotation2,
        radius = item.radius,
        alpha = item.alpha,
        painter = painter,
        category = item.category,
        isFirstHalf = false,
        sliceAngle = item.sliceAngle,
        dirX = dirX,
        dirY = dirY,
        normX = normX,
        normY = normY
    )
}

private fun DrawScope.drawSlicedHalf(
    center: Offset,
    rotation: Float,
    radius: Float,
    alpha: Float,
    painter: Painter,
    category: ComplianceCategory,
    isFirstHalf: Boolean,
    sliceAngle: Float,
    dirX: Float,
    dirY: Float,
    normX: Float,
    normY: Float
) {
    if (alpha <= 0f) return

    val clipExtent = radius * 3.5f
    val clipPath = Path().apply {
        if (isFirstHalf) {
            moveTo(center.x - dirX * clipExtent, center.y - dirY * clipExtent)
            lineTo(center.x + dirX * clipExtent, center.y + dirY * clipExtent)
            lineTo(center.x + dirX * clipExtent + normX * clipExtent, center.y + dirY * clipExtent + normY * clipExtent)
            lineTo(center.x - dirX * clipExtent + normX * clipExtent, center.y - dirY * clipExtent + normY * clipExtent)
            close()
        } else {
            moveTo(center.x - dirX * clipExtent, center.y - dirY * clipExtent)
            lineTo(center.x + dirX * clipExtent, center.y + dirY * clipExtent)
            lineTo(center.x + dirX * clipExtent - normX * clipExtent, center.y + dirY * clipExtent - normY * clipExtent)
            lineTo(center.x - dirX * clipExtent - normX * clipExtent, center.y - dirY * clipExtent - normY * clipExtent)
            close()
        }
    }

    val categoryColor = when {
        category.isBonus -> Color(0xFF00E5FF)
        category.isTrap -> Color(0xFFFF7043)
        category.isViolation -> Color(0xFFFFC857)
        else -> Color(0xFF64B5F6)
    }

    val discBgColor = when {
        category.isBonus -> Color(0xFF043842)
        category.isTrap -> Color(0xFF45190C)
        category.isViolation -> Color(0xFF3E2805)
        else -> Color(0xFF0C2C4D)
    }

    clipPath(clipPath) {
        withTransform({
            translate(left = center.x, top = center.y)
            rotate(degrees = rotation)
        }) {
            val r = radius

            // Colored backing disc matching RULES (not pitch black!)
            drawCircle(
                color = discBgColor.copy(alpha = alpha),
                radius = r
            )

            // Inner soft highlight disc
            drawCircle(
                color = categoryColor.copy(alpha = alpha * 0.25f),
                radius = r * 0.88f
            )

            // Draw vector icon in full original color
            val iconDrawSize = r * 1.52f
            val iconPadding = (r * 2f - iconDrawSize) / 2f
            withTransform({
                translate(left = -r + iconPadding, top = -r + iconPadding)
            }) {
                with(painter) {
                    draw(
                        size = Size(iconDrawSize, iconDrawSize),
                        alpha = alpha
                    )
                }
            }

            // High-contrast rim border ring
            drawCircle(
                color = categoryColor.copy(alpha = alpha),
                radius = r * 0.95f,
                style = Stroke(width = 3.5f)
            )
        }
    }

    // Bright laser cut glow along the slice seam
    val cutLength = radius * 1.05f
    drawLine(
        brush = Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = alpha * 0.95f),
                Color(category.glowColor).copy(alpha = alpha * 0.75f)
            )
        ),
        start = Offset(center.x - dirX * cutLength, center.y - dirY * cutLength),
        end = Offset(center.x + dirX * cutLength, center.y + dirY * cutLength),
        strokeWidth = 3.5f
    )
}

private fun DrawScope.drawSliceTrail(points: List<SliceTrailPoint>) {
    if (points.size < 2) return

    val now = System.nanoTime()
    val maxAgeNanos = 180_000_000L

    for (i in 0 until points.size - 1) {
        val p1 = points[i]
        val p2 = points[i + 1]

        val age = (now - p1.timestampNanos).coerceAtLeast(0L)
        val progress = 1.0f - (age.toFloat() / maxAgeNanos).coerceIn(0f, 1f)
        if (progress <= 0f) continue

        val outerWidth = 14f * progress
        val innerWidth = 5f * progress

        drawLine(
            color = Color(0xFFFFC857).copy(alpha = 0.5f * progress),
            start = Offset(p1.x, p1.y),
            end = Offset(p2.x, p2.y),
            strokeWidth = outerWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.White.copy(alpha = 0.95f * progress),
            start = Offset(p1.x, p1.y),
            end = Offset(p2.x, p2.y),
            strokeWidth = innerWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawFloatingPopup(popup: FloatingPopup, paint: Paint) {
    if (popup.alpha <= 0f) return

    paint.color = Color(popup.color).copy(alpha = popup.alpha).hashCode()
    paint.textSize = 28f * popup.scale

    drawContext.canvas.nativeCanvas.drawText(
        popup.text,
        popup.x,
        popup.y,
        paint
    )
}

// =========================================================================
// TOP HUD COMPOSABLE
// =========================================================================

@Composable
private fun TopHudBar(
    levelNumber: Int,
    score: Int,
    lives: Int,
    comboMultiplier: Int,
    elapsedSeconds: Float,
    isAudioMuted: Boolean,
    onToggleAudioMute: () -> Unit,
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Borderless floating HUD container over the full-screen scenery without any black bar or top strip
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Score and Combo Badge in floating translucent glass pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x44000000),
                    border = BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x4464B5F6))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "M$levelNumber",
                                color = Color(0xFF90CAF9),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.hud_score_label).uppercase(),
                                style = HudLabelStyle,
                                color = Color(0xCCFFFFFF)
                            )
                            Text(
                                text = String.format("%06d", score),
                                style = HudScoreDigitsStyle.copy(fontSize = 18.sp),
                                color = GoldSecondary
                            )
                        }
                    }
                }

                // Combo Badge on left beside score
                if (comboMultiplier > 1) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x55000000),
                        border = BorderStroke(1.dp, GoldSecondary)
                    ) {
                        Text(
                            text = "${comboMultiplier}X",
                            style = ComboBadgeStyle.copy(fontSize = 11.sp),
                            color = GoldSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Right side: [Time count-up] [Volume mute] [Pause/Stop] [Heart lives] in floating glass pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x44000000),
                border = BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Time (count-up elapsed time)
                    val secondsInt = elapsedSeconds.toInt().coerceAtLeast(0)
                    val minutes = secondsInt / 60
                    val secs = secondsInt % 60
                    val timeString = String.format("%02d:%02d", minutes, secs)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x33FFFFFF),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .height(26.dp)
                                .padding(horizontal = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color(0xFF90CAF9),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timeString,
                                style = TimerDigitsStyle.copy(
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    // 2. Volume Mute Toggle (Matching Main Menu size & style)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .border(
                                1.2.dp,
                                if (isAudioMuted) Color(0x88FF5252) else Color(0x884FCB8F),
                                CircleShape
                            )
                            .clickable(onClick = onToggleAudioMute)
                            .testTag("hud_mute_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAudioMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = if (isAudioMuted) Color(0xFFFF5252) else MintSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // 3. Pause / Stop toggle (Matching Main Menu size & style)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .border(1.2.dp, Color(0x8864B5F6), CircleShape)
                            .clickable(onClick = onTogglePause)
                            .testTag("hud_pause_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color(0xFF90CAF9),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // 4. Heart/lives display
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..4) {
                            val isHeartActive = i <= lives
                            val isBonusSlot = i == 4
                            if (isHeartActive || !isBonusSlot) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                !isHeartActive -> Color(0x33FFFFFF)
                                                isBonusSlot -> CyanEnergy
                                                else -> CrimsonDanger
                                            }
                                        )
                                        .then(
                                            if (isHeartActive) {
                                                Modifier.border(1.dp, Color(0x66FFFFFF), CircleShape)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isHeartActive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Life $i",
                                        tint = if (isHeartActive) Color.White else Color(0x55FFFFFF),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// CATEGORY PAINTERS CACHE
// =========================================================================

@Composable
private fun rememberCategoryPainters(): Map<ComplianceCategory, Painter> {
    val painters = mutableMapOf<ComplianceCategory, Painter>()
    for (category in ComplianceCategory.entries) {
        painters[category] = painterResource(id = category.iconRes)
    }
    return painters
}
