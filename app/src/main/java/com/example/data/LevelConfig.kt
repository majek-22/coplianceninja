package com.example.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.R

enum class GameDifficulty(
    val id: String,
    @get:StringRes val labelRes: Int,
    val scoreMultiplier: Float,
    val speedMultiplier: Float,
    val spawnIntervalMultiplier: Float
) {
    NORMAL(
        id = "normal",
        labelRes = R.string.difficulty_normal,
        scoreMultiplier = 1.0f,
        speedMultiplier = 1.0f,
        spawnIntervalMultiplier = 1.0f
    ),
    HARD(
        id = "hard",
        labelRes = R.string.difficulty_hard,
        scoreMultiplier = 1.25f,
        speedMultiplier = 1.15f,
        spawnIntervalMultiplier = 0.85f
    )
}

data class LevelConfig(
    val levelNumber: Int,
    @get:StringRes val nameRes: Int,
    @get:StringRes val descRes: Int,
    @get:DrawableRes val iconRes: Int,
    val durationSeconds: Int,
    val oneStarScore: Int,
    val twoStarsScore: Int,
    val threeStarsScore: Int,
    val allowedViolations: List<ComplianceCategory>,
    val allowedLegitimate: List<ComplianceCategory>,
    val allowedTraps: List<ComplianceCategory>,
    val baseSpawnInterval: Float
) {
    fun calculateStars(score: Int): Int {
        return when {
            score >= threeStarsScore -> 3
            score >= twoStarsScore -> 2
            score >= oneStarScore -> 1
            else -> 0
        }
    }

    companion object {
        val ALL_LEVELS: List<LevelConfig> = listOf(
            LevelConfig(
                levelNumber = 1,
                nameRes = R.string.level1_name,
                descRes = R.string.level1_desc,
                iconRes = R.drawable.ic_item_verified_approval,
                durationSeconds = 60,
                oneStarScore = 80,
                twoStarsScore = 180,
                threeStarsScore = 300,
                allowedViolations = listOf(
                    ComplianceCategory.BRIBERY,
                    ComplianceCategory.FRAUD
                ),
                allowedLegitimate = listOf(
                    ComplianceCategory.OFFICIAL_DOCUMENT,
                    ComplianceCategory.VERIFIED_APPROVAL
                ),
                allowedTraps = listOf(
                    ComplianceCategory.FALSE_ALARM,
                    ComplianceCategory.HONEST_MISTAKE
                ),
                baseSpawnInterval = 1.5f
            ),
            LevelConfig(
                levelNumber = 2,
                nameRes = R.string.level2_name,
                descRes = R.string.level2_desc,
                iconRes = R.drawable.ic_item_fraud,
                durationSeconds = 65,
                oneStarScore = 120,
                twoStarsScore = 250,
                threeStarsScore = 450,
                allowedViolations = listOf(
                    ComplianceCategory.BRIBERY,
                    ComplianceCategory.FRAUD,
                    ComplianceCategory.MONEY_LAUNDERING
                ),
                allowedLegitimate = listOf(
                    ComplianceCategory.OFFICIAL_DOCUMENT,
                    ComplianceCategory.VERIFIED_APPROVAL,
                    ComplianceCategory.VALID_PARTNERSHIP
                ),
                allowedTraps = listOf(
                    ComplianceCategory.FALSE_ALARM
                ),
                baseSpawnInterval = 1.4f
            ),
            LevelConfig(
                levelNumber = 3,
                nameRes = R.string.level3_name,
                descRes = R.string.level3_desc,
                iconRes = R.drawable.ic_item_data_breach,
                durationSeconds = 70,
                oneStarScore = 160,
                twoStarsScore = 350,
                threeStarsScore = 600,
                allowedViolations = listOf(
                    ComplianceCategory.BRIBERY,
                    ComplianceCategory.FRAUD,
                    ComplianceCategory.MONEY_LAUNDERING,
                    ComplianceCategory.DATA_BREACH
                ),
                allowedLegitimate = listOf(
                    ComplianceCategory.OFFICIAL_DOCUMENT,
                    ComplianceCategory.VERIFIED_APPROVAL,
                    ComplianceCategory.VALID_PARTNERSHIP,
                    ComplianceCategory.CERTIFICATION,
                    ComplianceCategory.VERIFIED_INVOICE
                ),
                allowedTraps = listOf(
                    ComplianceCategory.FALSE_ALARM,
                    ComplianceCategory.UNVERIFIED_RUMOR,
                    ComplianceCategory.HONEST_MISTAKE
                ),
                baseSpawnInterval = 1.3f
            ),
            LevelConfig(
                levelNumber = 4,
                nameRes = R.string.level4_name,
                descRes = R.string.level4_desc,
                iconRes = R.drawable.ic_item_money_laundering,
                durationSeconds = 75,
                oneStarScore = 220,
                twoStarsScore = 450,
                threeStarsScore = 750,
                allowedViolations = ComplianceCategory.ALL_VIOLATIONS,
                allowedLegitimate = ComplianceCategory.LEGITIMATE,
                allowedTraps = ComplianceCategory.TRAPS,
                baseSpawnInterval = 1.2f
            )
        )

        fun getByLevel(level: Int): LevelConfig {
            return ALL_LEVELS.find { it.levelNumber == level } ?: ALL_LEVELS.first()
        }
    }
}
