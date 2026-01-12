package com.example.tammer_manager.data.combinatorics

/**
 * All possible index combinations of length [k] of a list whose length is [listLength], in lexicographic order
 */
class IndexCombinationSequence (private val listLength: Int, private val k: Int): Iterable<IntArray>{
    override fun iterator(): Iterator<IntArray> {
        return object: Iterator<IntArray>{
            override fun next(): IntArray {
                TODO("Not yet implemented")
            }

            override fun hasNext(): Boolean {
                TODO("Not yet implemented")
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
            override fun next(): IntArray {
                TODO("Not yet implemented")
            }

            override fun hasNext(): Boolean {
                TODO("Not yet implemented")
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
            override fun next(): Pair<IntArray, IntArray> {
                TODO("Not yet implemented")
            }

            override fun hasNext(): Boolean {
                TODO("Not yet implemented")
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
    TODO("Not yet implemented")
}