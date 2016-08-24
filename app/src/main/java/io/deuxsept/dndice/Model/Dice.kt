package io.deuxsept.dndice.Model

import java.util.*

/**
 * Created by Flo
 * 09/08/2016.
 */
class Dice: IRollable {
    /**
     * Type of dice (e.g. d20, d10, d4, d<n> where <n> is <dice_type>)
     */
    var dice_type: Int
    /**
     * Amount of rolls to do (e.g. 2d10, 4d4, <n>d6 where <n> is <dice_rolls>)
     */
    var dice_rolls: Int
    /**
     * Bonus to add after the roll (e.g. 2d10 + 3, 4d4 + <n> where <n> is <bonus>)
     */

    constructor(dice_rolls: Int, dice_type: Int) {
        this.dice_rolls = dice_rolls
        this.dice_type = dice_type
    }

    override fun toString(): String {
        return "${dice_rolls}d$dice_type"
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is Dice -> { this.dice_rolls == other.dice_rolls && this.dice_type == other.dice_type }
            else -> { false }
        }
    }

    override fun roll(): List<Int> {
        val rng: Random = Random()

        val rolls_for_item: MutableList<Int> = mutableListOf()
        for (i in 0..Math.abs(dice_rolls) - 1) {
            rolls_for_item.add((rng.nextInt(dice_type) + 1) * Integer.signum(dice_rolls))
        }

        return rolls_for_item
    }
}