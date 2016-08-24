package io.deuxsept.dndice.tests.logic

import io.deuxsept.dndice.Model.Dice
import io.deuxsept.dndice.Model.DiceRoll
import io.deuxsept.dndice.Model.FlatBonus
import org.junit.Assert
import org.junit.Test

/**
 * Tests for DiceRoll
 */
class DiceRollTest {
    // Default test dice, 2d10 + 3
    private val default_dice : DiceRoll = DiceRoll(listOf(Dice(2,10), FlatBonus(3)))

    @Test
    fun EqualityTest() {
        val copy_dice = DiceRoll.from_string("2d10 + 3")
    }

    @Test
    fun FromStringTest() {
        var parsed_dice : DiceRoll = DiceRoll.from_string("2d10 + 3")
        Assert.assertTrue("Parsed dice does not have the expected value ($default_dice | $parsed_dice)", default_dice.formula() == parsed_dice.formula())

        // from_string is supposed to handle any spacing issues
        parsed_dice = DiceRoll.from_string(" 2 d 10 +     3")
        Assert.assertTrue("Parsed dice does not have the expected value ($default_dice | $parsed_dice)", default_dice.formula() == parsed_dice.formula())

        // "fuck you" tests -- Things that should rarely happen but still do. Let's ensure we handle those correctly
        val hard_dice = DiceRoll(listOf(Dice(2,10), FlatBonus(3), Dice(5,20), FlatBonus(8), FlatBonus(-5),Dice(-2, 4)))
        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 + 8 - 5 - 2d4")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice.formula() == parsed_dice.formula())

        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 + 8 - 5 + 2d4")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice.formula() != parsed_dice.formula())

        // Try empty symbols
        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 + 8 - 5 - 2d4 +")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice.formula() == parsed_dice.formula())

        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 +++-+ 8 - 5 - 2d4 +")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice.formula() == parsed_dice.formula())

        parsed_dice = DiceRoll.from_string("d10+3")
    }

    @Test
    fun RollTest() {
        var not_random_roll = DiceRoll.from_string("2")
        //Assert.assertTrue("Roll should only have one element, got ${not_random_roll.roll().size}", not_random_roll.roll().size == 1)
        //Assert.assertTrue("Roll should have returned 2, got ${not_random_roll.roll().sum()}", not_random_roll.roll().sum() == 2)

        var random_unique_roll = DiceRoll.from_string("1d1")
        //Assert.assertTrue("Roll should only have one element, got ${random_unique_roll.roll().size}", random_unique_roll.roll().size == 1)
        //Assert.assertTrue("Roll should have returned 1, got ${random_unique_roll.roll().sum()}", random_unique_roll.roll().sum() == 1)

        var no_roll = DiceRoll.from_string("0d20")
        //Assert.assertTrue("Roll should only have one element, got ${no_roll.roll().size}", no_roll.roll().size == 0)
        //Assert.assertTrue("Roll should have returned 1, got ${no_roll.roll().sum()}", no_roll.roll().sum() == 0)
    }
}
