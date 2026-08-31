package com.example.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.R

enum class BadgeShape {
    HEXAGON,        // Violations
    CIRCLE,         // Legitimate (safe)
    DIAMOND_DASHED, // Traps (decoys)
    STAR            // Bonuses
}

enum class ComplianceCategory(
    val id: String,
    @get:StringRes val displayNameRes: Int,
    val isViolation: Boolean,
    val isBonus: Boolean = false,
    val isTrap: Boolean = false,
    val basePoints: Int,
    @get:StringRes val explanationRes: Int,
    @get:DrawableRes val iconRes: Int,
    val badgeColor: Long,
    val borderColor: Long,
    val glowColor: Long = borderColor
) {
    // =========================================================================
    // Violations (MUST SLICE: +10 pts * comboMultiplier) — Warm Orange/Coral
    // =========================================================================
    BRIBERY(
        id = "bribery",
        displayNameRes = R.string.cat_bribery,
        isViolation = true,
        basePoints = 10,
        explanationRes = R.string.exp_bribery,
        iconRes = R.drawable.bribery,
        badgeColor = 0xFFD97706,
        borderColor = 0xFFF59E0B
    ),
    FRAUD(
        id = "fraud",
        displayNameRes = R.string.cat_fraud,
        isViolation = true,
        basePoints = 10,
        explanationRes = R.string.exp_fraud,
        iconRes = R.drawable.fraud,
        badgeColor = 0xFFEA580C,
        borderColor = 0xFFFB923C
    ),
    MONEY_LAUNDERING(
        id = "money_laundering",
        displayNameRes = R.string.cat_money_laundering,
        isViolation = true,
        basePoints = 10,
        explanationRes = R.string.exp_money_laundering,
        iconRes = R.drawable.money_laundering,
        badgeColor = 0xFFC2410C,
        borderColor = 0xFFF97316
    ),
    DATA_BREACH(
        id = "data_breach",
        displayNameRes = R.string.cat_data_breach,
        isViolation = true,
        basePoints = 10,
        explanationRes = R.string.exp_data_breach,
        iconRes = R.drawable.data_breach,
        badgeColor = 0xFFBE185D,
        borderColor = 0xFFF472B6
    ),
    SYSTEMIC_CORRUPTION(
        id = "systemic_corruption",
        displayNameRes = R.string.cat_systemic_corruption,
        isViolation = true,
        basePoints = 25, // High-value rare violation
        explanationRes = R.string.exp_systemic_corruption,
        iconRes = R.drawable.systemic_corruption,
        badgeColor = 0xFF6D28D9,
        borderColor = 0xFFA78BFA
    ),
    INSIDER_TRADING(
        id = "insider_trading",
        displayNameRes = R.string.cat_insider_trading,
        isViolation = true,
        basePoints = 15,
        explanationRes = R.string.exp_insider_trading,
        iconRes = R.drawable.fraud,
        badgeColor = 0xFFB45309,
        borderColor = 0xFFFBBF24
    ),
    CONFLICT_OF_INTEREST(
        id = "conflict_of_interest",
        displayNameRes = R.string.cat_conflict_of_interest,
        isViolation = true,
        basePoints = 15,
        explanationRes = R.string.exp_conflict_of_interest,
        iconRes = R.drawable.bribery,
        badgeColor = 0xFF9A3412,
        borderColor = 0xFFFB923C
    ),
    EMBEZZLEMENT(
        id = "embezzlement",
        displayNameRes = R.string.cat_embezzlement,
        isViolation = true,
        basePoints = 20,
        explanationRes = R.string.exp_embezzlement,
        iconRes = R.drawable.money_laundering,
        badgeColor = 0xFF9F1239,
        borderColor = 0xFFFB7185
    ),

    // =========================================================================
    // Legitimate / Do-not-slice (AVOID: Slicing costs 1 life) — Soft Blue/Sky
    // =========================================================================
    OFFICIAL_DOCUMENT(
        id = "official_doc",
        displayNameRes = R.string.cat_official_document,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_official_document,
        iconRes = R.drawable.official_document,
        badgeColor = 0xFF1D4ED8,
        borderColor = 0xFF60A5FA
    ),
    VERIFIED_APPROVAL(
        id = "verified_approval",
        displayNameRes = R.string.cat_verified_approval,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_verified_approval,
        iconRes = R.drawable.verified_approval,
        badgeColor = 0xFF0284C7,
        borderColor = 0xFF38BDF8
    ),
    VALID_PARTNERSHIP(
        id = "valid_partnership",
        displayNameRes = R.string.cat_valid_partnership,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_valid_partnership,
        iconRes = R.drawable.valid_partnership,
        badgeColor = 0xFF0E7490,
        borderColor = 0xFF22D3EE
    ),
    CERTIFICATION(
        id = "certification",
        displayNameRes = R.string.cat_certification,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_certification,
        iconRes = R.drawable.certification,
        badgeColor = 0xFF2563EB,
        borderColor = 0xFF93C5FD
    ),
    VERIFIED_INVOICE(
        id = "verified_invoice",
        displayNameRes = R.string.cat_verified_invoice,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_verified_invoice,
        iconRes = R.drawable.verified_invoice,
        badgeColor = 0xFF0369A1,
        borderColor = 0xFF7DD3FC
    ),
    COMPLIANCE_AUDIT(
        id = "compliance_audit",
        displayNameRes = R.string.cat_compliance_audit,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_compliance_audit,
        iconRes = R.drawable.official_document,
        badgeColor = 0xFF1E40AF,
        borderColor = 0xFF93C5FD
    ),
    ETHICS_TRAINING(
        id = "ethics_training",
        displayNameRes = R.string.cat_ethics_training,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_ethics_training,
        iconRes = R.drawable.certification,
        badgeColor = 0xFF1E3A8A,
        borderColor = 0xFF60A5FA
    ),
    TRANSPARENCY_REPORT(
        id = "transparency_report",
        displayNameRes = R.string.cat_transparency_report,
        isViolation = false,
        basePoints = 0,
        explanationRes = R.string.exp_transparency_report,
        iconRes = R.drawable.verified_approval,
        badgeColor = 0xFF075985,
        borderColor = 0xFF38BDF8
    ),

    // =========================================================================
    // Traps (Decoys - Avoid: -10 pts, resets combo, no life lost) — Soft Purple
    // =========================================================================
    FALSE_ALARM(
        id = "false_alarm",
        displayNameRes = R.string.cat_false_alarm,
        isViolation = false,
        isTrap = true,
        basePoints = 0,
        explanationRes = R.string.exp_false_alarm,
        iconRes = R.drawable.false_alarm,
        badgeColor = 0xFF7E22CE,
        borderColor = 0xFFC084FC
    ),
    UNVERIFIED_RUMOR(
        id = "unverified_rumor",
        displayNameRes = R.string.cat_unverified_rumor,
        isViolation = false,
        isTrap = true,
        basePoints = 0,
        explanationRes = R.string.exp_unverified_rumor,
        iconRes = R.drawable.unverified_rumor,
        badgeColor = 0xFF6B21A8,
        borderColor = 0xFFA855F7
    ),
    HONEST_MISTAKE(
        id = "honest_mistake",
        displayNameRes = R.string.cat_honest_mistake,
        isViolation = false,
        isTrap = true,
        basePoints = 0,
        explanationRes = R.string.exp_honest_mistake,
        iconRes = R.drawable.honest_mistake,
        badgeColor = 0xFF86198F,
        borderColor = 0xFFE879F9
    ),
    PHISHING_BAIT(
        id = "phishing_bait",
        displayNameRes = R.string.cat_phishing_bait,
        isViolation = false,
        isTrap = true,
        basePoints = 0,
        explanationRes = R.string.exp_phishing_bait,
        iconRes = R.drawable.data_breach,
        badgeColor = 0xFF581C87,
        borderColor = 0xFFD8B4FE
    ),
    MISFILED_MEMO(
        id = "misfiled_memo",
        displayNameRes = R.string.cat_misfiled_memo,
        isViolation = false,
        isTrap = true,
        basePoints = 0,
        explanationRes = R.string.exp_misfiled_memo,
        iconRes = R.drawable.honest_mistake,
        badgeColor = 0xFF701A75,
        borderColor = 0xFFF0ABFC
    ),

    // =========================================================================
    // Bonus Items (~5% spawn chance: +1 life, +25 pts) — Soft Gold / Amber
    // =========================================================================
    SHIELD(
        id = "shield",
        displayNameRes = R.string.cat_shield,
        isViolation = false,
        isBonus = true,
        basePoints = 25,
        explanationRes = R.string.exp_shield,
        iconRes = R.drawable.compliance_shield,
        badgeColor = 0xFFCA8A04,
        borderColor = 0xFFFDE047
    ),
    WHISTLEBLOWER(
        id = "whistleblower",
        displayNameRes = R.string.cat_whistleblower,
        isViolation = false,
        isBonus = true,
        basePoints = 25,
        explanationRes = R.string.exp_whistleblower,
        iconRes = R.drawable.bonus_shield,
        badgeColor = 0xFFD97706,
        borderColor = 0xFFFEF08A
    ),
    STAR_AUDITOR(
        id = "star_auditor",
        displayNameRes = R.string.cat_star_auditor,
        isViolation = false,
        isBonus = true,
        basePoints = 30,
        explanationRes = R.string.exp_star_auditor,
        iconRes = R.drawable.bonus_shield,
        badgeColor = 0xFFB45309,
        borderColor = 0xFFFACC15
    );

    val badgeShape: BadgeShape
        get() = when {
            isBonus -> BadgeShape.STAR
            isTrap -> BadgeShape.DIAMOND_DASHED
            isViolation -> BadgeShape.HEXAGON
            else -> BadgeShape.CIRCLE
        }

    companion object {
        val VIOLATIONS = entries.filter { it.isViolation && it != SYSTEMIC_CORRUPTION }
        val ALL_VIOLATIONS = entries.filter { it.isViolation }
        val LEGITIMATE = entries.filter { !it.isViolation && !it.isBonus && !it.isTrap }
        val TRAPS = entries.filter { it.isTrap }
        val BONUSES = entries.filter { it.isBonus }
    }

    val categoryTypeLabelRes: Int
        @StringRes get() = when {
            isBonus -> R.string.category_bonus
            isTrap -> R.string.category_trap
            isViolation -> R.string.category_violation
            else -> R.string.category_legit
        }
}
