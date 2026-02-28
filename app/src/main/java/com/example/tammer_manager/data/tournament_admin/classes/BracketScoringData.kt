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
    val bracketTheoreticalBest: PairingAssessmentCriteria = PairingAssessmentCriteria(),

    val mdpPairingScore: PairingAssessmentCriteria = PairingAssessmentCriteria(),
    val mdpPairs: MutableList<Pair<RegisteredPlayer, RegisteredPlayer>> = mutableListOf(),
    var mdpSplitTheoreticalBest: PairingAssessmentCriteria = PairingAssessmentCriteria(),

    val remainderPairingScore: PairingAssessmentCriteria = PairingAssessmentCriteria(),
    val remainderPairs: MutableList<Pair<RegisteredPlayer, RegisteredPlayer>> = mutableListOf(),
    var remainderTheoreticalBest: PairingAssessmentCriteria = PairingAssessmentCriteria(),
    var remainderSplitTheoreticalBest: PairingAssessmentCriteria = PairingAssessmentCriteria(),

    var foundValidCandidate: Boolean = false,
    var foundBestPossibleScore: Boolean = false
){
    fun updateHiScore():Boolean{
        if (this.mdpPairingScore + this.remainderPairingScore >= this.bestTotal){
            return false
        }

        this.bestTotal = this.mdpPairingScore + this.remainderPairingScore
        this.bestCandidate.clear()
        this.bestCandidate.addAll(this.mdpPairs.plus(this.remainderPairs))

        foundBestPossibleScore = (
            this.bestTotal == PairingAssessmentCriteria() ||
            PairingAssessmentCriteria.colorConflictComparator.compare(
                this.bestTotal, this.bracketTheoreticalBest
            ) <= 0
        )
        return true
    }

    fun setRemainderPairs(s1: List<RegisteredPlayer>, s2: List<RegisteredPlayer>){
        remainderPairs.clear()
        s1.indices.forEach {
            remainderPairs.add(Pair(s1[it], s2[it]))
        }
    }

    fun setMdpPairs(s1: List<RegisteredPlayer>, s2: List<RegisteredPlayer>){
        mdpPairs.clear()
        s1.indices.forEach {
            mdpPairs.add(Pair(s1[it], s2[it]))
        }
    }
}