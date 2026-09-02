package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ComplianceCategory
import com.example.data.GameDifficulty
import com.example.data.LevelConfig
import com.example.data.SlicedCategoryRecord
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundGradientEnd
import com.example.ui.theme.CoralPrimary
import com.example.ui.theme.DragHandleColor
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassWhite05
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.HudLabelStyle
import com.example.ui.theme.HudScoreDigitsStyle
import com.example.ui.theme.SheetBorderTop
import com.example.ui.theme.SheetContainerBg
import com.example.ui.theme.ShiftCompleteTitleStyle
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ResultScreen(
    level: LevelConfig,
    difficulty: GameDifficulty,
    score: Int,
    stars: Int,
    highScore: Int,
    isNewHighScore: Boolean,
    rankRes: Int,
    trapsAvoided: Int,
    trapsSliced: Int,
    slicedSummary: List<SlicedCategoryRecord>,
    elapsedSeconds: Float = 0f,
    currentLanguage: String = "en",
    onToggleLanguage: () -> Unit = {},
    onPlayAgain: () -> Unit,
    onSelectLevel: () -> Unit,
    onReturnToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    DisposableEffect(window) {
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            onDispose { }
        } else {
            onDispose { }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fullscreen Background Artwork
        Image(
            painter = painterResource(id = R.drawable.bg_gameplay_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Semi-transparent overlay to ensure contrast and readability while showcasing background art
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xC50B132B),
            border = BorderStroke(1.dp, Color(0x4064B5F6)),
            shadowElevation = 16.dp
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                val isLandscape = maxWidth > maxHeight
                val isIndonesian = currentLanguage == "in" || currentLanguage == "id"
                val secondsInt = elapsedSeconds.toInt().coerceAtLeast(0)
                val minutes = secondsInt / 60
                val secs = secondsInt % 60
                val timeSurvivedFormatted = String.format("%02d:%02d", minutes, secs)

                val rankName = when (rankRes) {
                    R.string.rank_intern -> if (isIndonesian) "Magang Kepatuhan" else "Compliance Intern"
                    R.string.rank_auditor -> if (isIndonesian) "Auditor Muda" else "Junior Auditor"
                    R.string.rank_officer -> if (isIndonesian) "Petugas Kepatuhan" else "Compliance Officer"
                    R.string.rank_risk_lead -> if (isIndonesian) "Ketua Manajemen Risiko" else "Senior Risk Lead"
                    R.string.rank_director -> if (isIndonesian) "Direktur Kepatuhan Utama" else "Chief Compliance Director"
                    else -> stringResource(rankRes)
                }
                val levelName = when (level.levelNumber) {
                    1 -> if (isIndonesian) "PENGENDALIAN INTERNAL" else "INTERNAL CONTROLS"
                    2 -> if (isIndonesian) "KLAIM & FRAUD" else "CLAIMS & FRAUD"
                    3 -> if (isIndonesian) "DATA & PRIVASI" else "DATA & PRIVACY"
                    4 -> if (isIndonesian) "KEJAHATAN FINANSIAL" else "FINANCIAL CRIMES"
                    else -> stringResource(level.nameRes).uppercase()
                }

                if (isLandscape) {
                    // Landscape two-column layout: Left = Summary & Actions, Right = Neutralized Items
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Report, Score, Traps, Action buttons
                        Column(
                            modifier = Modifier
                                .weight(1.1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                // Mission Title Badge & Stars
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0x3364B5F6),
                                        border = BorderStroke(1.dp, Color(0x6664B5F6))
                                    ) {
                                        Text(
                                            text = levelName,
                                            color = Color(0xFF90CAF9),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Survival Time Badge
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0x3300E5FF),
                                            border = BorderStroke(1.dp, Color(0x6600E5FF))
                                        ) {
                                            Text(
                                                text = if (isIndonesian) "BERTAHAN: $timeSurvivedFormatted" else "SURVIVED: $timeSurvivedFormatted",
                                                color = Color(0xFF80D8FF),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        // Stars
                                        Row {
                                            for (i in 1..3) {
                                                val earned = i <= stars
                                                Icon(
                                                    imageVector = if (earned) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    tint = if (earned) Color(0xFFFFD54F) else Color(0x44FFFFFF),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Title & Score Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isIndonesian) "LAPORAN TUGAS" else "SHIFT REPORT",
                                            style = ShiftCompleteTitleStyle.copy(fontSize = 19.sp)
                                        )
                                        Text(
                                            text = "${if (isIndonesian) "PANGKAT PETUGAS" else "OFFICER RANK"}: $rankName",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp
                                            ),
                                            color = TextSecondary
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (isNewHighScore) {
                                                if (isIndonesian) "REKOR SKOR BARU!" else "NEW HIGH SCORE!"
                                            } else {
                                                if (isIndonesian) "SKOR AKHIR" else "FINAL SCORE"
                                            },
                                            style = HudLabelStyle.copy(
                                                color = GoldSecondary,
                                                fontSize = 9.sp
                                            )
                                        )
                                        Text(
                                            text = String.format("%,d", score),
                                            style = HudScoreDigitsStyle.copy(
                                                fontSize = 24.sp,
                                                color = GoldSecondary
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Trap Recognition Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF7043)),
                                    border = BorderStroke(1.dp, Color(0x55FF7043))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (isIndonesian) "Berhasil Dihindari: $trapsAvoided" else "Correctly Avoided: $trapsAvoided",
                                            color = Color(0xFF4FCB8F),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isIndonesian) "Keliru Ditebas: $trapsSliced (-10 poin)" else "Mistakenly Sliced: $trapsSliced (-10 pts)",
                                            color = if (trapsSliced > 0) Color(0xFFFF5252) else Color(0xFFB0BEC5),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Action buttons
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 1. Play Again
                                Button(
                                    onClick = onPlayAgain,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("play_again_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(listOf(CoralPrimary, GoldSecondary)),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isIndonesian) "MAIN LAGI" else "PLAY AGAIN",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BackgroundDark
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = BackgroundDark,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // 2. Mission Select
                                    OutlinedButton(
                                        onClick = onSelectLevel,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("select_level_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFF64B5F6)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = GlassWhite05,
                                            contentColor = Color(0xFF90CAF9)
                                        )
                                    ) {
                                        Icon(Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isIndonesian) "PILIH MISI" else "SELECT MISSION",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // 3. Main Menu
                                    OutlinedButton(
                                        onClick = onReturnToMenu,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("main_menu_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, GlassBorderLight),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = GlassWhite05,
                                            contentColor = TextPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.Home, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isIndonesian) "MENU UTAMA" else "MAIN MENU",
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Right Column: Violations Neutralized Debrief list
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isIndonesian) "PELANGGARAN DINETRALISIR" else "VIOLATIONS NEUTRALIZED",
                                    style = HudLabelStyle.copy(letterSpacing = 1.2.sp, fontSize = 11.sp),
                                    color = GoldSecondary
                                )

                                // Circular Language Toggle (EN / ID) matching Main Menu
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable(onClick = onToggleLanguage)
                                        .testTag("result_lang_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x44000000))
                                            .border(1.2.dp, GoldSecondary.copy(alpha = 0.7f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Crossfade(
                                            targetState = currentLanguage,
                                            animationSpec = tween(250),
                                            label = "result_language_toggle_crossfade"
                                        ) { lang ->
                                            Text(
                                                text = if (lang == "in" || lang == "id") "ID" else "EN",
                                                color = GoldSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (slicedSummary.isNotEmpty()) {
                                    items(slicedSummary, key = { it.category.id }) { record ->
                                        DebriefViolationCard(
                                            category = record.category,
                                            count = record.count,
                                            currentLanguage = currentLanguage
                                        )
                                    }
                                } else {
                                    item {
                                        Text(
                                            text = if (isIndonesian)
                                                "Tidak ada pelanggaran yang ditebas ronde ini. Pelajari kategori dan tetap waspada!"
                                            else
                                                "No violations sliced this round. Study the categories below and stay sharp!",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                    items(ComplianceCategory.VIOLATIONS, key = { it.id }) { cat ->
                                        DebriefViolationCard(
                                            category = cat,
                                            count = 0,
                                            currentLanguage = currentLanguage
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Portrait fallback
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = levelName,
                                color = Color(0xFF90CAF9),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0x3300E5FF),
                                border = BorderStroke(1.dp, Color(0x6600E5FF))
                            ) {
                                Text(
                                    text = if (isIndonesian) "BERTAHAN: $timeSurvivedFormatted" else "SURVIVED: $timeSurvivedFormatted",
                                    color = Color(0xFF80D8FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%,d", score),
                                style = HudScoreDigitsStyle.copy(fontSize = 28.sp, color = GoldSecondary)
                            )
                        }

                        if (slicedSummary.isNotEmpty()) {
                            items(slicedSummary, key = { it.category.id }) { record ->
                                DebriefViolationCard(
                                    category = record.category,
                                    count = record.count,
                                    currentLanguage = currentLanguage
                                )
                            }
                        }

                        item {
                            Button(
                                onClick = onPlayAgain,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("play_again_button")
                            ) {
                                Text(text = if (isIndonesian) "MAIN LAGI" else "PLAY AGAIN")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebriefViolationCard(
    category: ComplianceCategory,
    count: Int,
    currentLanguage: String = "en"
) {
    val isId = currentLanguage == "in" || currentLanguage == "id"
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

    val displayName = category.getDisplayName(currentLanguage)
    val explanation = category.getExplanation(currentLanguage)
    val neutralizedSuffix = if (isId) "dinetralisir" else "neutralized"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GlassWhite05),
        border = BorderStroke(1.dp, GlassBorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(discBgColor)
                    .border(1.5.dp, categoryColor, CircleShape)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = displayName,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    if (count > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "×$count $neutralizedSuffix",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = GoldSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
