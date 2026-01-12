package com.example.tammer_manager.data.combinatorics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IndexSwapsTest {
    @Test
    fun `IndexSwaps returns correct indices`(){
        val indexPairs = mutableListOf<Pair<IntArray, IntArray>>()

        for(next in IndexSwaps(3, 3).iterator()){
            val indexListPair = Pair(next.first.copyOf(), next.second.copyOf())
            indexPairs.add(indexListPair)
        }

        assertThat(indexPairs).containsExactly(
            Pair(intArrayOf(0), intArrayOf(2)),
            Pair(intArrayOf(0), intArrayOf(1)),
            Pair(intArrayOf(0), intArrayOf(0)),
            Pair(intArrayOf(1), intArrayOf(2)),
            Pair(intArrayOf(1), intArrayOf(1)),
            Pair(intArrayOf(1), intArrayOf(0)),
            Pair(intArrayOf(2), intArrayOf(2)),
            Pair(intArrayOf(2), intArrayOf(1)),
            Pair(intArrayOf(2), intArrayOf(0)),
            Pair(intArrayOf(0,1), intArrayOf(2,1)),
            Pair(intArrayOf(0,1), intArrayOf(2,0)),
            Pair(intArrayOf(0,1), intArrayOf(1,0)),
            Pair(intArrayOf(0,2), intArrayOf(2,1)),
            Pair(intArrayOf(0,2), intArrayOf(2,0)),
            Pair(intArrayOf(0,2), intArrayOf(1,0)),
            Pair(intArrayOf(1,2), intArrayOf(2,1)),
            Pair(intArrayOf(1,2), intArrayOf(2,0)),
            Pair(intArrayOf(1,2), intArrayOf(1,0)),
            Pair(intArrayOf(0,1,2), intArrayOf(2,1,0)),
        )
    }
}