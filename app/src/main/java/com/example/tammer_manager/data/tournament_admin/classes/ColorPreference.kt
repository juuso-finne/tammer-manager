package com.example.tammer_manager.data.tournament_admin.classes

import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor

data class ColorPreference(
    val strength: ColorPreferenceStrength = ColorPreferenceStrength.NONE,
    val colorBalance: Int = 0,
    val preferredColor: PlayerColor? = null
)