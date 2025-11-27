package com.example.tammer_manager.data.player_import

import android.os.Parcelable
import com.example.tammer_manager.data.interfaces.Player
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportedPlayer (
    override val fullName: String,
    override val rating: Int
) : Parcelable, Player