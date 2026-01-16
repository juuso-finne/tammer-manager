package com.example.tammer_manager.data.tournament_admin.classes

data class CandidateAssessmentScore(
    var bestTotal: PairingAssessmentCriteria = PairingAssessmentCriteria(
        pabAssigneeUnplayedGames = Int.MAX_VALUE,
        topScorerOrOpponentColorImbalanceCount = Int.MAX_VALUE,
        topScorersOrOpponentsColorstreakCount = Int.MAX_VALUE,
        colorpreferenceConflicts = Int.MAX_VALUE,
        strongColorpreferenceConflicts = Int.MAX_VALUE
    ),

    val bestCandidate : MutableList<Pair<RegisteredPlayer, RegisteredPlayer?>> = mutableListOf(),

    val currentTotal: PairingAssessmentCriteria = PairingAssessmentCriteria(),

    val currentIndividualAssessments: MutableList<PairingAssessmentCriteria> = mutableListOf(),

    var isValidCandidate: Boolean = false
){
    fun updateHiScore(
        s1: List<RegisteredPlayer>,
        s2: List<RegisteredPlayer>
    ){
        this.bestTotal = this.currentTotal
        this.bestCandidate.clear()

        for (i in 0 until s1.size){
            this.bestCandidate.add(Pair(s1[i], s2[i]))
        }
    }
}