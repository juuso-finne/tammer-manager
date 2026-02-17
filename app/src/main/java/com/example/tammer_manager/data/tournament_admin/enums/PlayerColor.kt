package com.example.tammer_manager.data.tournament_admin.enums

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class PlayerColor (val colorValue: Color, val balance:Int) {
    WHITE (colorValue = Color.White, balance = 1) {
        override fun reverse(): PlayerColor {
            return PlayerColor.BLACK
        }
    },
    BLACK (colorValue = Color.Black, balance = -1) {
        override fun reverse(): PlayerColor {
            return PlayerColor.WHITE
        }
    }
    ;

    abstract fun reverse(): PlayerColor
}