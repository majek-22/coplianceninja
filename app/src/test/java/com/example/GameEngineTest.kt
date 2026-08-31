package com.example

import com.example.data.ComplianceCategory
import com.example.engine.GameEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameEngineTest {

    private lateinit var engine: GameEngine

    @Before
    fun setUp() {
        engine = GameEngine(screenWidth = 1080f, screenHeight = 1920f)
        engine.resetGame()
    }

    @Test
    fun testSliceViolationIncreasesScoreAndCombo() {
        val item = engine.spawnItemManually(
            category = ComplianceCategory.BRIBERY,
            x = 500f,
            y = 500f,
            radius = 58f
        )

        assertFalse("Item should start unsliced", item.sliced)

        val hits = engine.processSliceSegment(400f, 500f, 600f, 500f)

        assertEquals("Should register 1 slice hit", 1, hits)
        assertTrue("Item should be sliced", item.sliced)
        assertEquals("Bribery base score is 10", 10, engine.score)
        assertEquals("Combo streak should be 1", 1, engine.comboStreak)
        assertEquals("Combo multiplier should be 1x for 1st hit", 1, engine.comboMultiplier)
    }

    @Test
    fun testConsecutiveViolationSlicesIncreaseComboMultiplier() {
        for (i in 1..10) {
            val item = engine.spawnItemManually(
                category = ComplianceCategory.FRAUD,
                x = 100f * i,
                y = 500f,
                radius = 58f
            )
            engine.processSliceSegment(item.x - 40f, 500f, item.x + 40f, 500f)
        }

        assertEquals("Combo streak should be 10", 10, engine.comboStreak)
        assertEquals("Combo multiplier should reach max 4x", 4, engine.comboMultiplier)
    }

    @Test
    fun testWrongSliceDeductsLifeAndResetsCombo() {
        for (i in 1..3) {
            val v = engine.spawnItemManually(ComplianceCategory.DATA_BREACH, 100f * i, 300f)
            engine.processSliceSegment(v.x - 30f, 300f, v.x + 30f, 300f)
        }
        assertEquals("Combo streak should be 3", 3, engine.comboStreak)
        assertEquals("Multiplier should be 2x", 2, engine.comboMultiplier)

        val legitItem = engine.spawnItemManually(ComplianceCategory.OFFICIAL_DOCUMENT, 500f, 500f)
        val hits = engine.processSliceSegment(450f, 500f, 550f, 500f)

        assertEquals("Should register 1 slice hit", 1, hits)
        assertTrue("Legitimate item marked sliced", legitItem.sliced)
        assertEquals("Lives should be reduced by 1 (3 -> 2)", 2, engine.lives)
        assertEquals("Combo streak must reset to 0 on mistake", 0, engine.comboStreak)
        assertEquals("Multiplier must reset to 1x on mistake", 1, engine.comboMultiplier)
    }

    @Test
    fun testTrapSliceDeductsTenPointsAndResetsComboWithoutLifeLoss() {
        val initialLives = engine.lives
        // Give some initial score
        val v = engine.spawnItemManually(ComplianceCategory.BRIBERY, 300f, 300f)
        engine.processSliceSegment(250f, 300f, 350f, 300f)
        val scoreBeforeTrap = engine.score
        assertEquals("Should have 10 points", 10, scoreBeforeTrap)
        assertEquals("Combo streak should be 1", 1, engine.comboStreak)

        // Spawn a trap item (False Alarm)
        val trap = engine.spawnItemManually(ComplianceCategory.FALSE_ALARM, 500f, 500f)
        val hits = engine.processSliceSegment(450f, 500f, 550f, 500f)

        assertEquals("Should register 1 hit on trap", 1, hits)
        assertTrue("Trap should be marked sliced", trap.sliced)
        assertEquals("Score should be deducted by 10 points (10 -> 0)", 0, engine.score)
        assertEquals("Traps sliced count should be 1", 1, engine.trapsSlicedCount)
        assertEquals("Lives should NOT be deducted on trap slice!", initialLives, engine.lives)
        assertEquals("Combo streak must reset to 0 on trap slice", 0, engine.comboStreak)
        assertEquals("Multiplier must reset to 1x on trap slice", 1, engine.comboMultiplier)
    }

    @Test
    fun testTrapFallingOffScreenAvoidedWithoutLifePenalty() {
        val initialLives = engine.lives

        // Spawn trap falling off bottom
        engine.spawnItemManually(
            category = ComplianceCategory.UNVERIFIED_RUMOR,
            x = 400f,
            y = 2000f,
            vy = 800f
        )

        engine.update(0.10f)

        assertEquals("No life loss when trap is safely avoided", initialLives, engine.lives)
        assertEquals("Traps avoided count should be 1", 1, engine.trapsAvoidedCount)
    }

    @Test
    fun testScoreDifficultyTierProgression() {
        assertEquals("Tier 0 for 0-99 points", 0, engine.getScoreDifficultyTier(0).tier)
        assertEquals("Tier 0 for 99 points", 0, engine.getScoreDifficultyTier(99).tier)
        assertEquals("Tier 1 for 100 points", 1, engine.getScoreDifficultyTier(100).tier)
        assertEquals("Tier 2 for 300 points", 2, engine.getScoreDifficultyTier(300).tier)
        assertEquals("Tier 3 for 600 points", 3, engine.getScoreDifficultyTier(600).tier)
        assertEquals("Tier 4 for 1000 points", 4, engine.getScoreDifficultyTier(1000).tier)
        assertEquals("Tier 4 for 2500 points", 4, engine.getScoreDifficultyTier(2500).tier)

        val tier4 = engine.getScoreDifficultyTier(1200)
        assertEquals("Tier 4 has 35% speed boost", 0.35f, tier4.speedIncreasePercent, 0.001f)
        assertEquals("Tier 4 has 50% interval reduction", 0.50f, tier4.spawnIntervalReductionPercent, 0.001f)
    }

    @Test
    fun testMissedViolationDeductsLifeAndResetsCombo() {
        val initialLives = engine.lives

        engine.spawnItemManually(
            category = ComplianceCategory.MONEY_LAUNDERING,
            x = 500f,
            y = 2000f,
            vy = 800f
        )

        engine.update(0.10f)

        assertEquals("Missing a violation must cost 1 life", initialLives - 1, engine.lives)
        assertEquals("Combo must reset to 0 on missed violation", 0, engine.comboStreak)
    }

    @Test
    fun testMissedLegitimateItemDoesNotDeductLife() {
        val initialLives = engine.lives

        engine.spawnItemManually(
            category = ComplianceCategory.VERIFIED_APPROVAL,
            x = 400f,
            y = 2000f,
            vy = 800f
        )

        engine.update(0.10f)

        assertEquals("Letting legitimate item fall must NOT penalize lives", initialLives, engine.lives)
    }

    @Test
    fun testShieldBonusIncreasesLifeAndScore() {
        val legit = engine.spawnItemManually(ComplianceCategory.CERTIFICATION, 100f, 100f)
        engine.processSliceSegment(50f, 100f, 150f, 100f)
        assertEquals("Lives should be 2 after 1 mistake", 2, engine.lives)

        val shield = engine.spawnItemManually(ComplianceCategory.SHIELD, 300f, 300f)
        engine.processSliceSegment(250f, 300f, 350f, 300f)

        assertTrue("Shield was sliced", shield.sliced)
        assertEquals("Shield grants +1 life (2 -> 3)", 3, engine.lives)
        assertEquals("Shield awards +25 base points", 25, engine.score)
    }

    @Test
    fun testSliceSegmentMissDoesNotSlice() {
        val item = engine.spawnItemManually(ComplianceCategory.BRIBERY, 500f, 500f, radius = 50f)

        val hits = engine.processSliceSegment(100f, 100f, 200f, 200f)

        assertEquals("No hits registered", 0, hits)
        assertFalse("Item should remain unsliced", item.sliced)
    }

    @Test
    fun testGameOverWhenLivesDepleted() {
        var gameOverCalled = false
        engine.onGameOver = {
            gameOverCalled = true
        }

        for (i in 1..3) {
            val legit = engine.spawnItemManually(ComplianceCategory.VERIFIED_INVOICE, 100f * i, 200f)
            engine.processSliceSegment(legit.x - 30f, 200f, legit.x + 30f, 200f)
        }

        assertEquals("Lives should be 0", 0, engine.lives)
        assertTrue("Game should be in gameOver state", engine.isGameOver)
        assertTrue("onGameOver callback should be fired", gameOverCalled)
    }

    @Test
    fun testSlicedCategoriesRecordedForDebrief() {
        val b = engine.spawnItemManually(ComplianceCategory.BRIBERY, 200f, 200f)
        val m = engine.spawnItemManually(ComplianceCategory.MONEY_LAUNDERING, 400f, 200f)

        engine.processSliceSegment(b.x - 20f, 200f, b.x + 20f, 200f)
        engine.processSliceSegment(m.x - 20f, 200f, m.x + 20f, 200f)

        val summary = engine.getSlicedCategoriesSummary()
        assertEquals("Should have 2 distinct violation categories recorded", 2, summary.size)
        assertTrue(summary.any { it.category == ComplianceCategory.BRIBERY && it.count == 1 })
        assertTrue(summary.any { it.category == ComplianceCategory.MONEY_LAUNDERING && it.count == 1 })
    }
}
