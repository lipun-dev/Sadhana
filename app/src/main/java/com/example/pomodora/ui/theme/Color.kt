package com.example.pomodora.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- Option B: Light & Fresh (Day Mode) ---
val LightSecondary = Color(0xFF615B20)     // The Olive Brown
val LightPrimary = Color(0xFFE8E796)   // The Pale Cornsilk (Accents)
val LightTertiary = Color(0xFF787029)    // The Circle Color (Olive)
val LightBackground = Color(0xFF3A310F)  // The Beige Paper
val LightSurface = Color(0xFF2C2710)     // Pure White
val LightText = Color(0xFFFFFFFF)   // White text on the olive button
val LightOnPrimary = Color(0xFF051F06)        // Dark Blue-Grey text

// --- Option A: Deep Focus (Night Mode) ---
val DarkPrimary = Color(0xFFBCEFA7)      // The Light Sprout Green
val DarkSecondary = Color(0xFF416839)   // The Deep Fern Green
val DarkTertiary = Color(0xFF416839)
val DarkBackground = Color(0xFF051F06)   // The Very Dark Forest
val DarkSurface = Color(0xFF0C290D)      // Slightly lighter than background for cards
val DarkOnPrimary = Color(0xFF051F06)    // Dark text on the light green button
val DarkText = Color(0xFFE8F5E9) // Pale Mint text (Easy to read)

val DeepForestGreen = Color(0xFF031A0C) // The dark background
val MintAccent = Color(0xFF86EFAC)      // The bright wave/text color
val GlassBlack = Color(0x4D000000)      // 30% transparent black for fields
val FieldBorder = Color(0x3386EFAC)

val AppBackground     = Color(0xFF060E09)   // near-black forest floor
val FocusBgDeep       = Color(0xFF050D08)   // lifted surface
val CardSurface       = Color(0xFF101F16)   // card base
val CardBorder        = Color(0xFF1C3525)   // subtle border
val GlowGreen         = Color(0xFF39FF8F)   // neon mint — primary glow
val AccentGreen       = Color(0xFF2DC76D)   // mid green
val MutedGreen        = Color(0xFF1A4D32)   // deep muted for inactive
val EmptyCell         = Color(0xFF152B1E)   // empty heatmap cell
val TextPrimary       = Color(0xFFF0FFF6)   // near-white with green tint
val TextSecondary     = Color(0xFF5C8A6E)   // muted sage
val TextHint          = Color(0xFF2E5040)   // very muted
val GoldAccent        = Color(0xFFFFB830)   // amber gold — highlight
val CoralAccent       = Color(0xFFFF6B6B)   // warm coral — streak
val PurpleAccent      = Color(0xFFB57BFF) // soft violet — trees

//Focus Screen
val FocusBgSurface     = Color(0xFF0C1910)
val FocusCardSurface   = Color(0xFF101F14)
val FocusCardBorder    = Color(0xFF1B3322)
val FocusRingTrack     = Color(0xFF132B1C)
val FocusGlowGreen     = Color(0xFF3DFFA0)
val FocusAccentGreen   = Color(0xFF28D97A)
val FocusMutedGreen    = Color(0xFF1C4A30)
val FocusBreakBlue     = Color(0xFF4FC3F7)
val FocusBreakBlueMuted= Color(0xFF1A3A4A)
val FocusDangerRed     = Color(0xFFFF5252)
val FocusDangerMuted   = Color(0xFF3D1515)
val FocusTextSecondary = Color(0xFF5A8A6C)
val GreenBarGradient     = Brush.verticalGradient(listOf(GlowGreen, AccentGreen, MutedGreen))
val GoldBarGradient      = Brush.verticalGradient(listOf(GoldAccent, Color(0xFFE0920A)))
val CardGradient         = Brush.verticalGradient(listOf(CardSurface, Color(0xFF0D1A12)))
val HeaderGradient       = Brush.horizontalGradient(listOf(GlowGreen, AccentGreen))