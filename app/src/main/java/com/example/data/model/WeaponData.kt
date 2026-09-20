package com.example.data.model

enum class WeaponCategory(val displayName: String) {
    AR("Assault Rifle"),
    SMG("Submachine Gun"),
    SHOTGUN("Shotgun"),
    SNIPER("Sniper Rifle"),
    MARKSMAN("Marksman (DMR)"),
    PISTOL("Pistols")
}

data class Weapon(
    val id: String,
    val name: String,
    val category: WeaponCategory,
    val bodyDamage: Int,
    val headDamage: Int,
    val fireRate: Int, // 0 - 100
    val range: Int, // meters
    val magazine: Int,
    val recoilLevel: String, // Low, Medium, High, Extreme
    val recoilPullDownRate: Float, // speed required for drag compensation
    val recommendedDrill: DrillType,
    val proTip: String,
    val dragTechnique: String
)

object WeaponDatabase {
    val weapons: List<Weapon> = listOf(
        // AR
        Weapon(
            id = "ak47",
            name = "AK47",
            category = WeaponCategory.AR,
            bodyDamage = 38,
            headDamage = 209,
            fireRate = 56,
            range = 72,
            magazine = 30,
            recoilLevel = "Extreme",
            recoilPullDownRate = 1.4f,
            recommendedDrill = DrillType.RECOIL,
            proTip = "First 3 shots have pinpoint accuracy. Tap-fire or pull down firmly after the 4th bullet.",
            dragTechnique = "Straight Drag at medium distance; Rotation drag in close quarters."
        ),
        Weapon(
            id = "m4a1",
            name = "M4A1",
            category = WeaponCategory.AR,
            bodyDamage = 29,
            headDamage = 160,
            fireRate = 57,
            range = 79,
            magazine = 30,
            recoilLevel = "Low",
            recoilPullDownRate = 0.8f,
            recommendedDrill = DrillType.ACCURACY,
            proTip = "Most stable AR in the game with chips installed. Excellent for long-range beaming.",
            dragTechnique = "Gentle upward flick; target stays locked easily."
        ),
        Weapon(
            id = "scar",
            name = "SCAR",
            category = WeaponCategory.AR,
            bodyDamage = 30,
            headDamage = 165,
            fireRate = 61,
            range = 68,
            magazine = 30,
            recoilLevel = "Medium",
            recoilPullDownRate = 0.95f,
            recommendedDrill = DrillType.TRACKING,
            proTip = "Balanced fire rate and manageable spread. Perfect starter weapon for learning drag shots.",
            dragTechnique = "Smooth continuous upward drag."
        ),
        Weapon(
            id = "groza",
            name = "GROZA",
            category = WeaponCategory.AR,
            bodyDamage = 35,
            headDamage = 192,
            fireRate = 58,
            range = 77,
            magazine = 30,
            recoilLevel = "Medium",
            recoilPullDownRate = 1.05f,
            recommendedDrill = DrillType.HEADSHOT,
            proTip = "Air-drop powerhouse with heavy single-bullet damage and high stability.",
            dragTechnique = "Direct upward flick when enemy is stationary."
        ),

        // SMG
        Weapon(
            id = "mp40",
            name = "MP40",
            category = WeaponCategory.SMG,
            bodyDamage = 24,
            headDamage = 132,
            fireRate = 83,
            range = 22,
            magazine = 32,
            recoilLevel = "High",
            recoilPullDownRate = 1.6f,
            recommendedDrill = DrillType.TRACKING,
            proTip = "Blistering fire rate. Bullets spread quickly after 10 rounds—reset spray with Gloo Wall.",
            dragTechnique = "Fast, aggressive upward flick to head level then hold tracking."
        ),
        Weapon(
            id = "ump",
            name = "UMP",
            category = WeaponCategory.SMG,
            bodyDamage = 25,
            headDamage = 137,
            fireRate = 75,
            range = 25,
            magazine = 30,
            recoilLevel = "Medium",
            recoilPullDownRate = 1.1f,
            recommendedDrill = DrillType.HEADSHOT,
            proTip = "High armor penetration multiplier. The undisputed king of close-range one-tap spray.",
            dragTechnique = "J-Drag technique (curve slightly down then snap directly upward)."
        ),
        Weapon(
            id = "thompson",
            name = "Thompson",
            category = WeaponCategory.SMG,
            bodyDamage = 27,
            headDamage = 148,
            fireRate = 78,
            range = 24,
            magazine = 42,
            recoilLevel = "Medium",
            recoilPullDownRate = 1.2f,
            recommendedDrill = DrillType.FLICK,
            proTip = "Deep magazine allows sustained suppression without immediate reloading.",
            dragTechnique = "Diagonal drag corresponding to enemy strafe direction."
        ),

        // Shotgun
        Weapon(
            id = "m1887",
            name = "M1887",
            category = WeaponCategory.SHOTGUN,
            bodyDamage = 100,
            headDamage = 550,
            fireRate = 40,
            range = 14,
            magazine = 2,
            recoilLevel = "Extreme",
            recoilPullDownRate = 1.8f,
            recommendedDrill = DrillType.FLICK,
            proTip = "Highest burst damage in game. Must connect first blast directly on target's upper chest/head.",
            dragTechnique = "Explosive upward drag while mid-air jumping; quick switch immediately after firing."
        ),
        Weapon(
            id = "m1014",
            name = "M1014",
            category = WeaponCategory.SHOTGUN,
            bodyDamage = 94,
            headDamage = 470,
            fireRate = 38,
            range = 10,
            magazine = 6,
            recoilLevel = "High",
            recoilPullDownRate = 1.5f,
            recommendedDrill = DrillType.HEADSHOT,
            proTip = "Deadly inside rooms and tight corridors. Level 3 upgrade adds lethal continuous spam.",
            dragTechnique = "Crouch-jump into hard upward drag."
        ),

        // Sniper
        Weapon(
            id = "awm",
            name = "AWM",
            category = WeaponCategory.SNIPER,
            bodyDamage = 150,
            headDamage = 1100,
            fireRate = 27,
            range = 91,
            magazine = 5,
            recoilLevel = "Low",
            recoilPullDownRate = 0.5f,
            recommendedDrill = DrillType.REACTION,
            proTip = "Instant knock on level 3 helmets. Master the switch-shot technique for rapid double taps.",
            dragTechnique = "Hold fire button to scope, align reticle, release to fire."
        ),
        Weapon(
            id = "kar98k",
            name = "Kar98k",
            category = WeaponCategory.SNIPER,
            bodyDamage = 90,
            headDamage = 495,
            fireRate = 27,
            range = 84,
            magazine = 5,
            recoilLevel = "Low",
            recoilPullDownRate = 0.5f,
            recommendedDrill = DrillType.REACTION,
            proTip = "Biomechanic scope locks slightly towards targets. Great for long-range harassment.",
            dragTechnique = "Quick scope alignment followed by quick secondary weapon switch."
        ),

        // Marksman
        Weapon(
            id = "woodpecker",
            name = "Woodpecker",
            category = WeaponCategory.MARKSMAN,
            bodyDamage = 45,
            headDamage = 247,
            fireRate = 38,
            range = 63,
            magazine = 12,
            recoilLevel = "High",
            recoilPullDownRate = 1.3f,
            recommendedDrill = DrillType.HEADSHOT,
            proTip = "Pierces through armor effortlessly. The most respected one-tap headshot weapon.",
            dragTechnique = "Single decisive sharp vertical flick from chest to head."
        ),

        // Pistol
        Weapon(
            id = "desert_eagle",
            name = "Desert Eagle",
            category = WeaponCategory.PISTOL,
            bodyDamage = 90,
            headDamage = 495,
            fireRate = 33,
            range = 38,
            magazine = 7,
            recoilLevel = "High",
            recoilPullDownRate = 1.4f,
            recommendedDrill = DrillType.FLICK,
            proTip = "Clash Squad legendary weapon. One precise tap to head guarantees an instant round win.",
            dragTechnique = "Lock red dot on enemy neck, execute fast vertical upward swipe."
        )
    )
}
