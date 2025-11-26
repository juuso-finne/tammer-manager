package com.example.tammer_manager.data.player_import

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportedPlayer (val fullName: String, val rating: Int) : Parcelable