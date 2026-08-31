package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.GlossaryEntry
import com.example.data.GlossarySection

@Composable
fun IconGlossaryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEntry by remember { mutableStateOf<GlossaryEntry?>(null) }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D1B2A),
            Color(0xFF132238),
            Color(0xFF0A1118)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .testTag("glossary_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.glossary_title),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.glossary_subtitle),
                        color = Color(0xFF90CAF9),
                        fontSize = 12.sp
                    )
                }
            }

            // Glossary Sections
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlossarySection.entries.forEach { section ->
                    val sectionEntries = GlossaryEntry.ALL_ENTRIES.filter { it.section == section }

                    item(key = "header_${section.name}") {
                        SectionHeader(section = section)
                    }

                    items(sectionEntries, key = { it.category.name }) { entry ->
                        GlossaryCard(
                            entry = entry,
                            onSelect = { selectedEntry = entry }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Icon Detail Popup Dialog
        selectedEntry?.let { entry ->
            IconDetailDialog(
                entry = entry,
                onDismiss = { selectedEntry = null }
            )
        }
    }
}

@Composable
private fun SectionHeader(section: GlossarySection) {
    val (titleRes, badgeColor, subtext) = when (section) {
        GlossarySection.VIOLATIONS -> Triple(
            R.string.glossary_section_violations,
            Color(0xFFFFC857),
            "SLICE THESE: +10 pts × combo multiplier"
        )
        GlossarySection.LEGITIMATE -> Triple(
            R.string.glossary_section_legitimate,
            Color(0xFF64B5F6),
            "DO NOT SLICE: -1 Life penalty & resets combo"
        )
        GlossarySection.TRAPS -> Triple(
            R.string.glossary_section_traps,
            Color(0xFFFF7043),
            "AVOID SLICING: -10 pts & resets combo (NO life lost)"
        )
        GlossarySection.BONUS -> Triple(
            R.string.glossary_section_bonus,
            Color(0xFF00E5FF),
            "COLLECT: Restores +1 Life & grants +25 pts"
        )
    }

    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(titleRes),
                color = badgeColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
            )
        }
        Text(
            text = subtext,
            color = Color(0xFFB0BEC5),
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

@Composable
private fun GlossaryCard(
    entry: GlossaryEntry,
    onSelect: () -> Unit
) {
    val badgeColor = when (entry.section) {
        GlossarySection.VIOLATIONS -> Color(0xFFFFC857)
        GlossarySection.LEGITIMATE -> Color(0xFF64B5F6)
        GlossarySection.TRAPS -> Color(0xFFFF7043)
        GlossarySection.BONUS -> Color(0xFF00E5FF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
            .testTag("glossary_item_${entry.category.name}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xAA132238)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon in circle container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(entry.category.glowColor).copy(alpha = 0.22f))
                    .border(1.5.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = entry.category.iconRes),
                    contentDescription = stringResource(entry.category.displayNameRes),
                    tint = badgeColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(entry.category.displayNameRes),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Rule badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (entry.section) {
                                GlossarySection.VIOLATIONS -> "+10 PTS"
                                GlossarySection.LEGITIMATE -> "-1 LIFE"
                                GlossarySection.TRAPS -> "-10 PTS (TRAP)"
                                GlossarySection.BONUS -> "+1 LIFE"
                            },
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(entry.explanationRes),
                    color = Color(0xFFCFD8DC),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Tap to view details",
                tint = Color(0x6690CAF9),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun IconDetailDialog(
    entry: GlossaryEntry,
    onDismiss: () -> Unit
) {
    val badgeColor = when (entry.section) {
        GlossarySection.VIOLATIONS -> Color(0xFFFFC857)
        GlossarySection.LEGITIMATE -> Color(0xFF64B5F6)
        GlossarySection.TRAPS -> Color(0xFFFF7043)
        GlossarySection.BONUS -> Color(0xFF00E5FF)
    }

    val actionText = when (entry.section) {
        GlossarySection.VIOLATIONS -> stringResource(R.string.glossary_action_slice)
        GlossarySection.LEGITIMATE -> stringResource(R.string.glossary_action_avoid)
        GlossarySection.TRAPS -> stringResource(R.string.glossary_action_trap)
        GlossarySection.BONUS -> stringResource(R.string.glossary_action_collect)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, badgeColor, RoundedCornerShape(20.dp))
                .testTag("glossary_detail_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Icon Circle with Ambient Glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(entry.category.glowColor).copy(alpha = 0.45f),
                                    Color(0x11000000)
                                )
                            )
                        )
                        .border(2.dp, badgeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = entry.category.iconRes),
                        contentDescription = stringResource(entry.category.displayNameRes),
                        tint = badgeColor,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon Name
                Text(
                    text = stringResource(entry.category.displayNameRes),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Gameplay Action Badge (SLICE / AVOID / COLLECT)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.25f))
                        .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = actionText,
                        color = badgeColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Section Name
                Text(
                    text = stringResource(entry.section.titleRes).uppercase(),
                    color = Color(0xFF90CAF9),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Real-World Compliance Explanation
                Text(
                    text = stringResource(entry.explanationRes),
                    color = Color(0xFFECEFF1),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // "Got it" / "Tutup" Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("glossary_dialog_dismiss_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = badgeColor,
                        contentColor = Color(0xFF0D1B2A)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.glossary_dialog_got_it),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

