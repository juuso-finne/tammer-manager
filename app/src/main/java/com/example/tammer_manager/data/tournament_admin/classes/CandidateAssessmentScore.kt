package com.example.tammer_manager.data.tournament_admin.classes

data class CandidateAssessmentScore(
    var bestTotal: PairingAssessmentCriteria = PairingAssessmentCriteria(
        pabAssigneeScore = Float.MAX_VALUE,
        pabAssigneeUnplayedGames = Int.MAX_VALUE,
        topScorerOrOpponentColorImbalanceCount = Int.MAX_VALUE,
        topScorersOrOpponentsColorstreakCount = Int.MAX_VALUE,
        colorpreferenceConflicts = Int.MAX_VALUE,
        strongColorpreferenceConflicts = Int.MAX_VALUE
    ),

    val bestCandidate : MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>> = mutableListOf(),

    val currentTotal: PairingAssessmentCriteria = PairingAssessmentCriteria(),

    val currentIndividualAssessments: MutableList<PairingAssessmentCriteria> = mutableListOf(),

    val currentCandidate: MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>> = mutableListOf(),

    var isValidCandidate: Boolean = false
){
    fun updateHiScore(newBest: List<Pair<RegisteredPlayer, RegisteredPlayer?>>):Boolean{
        if (this.currentTotal >= bestTotal){
            return false
        }

        this.bestTotal = this.currentTotal.copy()
        this.bestCandidate.clear()
        this.bestCandidate.addAll(newBest)
        return true
    }

    fun resetCurrentScore(){
        this.currentTotal.reset()
        this.currentIndividualAssessments.clear()
        this.isValidCandidate = false
        this.currentCandidate.clear()
    }
}