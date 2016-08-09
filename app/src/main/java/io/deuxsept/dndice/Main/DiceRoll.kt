package io.deuxsept.dndice.Main

import java.util.*

/**
 * Represents a single roll, with dice type, amount of rolls and added bonus
 * Use this as if it were an immutable class until further Kotlin learning is done.
 * Do. Not. Modify. The. Fields. Of. An. Existing. Instance.
 */
class DiceRoll {
    var bonus_list: MutableList<Int> = mutableListOf()
    var dice_list: MutableList<Dice> = mutableListOf()

    /**
     * If you're using this constructor for anything else than tests, you're stupid. Use from_string
     */
    constructor(dices: List<Dice>, bonuses: List<Int>) {
        dice_list = dices.toMutableList()
        bonus_list = bonuses.toMutableList()
    }

    /**
     * You better have a good reason to use this constructor, I swear. Use from_string
     */
    constructor() { }

    /**
     * Override equality comparison
     * Two rolls are the same when their bonus, dice type and rollcount match
     */
    override fun equals(other: Any?): Boolean {
        return when(other) {
            is DiceRoll -> {
                (other.bonus_list.size == this.bonus_list.size && other.bonus_list.filter { !this.bonus_list.contains(it) }.isEmpty()) &&
                        (other.dice_list.size == this.dice_list.size && other.dice_list.filter { !this.dice_list.contains(it) }.isEmpty())
            }
            else -> { false }
        }
    }

    /**
     * Override hashcode implementation
     */
    override fun hashCode(): Int {
        return Objects.hash(this.bonus_list, this.dice_list)
    }

    /**
     * Returns this roll as a liste of elements separated by '+'
     */
    override fun toString(): String {
        return dice_list.joinToString(" + ") + " + " + bonus_list.joinToString(" + ")
    }

    /**
     * Returns the formula used for the roll, with dices first then bonuses and appropriate signs.
     */
    fun formula(): String {
        return "${dice_list.joinToString(" + ")} + ${bonus_list.joinToString(" + ")}"
    }

    /**
     * Get random results for this dice roll
     * Returns a list of random values as well as the flat bonuses
     */
    fun roll(): DiceRollResult {
        var rng: Random = Random()
        var rolls: MutableList<List<Int>> = mutableListOf()

        dice_list.forEach {
            item -> run {
                var rolls_for_item: MutableList<Int> = mutableListOf()
                for (i in 0..Math.abs(item.dice_rolls) - 1) {
                    rolls_for_item.add((rng.nextInt(item.dice_type) + 1) * Integer.signum(item.dice_rolls))
                }
                rolls.add(rolls_for_item)
            }
        }

        bonus_list.forEach {
            item -> run {
                var rolls_for_item: MutableList<Int> = mutableListOf()
                rolls_for_item.add(item)
                rolls.add(rolls_for_item)
            }
        }

        return DiceRollResult(rolls)
    }

    /**
     * Utilities to create a new dice roll
     */
    companion object DiceCreator {
        /**
         * Create a new DiceRoll from a String value
         * Will ignore any and all spaces
         * ex: DiceRoll.from_string("4d20 + 12")
         */
        fun from_string(value: String): DiceRoll {
            val dices : MutableList<Dice> = mutableListOf()
            val bonuses : MutableList<Int> = mutableListOf()

            val accumulator = StringBuilder()
            val allowed_symbols = arrayOf('+', '-')

            var is_dice: Boolean = false
            var is_bonus: Boolean = true
            var trigger_parse: Boolean = false

            val stripped = value.replace(" ", "")
            // Parsey fun time
            // character by character, we add them into an accumulator, activating or disactivating
            // flags with the characters we find
            // finding a 'd' will trigger the is_dice flag
            // finding a '+' or '-' will trigger the trigger_parse flag, which tells us to interpret
            // what we got and clear everything back to default values
            for (i in 0..stripped.length - 1) {
                accumulator.append(stripped[i])

                // We can't lookahead for the very last character, so we just automatically
                // trigger the trigger_parse flag when we reach it
                if (i+1 < stripped.length) {
                    when(stripped[i+1]) {
                        'd' -> {
                            is_dice = true
                            is_bonus = false
                        }
                        in allowed_symbols -> {
                            trigger_parse = true
                        }
                    }
                }
                else { trigger_parse = true }

                // Fill out the appropriate list for the dice, and reset everything
                if (trigger_parse) {
                    if (is_dice) {
                        dices.add(interpret_dice(accumulator.toString()))
                    }
                    if (is_bonus) {
                        val parsed = interpret_bonus(accumulator.toString())
                        when(parsed) {
                            null -> {}
                            else -> { bonuses.add(parsed) }
                        }
                    }
                    accumulator.setLength(0) // clear the accumulator
                    is_dice = false
                    is_bonus = true
                    trigger_parse = false
                }
            }

            return DiceRoll(dices, bonuses)
        }

        /**
         * Interpret <value> as a dice
         * ex: "2d10" will return a Dice(2, 10)
         */
        private fun interpret_dice(value: String): Dice {
            val splitted = value.split('d')
            // 'd100' is a perfectly valid value, we just reinterpret that as 1d100
            val rolls = when(splitted[0].length) {
                0 -> { 1 }
                1 -> { if (splitted[0] == "-") -1
                       else if (splitted[0] == "+") 1
                       else splitted[0].toInt() }
                else -> { splitted[0].toInt() }
            }
            return Dice(rolls , splitted[1].toInt())
        }

        /**
         * Interpret <value> as a bonus
         */
        private fun interpret_bonus(value: String): Int? {
            return when(value.length) {
                0 -> null
                1 -> if (value[0] == '+' || value[0] == '-') null else value.toInt()
                else -> value.toInt()
            }
        }
    }
}