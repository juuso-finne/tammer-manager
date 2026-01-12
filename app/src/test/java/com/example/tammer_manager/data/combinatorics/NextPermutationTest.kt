package com.example.tammer_manager.data.combinatorics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NextPermutationTest {
    @Test
    fun `Elements are in the expected order after calling NextPermutation`(){
        val a = mutableListOf("G", "F", "E", "D", "C", "B", "A")
        nextPermutation(a, mutableListOf())
        assertThat(a).isEqualTo(mutableListOf ("G", "F", "E", "D", "C", "A", "B"))

        val b = mutableListOf(5,4,3,2,1)
        nextPermutation(b, mutableListOf(), 3)
        assertThat(b.subList(0,3)).isEqualTo(mutableListOf(5,4,2))

        nextPermutation(b, mutableListOf(), 3)
        assertThat(b.subList(0,3)).isEqualTo(mutableListOf(5,4,1))
    }

    @Test
    fun `Changed elements are identified correctly`(){
        val a = mutableListOf("G", "F", "E", "D", "C", "B", "A")
        val changedIndices = mutableListOf<Int>()

        nextPermutation(a, changedIndices)
        assertThat(changedIndices).containsExactly(5,6)

        val b = mutableListOf(5,4,3,2,1)
        nextPermutation(b, mutableListOf(), 3)
        assertThat(changedIndices).containsExactly(2)

    }
}