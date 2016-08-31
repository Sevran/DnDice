package io.deuxsept.dndice.Model

import java.util.*

/**
 * Represents a single roll, with dice type, amount of rolls and added bonus
 * Use this as if it were an immutable class until further Kotlin learning is done.
 * Do. Not. Modify. The. Fields. Of. An. Existing. Instance.
 */
class DiceRoll {
    var elements_in_roll: MutableList<IRollable> = mutableListOf()

    /**
     * If you're using this constructor for anything else than tests, you're stupid. Use from_string
     */
    constructor(data: List<IRollable>) {
        elements_in_roll = data.toMutableList()
    }

    /**
     * Override equality comparison
     * Two rolls are the same when their bonus, dice type and rollcount match
     */
    override fun equals(other: Any?): Boolean {
        return when(other) {
            is DiceRoll -> {
                other.elements_in_roll.size == this.elements_in_roll.size && other.elements_in_roll.filter { it -> !this.elements_in_roll.contains(it) }.isEmpty()
            }
            else -> { false }
        }
    }

    /**
     * Override hashcode implementation
     */
    override fun hashCode(): Int {
        return Objects.hash(this.elements_in_roll)
    }

    /**
     * Returns this roll as a liste of elements separated by '+'
     */
    override fun toString(): String {
        return "${elements_in_roll.joinToString(" + ")}"
    }

    /**
     * Returns the formula used for the roll, with dices first then bonuses and appropriate signs.
     */
    fun formula(): String {
        val prefixed = StringBuilder()
        elements_in_roll.forEach {
            item -> run {
                prefixed.append(when(item) {
                    is Dice -> (if (item.dice_rolls > 0) "+" else "") + "${item.dice_rolls}d${item.dice_type}"
                    is FlatBonus -> (if (item.bonus > 0) "+" else "") + "${item.bonus}"
                    else -> "panicpanicpanicpanic"
                })
            }
        }

        return prefixed.removePrefix("+").toString()
    }

    /**
     * Get random results for this dice roll
     * Returns a list of random values as well as the flat bonuses
     */
    fun roll(): DiceRollResult {
        val rolls: MutableList<IRollable> = mutableListOf()

        elements_in_roll.forEach {
            item -> run { item.roll(); rolls.add(item) }
        }

        return DiceRollResult(rolls, formula())
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
            val accumulator = StringBuilder()
            val elements: MutableList<IRollable> = mutableListOf()
            var chars_parsed = 0
            var trigger_parse = false
            val stripped = value.replace(" ", "")

            stripped.forEach {
                char -> run {
                    if (char == '+' || char == '-') {
                        trigger_parse = true
                    }
                    // If we reach the last character, add it to parse
                    if (chars_parsed+1 == stripped.length && !(char == '+' || char == '-')) {
                        accumulator.append(char)
                        trigger_parse = true
                    }

                    if (trigger_parse) {
                        val parsed: IRollable = if (accumulator.contains('d')) interpret_dice(accumulator.toString()) else interpret_bonus(accumulator.toString())

                        // Only add the rolls if they're non zero
                        when(parsed) {
                            is FlatBonus -> if(parsed.bonus != 0) elements.add(parsed)
                            is Dice      -> if(parsed.dice_rolls != 0) elements.add(parsed)
                        }
                        trigger_parse = false
                        accumulator.setLength(0)
                    }

                    accumulator.append(char)
                    chars_parsed++
                }
            }

            return DiceRoll(elements)
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
            val max = when(splitted.size) {
                1 -> { splitted[0].toInt() }
                else -> { splitted[1].toInt() }
            }
            return Dice(rolls , max)
        }

        /**
         * Interpret <value> as a bonus
         */
        private fun interpret_bonus(value: String): FlatBonus {
            return when(value.length) {
                1 -> if (value[0] == '+' || value[0] == '-') FlatBonus(0) else FlatBonus(value.toInt())
                else -> FlatBonus(value.toInt())
            }
        }
    }
}