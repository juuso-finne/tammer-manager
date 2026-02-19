package com.example.tammer_manager.data.combinatorics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NextPermutationTest {
    @Test
    fun `Elements are in the expected order after calling NextPermutation`(){
        val a = mutableListOf("A", "B", "C", "D", "E", "F", "G")
        nextPermutation(a, mutableListOf())
        assertThat(a).isEqualTo(mutableListOf ("A", "B", "C", "D", "E", "G", "F"))

        val b = mutableListOf(1,2,3,4,5)
        nextPermutation(b, mutableListOf(), 3)
        assertThat(b.subList(0,3)).isEqualTo(mutableListOf(1,2,4))

        nextPermutation(b, mutableListOf(), 3)
        assertThat(b.subList(0,3)).isEqualTo(mutableListOf(1,2,5))
    }

    @Test
    fun `Changed elements are identified correctly`(){
        val a = mutableListOf("A", "B", "C", "D", "E", "F", "G")
        val changedIndices = mutableListOf<Int>()

        nextPermutation(a, changedIndices)
        assertThat(changedIndices).containsExactly(5,6)

        val b = mutableListOf(1,2,3,4,5)
        nextPermutation(b, changedIndices, 3)
        assertThat(changedIndices).containsExactly(2)
    }

    @Test
    fun `Returns false after last possible permutation`(){
        val a = mutableListOf("G", "F", "E", "D", "C", "B", "A")
        assertThat(nextPermutation(a, mutableListOf())).isFalse()
    }

    @Test
    fun `Permutation skip puts the list in correct order` (){
        val a = mutableListOf("A", "B", "D", "F", "E", "G", "C")
        setupPermutationSkip(a, 2, 5)

        assertThat(a).isEqualTo(
            mutableListOf("A", "B", "D", "G", "F", "C", "E")
        )

        nextPermutation(a, mutableListOf(), 5)

        assertThat(a).isEqualTo(
            mutableListOf("A", "B", "E", "C", "D", "F", "G")
        )
    }
}