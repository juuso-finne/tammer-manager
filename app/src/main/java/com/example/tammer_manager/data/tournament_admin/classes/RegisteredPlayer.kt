package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisteredPlayer (
    val fullName: String,
    val rating: Int,
    val id: Int,
    var tpn: Int,
    var isActive: Boolean = true,
    var score: Float = 0f,
    val matchHistory: MatchHistory = listOf()
): Parcelable{

    fun getColorBalance():Int{
        return matchHistory.sumOf() { match ->
            match.opponentId?.let{
                match.color.balance
            } ?: 0
        }
    }

    fun sameColorInLastNRounds(n: Int): Boolean{
        val sortedHistory = matchHistory.sortedByDescending { it.round }.filter { it.opponentId != null }
        if (sortedHistory.size < n || n <= 0){
            return false
        }

        val initalColor = sortedHistory[0].color

        for (i in 1..<n){
            if (sortedHistory[i].color != initalColor){
                return false
            }
        }
        return true
    }
}