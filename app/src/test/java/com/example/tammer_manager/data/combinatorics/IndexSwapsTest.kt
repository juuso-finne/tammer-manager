package com.example.tammer_manager.data.combinatorics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IndexSwapsTest {
    @Test
    fun `IndexSwaps returns correct indices`(){
        val indexPairs = mutableListOf<Pair<List<Int>, List<Int>>>()

        for(next in IndexSwaps(3, 3).iterator()){
            val indexListPair = Pair(next.first.toList(), next.second.toList())
            indexPairs.add(indexListPair)
        }

        assertThat(indexPairs).containsExactlyElementsIn(listOf(
                Pair(listOf<Int>(), listOf<Int>()),
            Pair(listOf(2), listOf(0)),
            Pair(listOf(2), listOf(1)),
            Pair(listOf(2), listOf(2)),
            Pair(listOf(1), listOf(0)),
            Pair(listOf(1), listOf(1)),
            Pair(listOf(1), listOf(2)),
            Pair(listOf(0), listOf(0)),
            Pair(listOf(0), listOf(1)),
            Pair(listOf(0), listOf(2)),
            Pair(listOf(1,2), listOf(0,1)),
            Pair(listOf(1,2), listOf(0,2)),
            Pair(listOf(1,2), listOf(1,2)),
            Pair(listOf(0,2), listOf(0,1)),
            Pair(listOf(0,2), listOf(0,2)),
            Pair(listOf(0,2), listOf(1,2)),
            Pair(listOf(0,1), listOf(0,1)),
            Pair(listOf(0,1), listOf(0,2)),
            Pair(listOf(0,1), listOf(1,2)),
            Pair(listOf(0,1,2), listOf(0,1,2))
        ))
    }
}