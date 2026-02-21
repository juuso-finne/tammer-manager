package com.example.tammer_manager.data.tournament_admin.classes

data class BracketScoringData(
    var bestTotal: PairingAssessmentCriteria = PairingAssessmentCriteria(
        pabAssigneeScore = Float.MAX_VALUE,
        pabAssigneeUnplayedGames = Int.MAX_VALUE,
        topScorerOrOpponentColorImbalanceCount = Int.MAX_VALUE,
        topScorersOrOpponentsColorstreakCount = Int.MAX_VALUE,
        colorpreferenceConflicts = Int.MAX_VALUE,
        strongColorpreferenceConflicts = Int.MAX_VALUE
    ),

    val bestCandidate : MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>> = mutableListOf(),

    val mdpPairingScore: PairingAssessmentCriteria = PairingAssessmentCriteria(),
    var mdpPairs: List<Pair<RegisteredPlayer, RegisteredPlayer>> = listOf(),

    val remainderPairingScore: PairingAssessmentCriteria = PairingAssessmentCriteria(),
    var remainderPairs: List<Pair<RegisteredPlayer, RegisteredPlayer>> = listOf(),

    var isValidCandidate: Boolean = false
){
    fun updateHiScore():Boolean{
        if (this.mdpPairingScore + this.remainderPairingScore >= this.bestTotal){
            return false
        }

        this.bestTotal = this.mdpPairingScore + this.remainderPairingScore
        this.bestCandidate.clear()
        this.bestCandidate.addAll(this.mdpPairs.plus(this.remainderPairs))
        return true
    }
}