package com.example.tammer_manager.data.tournament_admin.enums

import androidx.compose.ui.graphics.Color

enum class PlayerColor (val colorValue: Color) {
    WHITE (colorValue = Color.White) {
        override fun reverse(): PlayerColor {
            return PlayerColor.BLACK
        }
    },
    BLACK (colorValue = Color.Black) {
        override fun reverse(): PlayerColor {
            return PlayerColor.WHITE
        }
    }
    ;

    abstract fun reverse(): PlayerColor
}