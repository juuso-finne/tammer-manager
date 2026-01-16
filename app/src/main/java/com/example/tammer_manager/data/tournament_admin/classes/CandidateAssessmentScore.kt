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
)