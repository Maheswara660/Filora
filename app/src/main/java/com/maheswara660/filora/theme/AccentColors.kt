package com.maheswara660.filora.theme

import androidx.compose.ui.graphics.Color

data class AccentCombination(
    val primary: Color,
    val secondary: Color,
    val name: String
)

val CustomAccents = listOf(
    AccentCombination(Color(0xFF006064), Color(0xFF00838F), "Ocean Deep"), // Teal / Cyan
    AccentCombination(Color(0xFF880E4F), Color(0xFFAD1457), "Berry Punch"), // Pink / Rose
    AccentCombination(Color(0xFF1B5E20), Color(0xFF2E7D32), "Forest Green"), // Green
    AccentCombination(Color(0xFFE65100), Color(0xFFEF6C00), "Sunset Orange"), // Orange
    AccentCombination(Color(0xFF0D47A1), Color(0xFF1565C0), "Midnight Blue"), // Blue
    AccentCombination(Color(0xFFFF6F00), Color(0xFFFF8F00), "Amber Gold"), // Amber
    AccentCombination(Color(0xFF4A148C), Color(0xFF6A1B9A), "Royal Violet"), // Purple
    AccentCombination(Color(0xFFB71C1C), Color(0xFFC62828), "Ruby Red"), // Red
    AccentCombination(Color(0xFF33691E), Color(0xFF558B2F), "Olive Moss"), // Olive
    AccentCombination(Color(0xFF3E2723), Color(0xFF4E342E), "Coffee Bean")  // Brown
)
