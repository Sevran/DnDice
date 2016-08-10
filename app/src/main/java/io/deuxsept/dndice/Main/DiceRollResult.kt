package io.deuxsept.dndice.Main

/**
 * Created by Flo
 * 10/08/2016.
 */
class DiceRollResult {
    val results: List<List<Int>>

    constructor(data: List<List<Int>>){
        results = data
    }

    /**
     * Get the dice roll as a human presentable string
     */
    fun as_readable_string(): String {
        return results.joinToString {
            item -> run {
                when(item.size){
                    1 -> item[0].toString()
                    else -> item.toString()
                }
            }
        }
    }

    /**
     * Returns the total from the rolls
     */
    fun as_total(): Int {
        return results.sumBy { item -> item.sum() }
    }
}