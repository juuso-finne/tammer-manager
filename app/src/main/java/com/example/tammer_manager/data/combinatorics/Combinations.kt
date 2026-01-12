package com.example.tammer_manager.data.combinatorics

import kotlin.math.min

/**
 * All possible index combinations of length [k] of a list whose length is [listLength], in lexicographic order
 */
class IndexCombinationSequence (private val listLength: Int, private val k: Int): Iterable<IntArray>{
    override fun iterator(): Iterator<IntArray> {
        return object: Iterator<IntArray>{
            val lengthDifference = listLength - k
            var current: IntArray? = null
            var hasNext =
                k >= 0 &&
                        listLength > 0 &&
                        k <= listLength

            override fun next(): IntArray {
                if (!hasNext) throw NoSuchElementException()

                if(current == null){
                    current = IntArray(k){it}
                    hasNext = k != listLength && k != 0
                    return current!!
                }

                for (i in current!!.lastIndex downTo 0){
                    if (current!![i] - i >= lengthDifference){
                        continue
                    }

                    current!![i]++

                    for(offset in 0 until k - i){
                        current!![i + offset] = current!![i] + offset
                    }
                    hasNext = i > 0 || current!!.first() < lengthDifference
                    return current!!
                }

                hasNext = false
                return current!!
            }

            override fun hasNext(): Boolean {
                return hasNext
            }
        }
    }
}

/**
 * All possible index combinations of length [k] of a list whose length is [listLength], in colexicographic order
 */
class ReverseIndexCombinationSequence (private val listLength: Int, private val k: Int): Iterable<IntArray>{
    override fun iterator(): Iterator<IntArray> {
        return object: Iterator<IntArray>{
            val lengthDifference = listLength - k
            var current: IntArray? = null
            var hasNext =
                k >= 0 &&
                        listLength > 0 &&
                        k <= listLength

            override fun next(): IntArray {
                if (!hasNext) throw NoSuchElementException()

                if(current == null){
                    current = IntArray(k){it + lengthDifference}
                    hasNext = k != listLength && k != 0
                    return current!!
                }

                for (i in current!!.lastIndex downTo 0){

                    if ( i == 0 || current!![i] - current!![i-1] > 1){
                        current!![i]--

                        for (j in i + 1 until k){
                            current!![j] = lengthDifference + j
                        }

                        hasNext = current!!.last() >= k || current!!.first() > 0
                        return current!!
                    }
                }

                hasNext = false
                return current!!
            }

            override fun hasNext(): Boolean {
                return hasNext
            }
        }
    }
}

/**
 * Lazily iterates over all valid index-swap combinations between two
 * sequences of sizes [sizeS1] and [sizeS2].
 *
 * Each element produced is a pair of integer arrays:
 * - the first array contains indices into the first sequence (S1)
 * - the second array contains corresponding indices into the second sequence (S2)
 *
 * Both arrays are guaranteed to have the same length `k`, where
 * `0 ≤ k ≤ min(sizeS1, sizeS2)`. Each emitted pair represents a
 * *k-exchange* between S1 and S2.
 *
 * ### Iteration order
 * The iterator progresses in increasing exchange size `k`, starting at `0`
 * and ending at `min(sizeS1, sizeS2)`. For each `k`:
 *
 * 1. All index combinations of size `k` from S1 are generated in reverse
 *    index order
 * 2. For each such S1 combination, all index combinations of size `k`
 *    from S2 are generated in forward index order
 *
 * This ordering is deterministic and stable, making it suitable for
 * exhaustive but interruptible search.
 *
 * ### Usage
 * The produced index pairs are intended to be applied via [applyIndexSwap].
 *
 * ### Notes
 * - The iterator is stateful and not thread-safe
 * - All arrays returned are reused only for the duration of a single
 *   iteration step and must not be mutated
 *
 * @param sizeS1 size of the first sequence
 * @param sizeS2 size of the second sequence
 */
class IndexSwaps (private val sizeS1: Int, private val sizeS2: Int): Iterable<Pair<IntArray, IntArray>>{
    override fun iterator(): Iterator<Pair<IntArray, IntArray>> {
        return object: Iterator<Pair<IntArray, IntArray>>{
            var i = 0
            val max = min(sizeS1, sizeS2)

            var s1Iterator: Iterator<IntArray> = ReverseIndexCombinationSequence(sizeS1, i).iterator()
            var s2Iterator: Iterator<IntArray> = IndexCombinationSequence(sizeS2, i).iterator()
            var currentS1: IntArray = s1Iterator.next()

            var nextPair: Pair<IntArray, IntArray>? = null

            override fun hasNext(): Boolean {
                if (nextPair != null) return true

                while (true) {

                    // 1. Can we emit another s2 for current s1?
                    if (s2Iterator.hasNext()) {
                        nextPair = Pair(currentS1, s2Iterator.next())
                        return true
                    }

                    // 2. Can we advance s1?
                    if (s1Iterator.hasNext()) {
                        currentS1 = s1Iterator.next()
                        s2Iterator = IndexCombinationSequence(sizeS2, i).iterator()
                        continue
                    }

                    // 3. Can we advance i?
                    i++
                    if (i > max) return false


                    s1Iterator = ReverseIndexCombinationSequence(sizeS1, i).iterator()
                    if (!s1Iterator.hasNext()) {
                        continue
                    }

                    currentS1 = s1Iterator.next()
                    s2Iterator = IndexCombinationSequence(sizeS2, i).iterator()
                }
            }

            override fun next(): Pair<IntArray, IntArray> {
                if (!hasNext()) throw NoSuchElementException()
                val result = nextPair!!
                nextPair = null
                return result
            }
        }
    }
}

/**
 * Applies an index-based exchange between two mutable lists in-place.
 *
 * For each position `i` in the provided index arrays, the elements at
 * `s1[indexListPair.first[i]]` and `s2[indexListPair.second[i]]` are swapped.
 *
 * This function performs a *pairwise exchange* and assumes that both index
 * arrays are of equal length and aligned by position.
 *
 * ### Preconditions
 * - Both index arrays must have the same length
 * - All indices must be valid for their respective lists
 * - The index pair must originate from [IndexSwaps] for correctness
 *
 * ### Undefined behavior
 * Behavior is undefined if any index is out of bounds or if the index
 * arrays are not aligned.
 *
 * @param s1 the first mutable list
 * @param s2 the second mutable list
 * @param indexListPair a pair of aligned index arrays describing the swap
 */
fun <T> applyIndexSwap (s1: MutableList<T>, s2: MutableList<T>, indexListPair: Pair<IntArray, IntArray>){
    val s1Swaps = indexListPair.first
    val s2Swaps = indexListPair.second

    for (i in 0 until s1Swaps.size) {
        val temp = s1[s1Swaps[i]]
        s1[s1Swaps[i]] = s2[s2Swaps[i]]
        s2[s2Swaps[i]] = temp
    }
}