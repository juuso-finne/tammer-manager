package com.example.tammer_manager.data.tournament_admin.classes

data class PairingAssessmentCriteria(

    //Minimise the number of unplayed games of the assignee of the pairing-allocated-bye.
    var pabAssigneeUnplayedGames:Int = 0,

    //Minimise the number of topscorers or topscorers' opponents who get a colour difference higher than +2 or lower than -2
    var topScorerOrOpponentColorImbalanceCount: Int = 0,

    //Minimise the number of topscorers or topscorers' opponents who get the same colour three times in a row.
    var topScorersOrOpponentsColorstreakCount: Int = 0,

    //Minimise the number of players who do not get their colour preference.
    var colorpreferenceConflicts: Int = 0,

    //Minimise the number of players who do not get their strong colour preference.
    var strongColorpreferenceConflicts: Int = 0
): Comparable<PairingAssessmentCriteria>{

    override fun compareTo(other: PairingAssessmentCriteria): Int =
        compareBy<PairingAssessmentCriteria>(
            { it.pabAssigneeUnplayedGames },
            { it.topScorerOrOpponentColorImbalanceCount },
            { it.topScorersOrOpponentsColorstreakCount },
            { it.colorpreferenceConflicts },
            { it.strongColorpreferenceConflicts }
        ).compare(this, other)
}
