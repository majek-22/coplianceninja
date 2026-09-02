package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.LevelConfig
import com.example.data.local.UserStats
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundGradientEnd
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.MintSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun LevelSelectScreen(
    userStats: UserStats?,
    onStartLevel: (LevelConfig) -> Unit,
    onOpenGlossary: () -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLevelForBriefing by remember { mutableStateOf<LevelConfig?>(null) }
    var lockedFeedbackMessage by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }
    val sliceTrail = remember { mutableStateListOf<Offset>() }
    val scope = rememberCoroutineScope()

    val cardBounds = remember { mutableMapOf<Int, Rect>() }
    var hasTriggeredSlice by remember { mutableStateOf(false) }

    fun triggerShakeFeedback(message: String) {
        lockedFeedbackMessage = message
        scope.launch {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(12f, animationSpec = tween(50))
            shakeOffset.animateTo(-12f, animationSpec = tween(50))
            shakeOffset.animateTo(8f, animationSpec = tween(50))
            shakeOffset.animateTo(-8f, animationSpec = tween(50))
            shakeOffset.animateTo(0f, animationSpec = tween(50))
            delay(1500)
            lockedFeedbackMessage = null
        }
    }

    fun checkSliceHit(p1: Offset, p2: Offset) {
        if (hasTriggeredSlice) return
        val length = hypot(p2.x - p1.x, p2.y - p1.y)
        if (length < 35f) return

        for ((levelNum, rect) in cardBounds) {
            if (lineIntersectsRect(p1, p2, rect)) {
                val lvl = LevelConfig.getByLevel(levelNum)
                val isUnlocked = userStats?.isLevelUnlocked(levelNum) ?: (levelNum == 1)
                if (isUnlocked) {
                    hasTriggeredSlice = true
                    scope.launch {
                        delay(100)
                        onStartLevel(lvl)
                    }
                } else {
                    val prevLevel = levelNum - 1
                    triggerShakeFeedback(context.getString(R.string.level_unlock_requirement, prevLevel))
                }
                return
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        sliceTrail.clear()
                        sliceTrail.add(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val currentPos = change.position
                        if (sliceTrail.isNotEmpty()) {
                            val lastPos = sliceTrail.last()
                            checkSliceHit(lastPos, currentPos)
                        }
                        sliceTrail.add(currentPos)
                        if (sliceTrail.size > 14) {
                            sliceTrail.removeAt(0)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            delay(100)
                            sliceTrail.clear()
                        }
                    },
                    onDragCancel = {
                        sliceTrail.clear()
                    }
                )
            }
    ) {
        val isTabletLandscape = maxWidth > 600.dp

        // Fullscreen scenic background artwork
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Dark gradient overlay for crystal clear contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC0D1B2A),
                            Color(0xEE141E30),
                            Color(0xF80F172A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackToMenu,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .border(1.dp, Color(0x3364B5F6), CircleShape)
                            .testTag("level_select_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.level_select_title),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Gameplay Rules / Glossary shortcut button
                IconButton(
                    onClick = onOpenGlossary,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFC857))
                        .border(1.dp, GoldSecondary.copy(alpha = 0.5f), CircleShape)
                        .testTag("level_select_help_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = stringResource(R.string.menu_glossary),
                        tint = GoldSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Lock/Unlock feedback toast banner
            if (lockedFeedbackMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xDDFF5252)
                ) {
                    Text(
                        text = lockedFeedbackMessage ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Missions Grid (2 columns on landscape/tablet, 1 or 2 on phone)
            val columns = if (isTabletLandscape) 2 else 1

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(LevelConfig.ALL_LEVELS, key = { it.levelNumber }) { level ->
                    val isUnlocked = userStats?.isLevelUnlocked(level.levelNumber) ?: (level.levelNumber == 1)
                    val bestScore = userStats?.getBestForLevel(level.levelNumber) ?: 0
                    val stars = userStats?.getStarsForLevel(level.levelNumber) ?: 0

                    ProfessionalMissionCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        bestScore = bestScore,
                        stars = stars,
                        onTapInfo = { selectedLevelForBriefing = level },
                        modifier = Modifier.onGloballyPositioned { coords ->
                            cardBounds[level.levelNumber] = coords.boundsInRoot()
                        }
                    )
                }
            }
        }

        // Draw Interactive Slice Blade Trail
        if (sliceTrail.size >= 2) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(sliceTrail.first().x, sliceTrail.first().y)
                    for (i in 1 until sliceTrail.size) {
                        lineTo(sliceTrail[i].x, sliceTrail[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = CoralPrimary.copy(alpha = 0.5f),
                    style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }

        // Mission Briefing Dialog (opened by tapping info or card)
        selectedLevelForBriefing?.let { level ->
            MissionBriefingDialog(
                level = level,
                userStats = userStats,
                onDismiss = { selectedLevelForBriefing = null },
                onStart = {
                    selectedLevelForBriefing = null
                    onStartLevel(level)
                }
            )
        }
    }
}

