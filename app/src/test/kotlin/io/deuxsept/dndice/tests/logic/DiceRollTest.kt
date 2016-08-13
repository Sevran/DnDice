package io.deuxsept.dndice.tests.logic

import io.deuxsept.dndice.DiceLogic.Dice
import io.deuxsept.dndice.DiceLogic.DiceRoll
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for DiceRoll
 */
class DiceRollTest {
    // Default test dice, 2d10 + 3
    private val default_dice : DiceRoll = DiceRoll(listOf(Dice(2,10)), listOf(3))

    @Test
    fun EqualityTest() {
        val copy_dice = DiceRoll.from_string("2d10 + 3")
        Assert.assertTrue("equals returned false while both are the same ($default_dice | $copy_dice)", default_dice == copy_dice);

        val wrong_copy_dice_rollcount = DiceRoll.from_string("3d10 + 3")
        Assert.assertTrue("equals returned true while rollcounts are different", default_dice != wrong_copy_dice_rollcount)

        val wrong_copy_dice_type = DiceRoll.from_string("2d20 + 3")
        Assert.assertTrue("equals returned true while types are different", default_dice != wrong_copy_dice_type)

        val wrong_copy_dice_bonus = DiceRoll.from_string("2d10 + 1")
        Assert.assertTrue("equals returned true while bonuses are different", default_dice != wrong_copy_dice_bonus)
    }

    @Test
    fun FromStringTest() {
        var parsed_dice : DiceRoll = DiceRoll.from_string("2d10 + 3")
        Assert.assertTrue("Parsed dice does not have the expected value ($default_dice | $parsed_dice)", default_dice == parsed_dice)

        // from_string is supposed to handle any spacing issues
        parsed_dice = DiceRoll.from_string(" 2 d 10 +     3")
        Assert.assertTrue("Parsed dice does not have the expected value ($default_dice | $parsed_dice)", default_dice == parsed_dice)

        // "fuck you" tests -- Things that should rarely happen but still do. Let's ensure we handle those correctly
        var hard_dice = DiceRoll(listOf(Dice(2,10), Dice(5,20), Dice(-2, 4)), listOf(3, 8, -5))
        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 + 8 - 5 - 2d4")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice == parsed_dice)

        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 + 8 - 5 + 2d4")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice != parsed_dice)

        // Try empty symbols
        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 + 8 - 5 - 2d4 +")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice == parsed_dice)

        parsed_dice = DiceRoll.from_string("2d10 + 3 + 5d20 +++-+ 8 - 5 - 2d4 +")
        Assert.assertTrue("Parsed dice does not have the expected value ($hard_dice | $parsed_dice).", hard_dice == parsed_dice)
    }

    @Test
    fun RollTest() {
        var not_random_roll = DiceRoll.from_string("2")
        Assert.assertTrue("Roll should only have one element, got ${not_random_roll.roll().size}", not_random_roll.roll().size == 1)
        Assert.assertTrue("Roll should have returned 2, got ${not_random_roll.roll().sum()}", not_random_roll.roll().sum() == 2)

        var random_unique_roll = DiceRoll.from_string("1d1")
        Assert.assertTrue("Roll should only have one element, got ${random_unique_roll.roll().size}", random_unique_roll.roll().size == 1)
        Assert.assertTrue("Roll should have returned 1, got ${random_unique_roll.roll().sum()}", random_unique_roll.roll().sum() == 1)

        var no_roll = DiceRoll.from_string("0d20")
        Assert.assertTrue("Roll should only have one element, got ${no_roll.roll().size}", no_roll.roll().size == 0)
        Assert.assertTrue("Roll should have returned 1, got ${no_roll.roll().sum()}", no_roll.roll().sum() == 0)
    }
}