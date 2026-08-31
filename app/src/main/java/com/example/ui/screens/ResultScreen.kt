package com.example.ui.screens

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
    onPlayAgain: () -> Unit,
    onSelectLevel: () -> Unit,
    onReturnToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundGradientEnd, Color(0xFF150A28))
                )
            )
    ) {
        // Dimmed overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp, bottom = 10.dp),
            shape = RoundedCornerShape(24.dp),
            color = SheetContainerBg,
            border = BorderStroke(1.dp, SheetBorderTop),
            shadowElevation = 24.dp
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                val isLandscape = maxWidth > maxHeight
                val secondsInt = elapsedSeconds.toInt().coerceAtLeast(0)
                val minutes = secondsInt / 60
                val secs = secondsInt % 60
                val timeSurvivedFormatted = String.format("%02d:%02d", minutes, secs)

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
                                            text = stringResource(level.nameRes).uppercase(),
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
                                                text = stringResource(R.string.result_time_survived, timeSurvivedFormatted),
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
                                            text = stringResource(R.string.result_title).uppercase(),
                                            style = ShiftCompleteTitleStyle.copy(fontSize = 19.sp)
                                        )
                                        Text(
                                            text = "${stringResource(R.string.result_rank_prefix)}: ${stringResource(rankRes)}",
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
                                                stringResource(R.string.result_new_high_score).uppercase()
                                            } else {
                                                stringResource(R.string.result_final_score_label).uppercase()
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
                                            text = stringResource(R.string.result_traps_avoided_label, trapsAvoided),
                                            color = Color(0xFF4FCB8F),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(R.string.result_traps_sliced_label, trapsSliced),
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
                                                text = stringResource(R.string.result_play_again).uppercase(),
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
                                            text = stringResource(R.string.level_select_title).uppercase(),
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
                                            text = stringResource(R.string.result_main_menu),
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
                            Text(
                                text = stringResource(R.string.result_recap_heading).uppercase(),
                                style = HudLabelStyle.copy(letterSpacing = 1.2.sp, fontSize = 11.sp),
                                color = GoldSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (slicedSummary.isNotEmpty()) {
                                    items(slicedSummary, key = { it.category.id }) { record ->
                                        DebriefViolationCard(category = record.category, count = record.count)
                                    }
                                } else {
                                    item {
                                        Text(
                                            text = stringResource(R.string.result_recap_empty),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                    items(ComplianceCategory.VIOLATIONS, key = { it.id }) { cat ->
                                        DebriefViolationCard(category = cat, count = 0)
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
                                text = stringResource(level.nameRes).uppercase(),
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
                                    text = stringResource(R.string.result_time_survived, timeSurvivedFormatted),
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
                                DebriefViolationCard(category = record.category, count = record.count)
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
                                Text(text = stringResource(R.string.result_play_again).uppercase())
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
    count: Int
) {
    val borderColor = Color(category.borderColor)
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(borderColor.copy(alpha = 0.2f))
                    .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = stringResource(category.displayNameRes),
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
                        text = stringResource(category.displayNameRes),
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
                                text = "×$count ${stringResource(R.string.result_neutralized_suffix)}",
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
                    text = stringResource(category.explanationRes),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