@Composable
private fun ProfessionalMissionCard(
    level: LevelConfig,
    isUnlocked: Boolean,
    bestScore: Int,
    stars: Int,
    onTapInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtitleRes = when (level.levelNumber) {
        1 -> R.string.level1_subtitle
        2 -> R.string.level2_subtitle
        3 -> R.string.level3_subtitle
        4 -> R.string.level4_subtitle
        else -> R.string.level1_subtitle
    }

    val cardBg = if (isUnlocked) Color(0xCC182338) else Color(0x88121927)
    val borderColor = if (isUnlocked) Color(0x3364B5F6) else Color(0x22FFFFFF)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onTapInfo() }
            .testTag("mission_card_${level.levelNumber}"),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 3.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Themed Mission Image Badge with rich illustrated asset
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isUnlocked) Brush.radialGradient(
                            listOf(Color(0x3364B5F6), Color(0x180D1B2A))
                        ) else Brush.radialGradient(
                            listOf(Color(0x15FFFFFF), Color(0x11000000))
                        )
                    )
                    .border(
                        1.2.dp,
                        if (isUnlocked) Color(0x5564B5F6) else Color(0x22FFFFFF),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = level.iconRes),
                    contentDescription = stringResource(level.nameRes),
                    modifier = Modifier
                        .size(44.dp)
                        .alpha(if (isUnlocked) 1f else 0.35f),
                    contentScale = ContentScale.Fit
                )
                if (!isUnlocked) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xDD101827))
                            .border(1.dp, Color(0x55FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color(0xFFFFC857),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(level.nameRes),
                        color = if (isUnlocked) TextPrimary else Color(0xFF90A4AE),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Status Badge
                    if (isUnlocked) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x224FCB8F)
                        ) {
                            Text(
                                text = stringResource(R.string.level_status_active),
                                color = MintSuccess,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x33FFFFFF)
                        ) {
                            Text(
                                text = stringResource(R.string.level_locked_badge),
                                color = Color(0xFFB0BEC5),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Subtitle
                Text(
                    text = stringResource(subtitleRes),
                    color = if (isUnlocked) Color(0xFFB0BEC5) else Color(0xFF607D8B),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stars & Best Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        for (i in 1..3) {
                            val earned = i <= stars
                            Icon(
                                imageVector = if (earned) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (earned && isUnlocked) GoldSecondary else Color(0x33FFFFFF),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    if (isUnlocked && bestScore > 0) {
                        Text(
                            text = stringResource(R.string.level_best_score, bestScore),
                            color = GoldSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (!isUnlocked) {
                        Text(
                            text = stringResource(R.string.level_unlock_short, level.levelNumber - 1),
                            color = Color(0xFF78909C),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionBriefingDialog(
    level: LevelConfig,
    userStats: UserStats?,
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    val isUnlocked = userStats?.isLevelUnlocked(level.levelNumber) ?: (level.levelNumber == 1)
    val bestScore = userStats?.getBestForLevel(level.levelNumber) ?: 0
    val stars = userStats?.getStarsForLevel(level.levelNumber) ?: 0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(1.5.dp, Color(0x4464B5F6), RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141E30)),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mission Environment Preview Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.2.dp, Color(0x5564B5F6), RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = level.backgroundRes),
                        contentDescription = stringResource(level.nameRes),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xBB101827))
                                )
                            )
                    )
                    // Level badge chip in top-right
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xCC0D1B2A)
                    ) {
                        Text(
                            text = "TIER ${level.levelNumber}",
                            color = GoldSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(level.nameRes),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(level.descRes),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats & Duration bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.briefing_mode_label), color = TextSecondary, fontSize = 11.sp)
                        Text(text = stringResource(R.string.briefing_mode_val), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.briefing_best_score), color = TextSecondary, fontSize = 11.sp)
                        Text(text = "$bestScore", color = GoldSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.briefing_stars), color = TextSecondary, fontSize = 11.sp)
                        Text(text = "$stars / 3 ★", color = MintSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isUnlocked) {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("dialog_deploy_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.menu_start_shift),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text = stringResource(R.string.level_unlock_requirement, level.levelNumber - 1),
                            color = Color(0xFFB0BEC5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun lineIntersectsRect(p1: Offset, p2: Offset, rect: Rect): Boolean {
    if (rect.contains(p1) || rect.contains(p2)) return true
    val minX = minOf(p1.x, p2.x)
    val maxX = maxOf(p1.x, p2.x)
    val minY = minOf(p1.y, p2.y)
    val maxY = maxOf(p1.y, p2.y)

    if (maxX < rect.left || minX > rect.right || maxY < rect.top || minY > rect.bottom) {
        return false
    }

    return lineIntersectsLine(p1, p2, Offset(rect.left, rect.top), Offset(rect.right, rect.top)) ||
            lineIntersectsLine(p1, p2, Offset(rect.left, rect.bottom), Offset(rect.right, rect.bottom)) ||
            lineIntersectsLine(p1, p2, Offset(rect.left, rect.top), Offset(rect.left, rect.bottom)) ||
            lineIntersectsLine(p1, p2, Offset(rect.right, rect.top), Offset(rect.right, rect.bottom))
}

private fun lineIntersectsLine(a1: Offset, a2: Offset, b1: Offset, b2: Offset): Boolean {
    val d = (a2.x - a1.x) * (b2.y - b1.y) - (a2.y - a1.y) * (b2.x - b1.x)
    if (d == 0f) return false
    val u = ((b1.x - a1.x) * (b2.y - b1.y) - (b1.y - a1.y) * (b2.x - b1.x)) / d
    val v = ((b1.x - a1.x) * (a2.y - a1.y) - (b1.y - a1.y) * (a2.x - a1.x)) / d
    return (u in 0f..1f) && (v in 0f..1f)
}
