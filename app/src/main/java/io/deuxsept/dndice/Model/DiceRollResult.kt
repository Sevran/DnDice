package io.deuxsept.dndice.Model

/**
 * Created by Flo
 * 10/08/2016.
 */
class DiceRollResult {
    val results: List<IRollable>

    constructor(data: List<IRollable>){
        results = data
    }

    /**
     * Get the dice roll as a human presentable string
     */
    fun as_readable_string(): String {
        return results.joinToString {
            item -> item.results_as_string()
        }
    }

    /**
     * Returns the total from the rolls
     */
    fun as_total(): Int {
        return this.results.sumBy { item -> item.results.sum() }
    }
}