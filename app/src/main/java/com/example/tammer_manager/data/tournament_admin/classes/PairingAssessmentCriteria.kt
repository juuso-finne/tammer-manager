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
    var strongColorpreferenceConflicts: Int = 0,

    /**Minimise the number of resident downfloaters who received a downfloat the previous round.*/
    var previousRoundDownfloaters: Int = 0,

    /**Minimise the number of MDP opponents who received an upfloat the previous round.*/
    var previousRoundUpfloaters: Int = 0,

    /**Minimise the number of resident downfloaters who received a downfloat two rounds before.*/
    var twoRoundsPriorDownfloaters: Int = 0,

    /**Minimise the number of MDP opponents who received an upfloat two rounds before.*/
    var twoRoundsPriorUpfloaters: Int = 0

): Comparable<PairingAssessmentCriteria>{

    operator fun plusAssign(other: PairingAssessmentCriteria){
        this.pabAssigneeScore += other.pabAssigneeScore
        this.pabAssigneeUnplayedGames += other.pabAssigneeUnplayedGames
        this.topScorerOrOpponentColorImbalanceCount += other.topScorerOrOpponentColorImbalanceCount
        this.topScorersOrOpponentsColorstreakCount += other.topScorersOrOpponentsColorstreakCount
        this.colorpreferenceConflicts += other.colorpreferenceConflicts
        this.strongColorpreferenceConflicts += other.strongColorpreferenceConflicts
        this.previousRoundDownfloaters += other.previousRoundDownfloaters
        this.previousRoundUpfloaters += other.previousRoundUpfloaters
        this.twoRoundsPriorDownfloaters += other.twoRoundsPriorDownfloaters
        this.twoRoundsPriorUpfloaters += other.twoRoundsPriorUpfloaters
    }

    operator fun minusAssign(other: PairingAssessmentCriteria){
        this.pabAssigneeScore -= other.pabAssigneeScore
        this.pabAssigneeUnplayedGames -= other.pabAssigneeUnplayedGames
        this.topScorerOrOpponentColorImbalanceCount -= other.topScorerOrOpponentColorImbalanceCount
        this.topScorersOrOpponentsColorstreakCount -= other.topScorersOrOpponentsColorstreakCount
        this.colorpreferenceConflicts -= other.colorpreferenceConflicts
        this.strongColorpreferenceConflicts -= other.strongColorpreferenceConflicts
        this.previousRoundDownfloaters -= other.previousRoundDownfloaters
        this.previousRoundUpfloaters -= other.previousRoundUpfloaters
        this.twoRoundsPriorDownfloaters -= other.twoRoundsPriorDownfloaters
        this.twoRoundsPriorUpfloaters -= other.twoRoundsPriorUpfloaters
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
                this.strongColorpreferenceConflicts + other.strongColorpreferenceConflicts,
            previousRoundDownfloaters =
                this.previousRoundDownfloaters + other.previousRoundDownfloaters,
            previousRoundUpfloaters =
                this.previousRoundUpfloaters + other.previousRoundUpfloaters,
            twoRoundsPriorDownfloaters =
                this.twoRoundsPriorDownfloaters + other.twoRoundsPriorDownfloaters,
            twoRoundsPriorUpfloaters =
                this.twoRoundsPriorUpfloaters + other.twoRoundsPriorUpfloaters
        )
    }

    override fun compareTo(other: PairingAssessmentCriteria): Int =
        compareBy<PairingAssessmentCriteria>(
            { it.pabAssigneeScore },
            { it.pabAssigneeUnplayedGames },
            { it.topScorerOrOpponentColorImbalanceCount },
            { it.topScorersOrOpponentsColorstreakCount },
            { it.colorpreferenceConflicts },
            { it.strongColorpreferenceConflicts },
            { it.previousRoundDownfloaters },
            { it.previousRoundUpfloaters },
            { it.twoRoundsPriorDownfloaters },
            { it.twoRoundsPriorUpfloaters }
        ).compare(this, other)

    fun reset(){
        this.pabAssigneeScore = 0f
        this.pabAssigneeUnplayedGames = 0
        this.topScorerOrOpponentColorImbalanceCount = 0
        this.topScorersOrOpponentsColorstreakCount = 0
        this.colorpreferenceConflicts = 0
        this.strongColorpreferenceConflicts = 0
        this.previousRoundDownfloaters = 0
        this.previousRoundUpfloaters = 0
        this.twoRoundsPriorDownfloaters = 0
        this.twoRoundsPriorUpfloaters = 0
    }

    fun setToMax(){
        this.pabAssigneeScore = Float.MAX_VALUE
        this.pabAssigneeUnplayedGames = Int.MAX_VALUE
        this.topScorerOrOpponentColorImbalanceCount = Int.MAX_VALUE
        this.topScorersOrOpponentsColorstreakCount = Int.MAX_VALUE
        this.colorpreferenceConflicts = Int.MAX_VALUE
        this.strongColorpreferenceConflicts = Int.MAX_VALUE
        this.previousRoundDownfloaters = Int.MAX_VALUE
        this.previousRoundUpfloaters = Int.MAX_VALUE
        this.twoRoundsPriorDownfloaters = Int.MAX_VALUE
        this.twoRoundsPriorUpfloaters = Int.MAX_VALUE
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

    fun compareByColorConflict(other: PairingAssessmentCriteria):Int{
        return Companion.colorConflictComparator.compare(this, other)
    }
}
