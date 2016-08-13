package io.deuxsept.dndice.DiceLogic

/**
 * Created by Flo
 * 09/08/2016.
 */
class Dice {
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
        return "${dice_rolls}d${dice_type}"
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is Dice -> { this.dice_rolls == other.dice_rolls && this.dice_type == other.dice_type }
            else -> { false }
        }
    }
}