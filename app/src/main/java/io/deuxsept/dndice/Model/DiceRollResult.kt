package io.deuxsept.dndice.Model

import io.deuxsept.dndice.Utils.LastRollInfo

/**
 * Created by Flo
 * 10/08/2016.
 */
public class DiceRollResult {
    val results: List<IRollable>
    val formula: String

    constructor(data: List<IRollable>, formula: String){
        results = data
        this.formula = formula
    }

    fun formula(): String {
        return formula
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

    fun as_total_string(): String {
        var total = as_total()
        return if (total == 0) "" else total.toString()
    }

    fun has_fumble(): Boolean {
        return results.filter { item -> item.results.contains(1) }.isNotEmpty()
    }

    fun has_critical(): Boolean {
        return results.filter {item -> item is Dice && item.results.contains(item.dice_type)}.isNotEmpty()
    }

    fun roll_info(): LastRollInfo {
        val fumble = has_fumble()
        val crit = has_critical()

        return (if (fumble && crit) LastRollInfo.Both
                else if (fumble)    LastRollInfo.Fumble
                else if (crit)      LastRollInfo.Critical
                else                LastRollInfo.Normal)
    }
}