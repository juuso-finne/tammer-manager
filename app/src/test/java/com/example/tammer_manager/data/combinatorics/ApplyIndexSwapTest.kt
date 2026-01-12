package com.example.tammer_manager.data.combinatorics

import com.google.common.truth.Truth
import org.junit.Test

class ApplyIndexSwapTest {
    @Test
    fun `Index swapping is performed correctly`(){
        val a = mutableListOf(1,2,3)
        val b = mutableListOf(7,8,9)

        applyIndexSwap(a, b, Pair(intArrayOf(0,2), intArrayOf(1,2)))
        Truth.assertThat(a).containsExactly(7,8,2)
        Truth.assertThat(b).containsExactly(1,3,9)
    }
}