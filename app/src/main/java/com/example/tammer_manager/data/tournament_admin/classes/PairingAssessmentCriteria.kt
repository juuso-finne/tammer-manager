package com.example.tammer_manager.data.tournament_admin.classes

data class PairingAssessmentCriteria(

    /**Minimise the score of the assignee of the pairing-allocated-bye.*/
    var pabAssigneeScore: Float = 0f,

    /**Minimise the number of unplayed games of the assignee of the pairing-allocated-bye.*/
    var pabAssigneeUnplayedGames:Int = 0,

    /**Minimise the number of topscorers or topscorers' opponents who get a colour difference higher than +2 or lower than -2*/
    var topScorerOrOpponentColorImbalanceCount: Int = 0,

    /**Minimise the number of topscorers or topscorers' opponents who get the same colour three times in a row.*/
    var topScorersOrOpponentsColorstreakCount: Int = 0,

    /**Minimise the number of players who do not get their colour preference.*/
    var colorpreferenceConflicts: Int = 0,

    /**Minimise the number of players who do not get their strong colour preference.*/
    var strongColorpreferenceConflicts: Int = 0
): Comparable<PairingAssessmentCriteria>{

    operator fun plusAssign(other: PairingAssessmentCriteria){

        this.pabAssigneeScore += other.pabAssigneeScore
        this.pabAssigneeUnplayedGames += other.pabAssigneeUnplayedGames
        this.topScorerOrOpponentColorImbalanceCount += other.topScorerOrOpponentColorImbalanceCount
        this.topScorersOrOpponentsColorstreakCount += other.topScorersOrOpponentsColorstreakCount
        this.colorpreferenceConflicts += other.colorpreferenceConflicts
        this.strongColorpreferenceConflicts += other.strongColorpreferenceConflicts

    }

    operator fun minusAssign(other: PairingAssessmentCriteria){
        this.pabAssigneeScore -= other.pabAssigneeScore
        this.pabAssigneeUnplayedGames -= other.pabAssigneeUnplayedGames
        this.topScorerOrOpponentColorImbalanceCount -= other.topScorerOrOpponentColorImbalanceCount
        this.topScorersOrOpponentsColorstreakCount -= other.topScorersOrOpponentsColorstreakCount
        this.colorpreferenceConflicts -= other.colorpreferenceConflicts
        this.strongColorpreferenceConflicts -= other.strongColorpreferenceConflicts
    }

    operator fun plus(other: PairingAssessmentCriteria): PairingAssessmentCriteria {
        return PairingAssessmentCriteria(
            pabAssigneeScore =
                this.pabAssigneeScore + other.pabAssigneeScore,
            pabAssigneeUnplayedGames =
                this.pabAssigneeUnplayedGames + other.pabAssigneeUnplayedGames,
            topScorerOrOpponentColorImbalanceCount =
                this.topScorerOrOpponentColorImbalanceCount + other.topScorerOrOpponentColorImbalanceCount,
            topScorersOrOpponentsColorstreakCount =
                this.topScorersOrOpponentsColorstreakCount + other.topScorersOrOpponentsColorstreakCount,
            colorpreferenceConflicts =
                this.colorpreferenceConflicts + other.colorpreferenceConflicts,
            strongColorpreferenceConflicts =
                this.strongColorpreferenceConflicts + other.strongColorpreferenceConflicts
        )
    }

    override fun compareTo(other: PairingAssessmentCriteria): Int =
        compareBy<PairingAssessmentCriteria>(
            {it.pabAssigneeScore},
            { it.pabAssigneeUnplayedGames },
            { it.topScorerOrOpponentColorImbalanceCount },
            { it.topScorersOrOpponentsColorstreakCount },
            { it.colorpreferenceConflicts },
            { it.strongColorpreferenceConflicts }
        ).compare(this, other)

    fun reset(){
        this.pabAssigneeScore = 0f
        this.pabAssigneeUnplayedGames = 0
        this.topScorerOrOpponentColorImbalanceCount = 0
        this.topScorersOrOpponentsColorstreakCount= 0
        this.colorpreferenceConflicts = 0
        this.strongColorpreferenceConflicts = 0
    }

    fun setToMax(){
        this.pabAssigneeScore = Float.MAX_VALUE
        this.pabAssigneeUnplayedGames = Int.MAX_VALUE
        this.topScorerOrOpponentColorImbalanceCount = Int.MAX_VALUE
        this.topScorersOrOpponentsColorstreakCount= Int.MAX_VALUE
        this.colorpreferenceConflicts = Int.MAX_VALUE
        this.strongColorpreferenceConflicts = Int.MAX_VALUE
    }

    companion object{
        val colorConflictComparator = Comparator<PairingAssessmentCriteria>{a, b ->
            var difference = a.strongColorpreferenceConflicts - b.strongColorpreferenceConflicts
            if(difference == 0){
                difference = a.colorpreferenceConflicts - b.colorpreferenceConflicts
            }
            difference
        }
    }
}
