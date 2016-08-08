package io.deuxsept.dndice

import android.util.Log

/**
 * Represents a single roll, with dice type, amount of rolls and added bonus
 * Use this as if it were an immutable class until further Kotlin learning is done.
 * Do. Not. Modify. The. Fields. Of. An. Existing. Instance.
 */
public class DiceRoll {
    /**
     * Type of dice (e.g. d20, d10, d4, d<n> where <n> is <dice_type>)
     */
    public var dice_type: Int
    /**
     * Amount of rolls to do (e.g. 2d10, 4d4, <n>d6 where <n> is <dice_rolls>)
     */
    public var dice_rolls: Int
    /**
     * Bonus to add after the roll (e.g. 2d10 + 3, 4d4 + <n> where <n> is <bonus>)
     */
    public var bonus: Int

    /**
     * Build a new DiceRoll with the appropriate values.
     * Defaults to 1 roll and 0 bonus if not specified
     */
    public constructor(num_faces: Int, num_rolls: Int = 1, bonus: Int = 0) {
        this.dice_type = num_faces
        this.dice_rolls = num_rolls
        this.bonus = bonus
    }

    companion object DiceCreator {
        /**
         * Create a new DiceRoll from a String value
         * Will ignore any and all spaces
         * ex: DiceRoll.from_string("4d20 + 12")
         */
        fun from_string(value: String): DiceRoll {
            var new_dice = DiceRoll(0)

            var stripped = value.replace(" ", "")
            var left : String

            // Get the applied bonus.
            // If our string contains '+', we grab what's right of that.
            // Otherwise, set it to 0
            // We also build <left> to be able to parse the dice type more easily later
            new_dice.bonus = when(stripped.contains("+")) {
                true -> {
                    left = stripped.split("+")[0];
                    stripped.split("+")[1].toInt()
                }
                false -> {
                    left = stripped;
                    0
                }
            }

            // Get the dice type.
            // If our string contains 'd', we grab what's right of that (stripped of the bonus part if it existed)
            // Otherwise, set it to 0 (Kotlin doesn't have union types so we can't set it to an Error type)
            // Maybe check for a functional library with Option/Maybe types ?
            new_dice.dice_type = when(left.contains("d")) {
                true -> { left.split("d")[1].toInt() }
                false -> { 0 }
            }

            // Get the amount of rolls
            // Grab what's left of 'd'
            // Otherwise set to 0
            new_dice.dice_rolls = when(stripped.get(0) == 'd') {
                true -> { 0 }
                false -> { stripped.split("d")[0].toInt() }
            }

            return new_dice
        }
    }
}