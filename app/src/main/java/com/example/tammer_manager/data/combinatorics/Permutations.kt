package com.example.tammer_manager.data.combinatorics

fun <T: Comparable<T>> popAndShift(list: MutableList<T>, i: Int):Boolean{
    list.removeAt(i).also { list.add(it) }
    return list[i] < list.last()
}


fun <T: Comparable<T>> swapWithFirstBigger(list: MutableList<T>, startIndex: Int): Boolean{
    val firstItem = list[startIndex]
    for(i in startIndex + 1..<list.size){
        if(list[i] < firstItem){
            list[startIndex] = list[i]
            list[i] = firstItem
            return true
        }
    }
    return false
}

fun <T: Comparable<T>> nextPermutation (list: MutableList<T>, changedIndices: MutableList<Int>, length: Int = list.size): Boolean {
    changedIndices.clear()
    val cutOff = length - 1
    changedIndices.add(cutOff)
    if (popAndShift(list, cutOff)){
        return true
    }
    for (i in cutOff - 1 downTo 0){
        changedIndices.add(i)
        if (swapWithFirstBigger(list, i)){
            return true
        }

        popAndShift(list, i)
    }
    return false
}