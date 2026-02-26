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
 * @param length the size of the permutation prefix
 * @return `true` if a new permutation was generated, or `false` if the
 *         final permutation has been reached
 */
fun <T: Comparable<T>> nextPermutation (
    list: MutableList<T>,
    length: Int = list.size
): Boolean {
    if (length <= 0){
        return false
    }
    val cutOff = length - 1
    if (popAndShift(list, cutOff)){
        return true
    }
    for (i in cutOff - 1 downTo 0){
        if (swapWithFirstLarger(list, i)){
            return true
        }

        popAndShift(list, i)
    }
    return false
}

/**
 * Mutates [list] in a way that calling [nextPermutation] will replace the element
 * at index [i] with the next biggest element and leave the rest in ascending order.
 *
 * @param list the list to be mutated
 * @param i the index of the element to be replaced
 * @param length the size of the permutation prefix
 */
fun <T: Comparable<T>> setupPermutationSkip(
    list: MutableList<T>,
    i: Int,
    length: Int = list.size
){
    if (length == i + 1){
        return
    }
    list.subList(i + 1, list.size).sortDescending()
    list.subList(length, list.size).sort()
}