package com.example.tammer_manager.data.combinatorics

/**
 * Removes the element at index [i], appends it to the end of the list,
 * and checks whether the element now at position [i] is bigger than
 * the last element.
 */
fun <T: Comparable<T>> popAndShift(list: MutableList<T>, i: Int):Boolean{
    list.removeAt(i).also { list.add(it) }
    return list[i] > list.last()
}

/**
 * Swaps the element at [startIndex] with the first element to its right
 * that is strictly bigger than it.
 */
fun <T: Comparable<T>> swapWithFirstLarger(list: MutableList<T>, startIndex: Int): Boolean{
    val firstItem = list[startIndex]
    for(i in startIndex + 1..<list.size){
        if(list[i] > firstItem){
            list[startIndex] = list[i]
            list[i] = firstItem
            return true
        }
    }
    return false
}

/**
 * Advances [list] in-place to the next permutation according to the
 * algorithm’s internal ordering rules.
 *
 * ### Preconditions
 * - The first [length] elements of [list] **must be sorted in strictly
 *   ascending order** before the first invocation.
 * - The contents of [list] **must not be modified externally** between
 *   successive calls.
 * - Only indices in the range `0 until length` may be accessed or
 *   interpreted by the caller.
 *
 * ### Undefined behavior
 * The behavior of this function is undefined if any of the preconditions
 * are violated, including (but not limited to):
 * - The input list is not initially sorted in ascending order
 * - Elements within the permutation prefix are modified between calls
 * - Indices outside the permutation prefix are accessed or relied upon
 *
 * @param list the mutable list whose permutation is advanced in-place
 * @param changedIndices cleared and populated with indices affected
 *        during this permutation step
 * @param length the size of the permutation prefix
 * @return `true` if a new permutation was generated, or `false` if the
 *         final permutation has been reached
 */
fun <T: Comparable<T>> nextPermutation (list: MutableList<T>, changedIndices: MutableList<Int>, length: Int = list.size): Boolean {
    if (length <= 0){
        return false
    }
    changedIndices.clear()
    val cutOff = length - 1
    changedIndices.add(cutOff)
    if (popAndShift(list, cutOff)){
        return true
    }
    for (i in cutOff - 1 downTo 0){
        changedIndices.add(i)
        if (swapWithFirstLarger(list, i)){
            return true
        }

        popAndShift(list, i)
    }
    return false
}