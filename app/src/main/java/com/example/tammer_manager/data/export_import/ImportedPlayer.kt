package com.example.tammer_manager.data.export_import

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportedPlayer (
    val fullName: String,
    val rating: Int
) : Parcelable