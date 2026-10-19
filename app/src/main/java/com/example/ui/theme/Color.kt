package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Gaming VC Dark Cyberpunk Design System Colors
val GamingDarkBg = Color(0xFF050A18)
val GamingSecondaryBg = Color(0xFF0A1228)
val GamingCardBg = Color(0xFF0E1935)
val GamingElevatedSurface = Color(0xFF132142)

val GamingPrimaryPurple = Color(0xFF6547FF)
val GamingDeepPurple = Color(0xFF8B35FF)
val GamingNeonBlue = Color(0xFF168BFF)
val GamingCyan = Color(0xFF20D9FF)
val GamingSuccessGreen = Color(0xFF00D6A0)
val GamingErrorRed = Color(0xFFFF4567)

val GamingTextPrimary = Color(0xFFF6F8FF)
val GamingTextSecondary = Color(0xFFA2B1D7)
val GamingTextMuted = Color(0xFF7181AB)
val GamingBorder = Color(0xFF203361)

// Backward compatible aliases to ensure existing components render smoothly
val MidnightNavy = GamingDarkBg
val DeepNavy = GamingSecondaryBg
val SlateNavy = GamingElevatedSurface
val CardNavy = GamingCardBg
val BorderNavy = GamingBorder

val ElectricViolet = GamingPrimaryPurple
val RoyalViolet = GamingDeepPurple
val VioletContainer = Color(0xFF1E1748)

val VibrantCyan = GamingCyan
val CyanAccent = GamingCyan
val CyanContainer = Color(0xFF0C243D)

val CallRed = GamingErrorRed
val CallRedContainer = Color(0xFF38101E)

val ActiveGreen = GamingSuccessGreen
val MicActiveYellow = Color(0xFFFFB800)

val TextPrimary = GamingTextPrimary
val TextSecondary = GamingTextSecondary
val TextTertiary = GamingTextMuted

// Gradients
val GamingButtonGradient = Brush.horizontalGradient(
    colors = listOf(GamingPrimaryPurple, GamingNeonBlue)
)

val GamingCardGradient = Brush.verticalGradient(
    colors = listOf(GamingCardBg, GamingSecondaryBg)
)

val GamingHeroGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF2A1B6B), Color(0xFF0C3875), GamingCardBg)
)

val GamingLogoGradient = Brush.linearGradient(
    colors = listOf(GamingCyan, GamingPrimaryPurple, GamingDeepPurple)
)
