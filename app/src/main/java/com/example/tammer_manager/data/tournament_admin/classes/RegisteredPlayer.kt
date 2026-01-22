package com.example.tammer_manager.data.tournament_admin.classes

import android.os.Parcelable
import com.example.tammer_manager.data.tournament_admin.enums.ColorPreferenceStrength
import com.example.tammer_manager.data.tournament_admin.enums.PlayerColor
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

@Parcelize
data class RegisteredPlayer (
    val fullName: String,
    val rating: Int,
    val id: Int,
    var tpn: Int,
    var isActive: Boolean = true,
    var score: Float = 0f,
    var matchHistory: MatchHistory = listOf(),
    var receivedPairingBye: Boolean = false
): Parcelable, Comparable<RegisteredPlayer>{

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

    fun getColorPreference(): ColorPreference{
        val sortedHistory = matchHistory.sortedByDescending { it.round }.filter { it.opponentId != null }

        if (sortedHistory.isEmpty()){
            return ColorPreference()
        }

        val colorBalance = getColorBalance()

        var strength = when(abs(colorBalance)){
            0 -> ColorPreferenceStrength.MILD
            1 -> ColorPreferenceStrength.STRONG
            else -> ColorPreferenceStrength.ABSOLUTE
        }

        var preferredColor = sortedHistory.first().color.reverse()

        if (colorBalance < 0){
            preferredColor = PlayerColor.WHITE
        } else if (colorBalance > 0){
            preferredColor = PlayerColor.BLACK
        }

        if (sameColorInLastNRounds(2)){
            strength = ColorPreferenceStrength.ABSOLUTE
        }

        return ColorPreference(
            strength = strength,
            preferredColor = preferredColor,
            colorBalance = colorBalance
        )
    }

    fun isTopScorer(roundsCompleted: Int): Boolean{
        return score > roundsCompleted / 2f
    }

    override fun compareTo(other: RegisteredPlayer): Int {
        return compareValuesBy(this, other, {-it.score}, {it.tpn})
    }
}