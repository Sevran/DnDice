package io.deuxsept.dndice.Model

/**
 * Created by Flo on 13/08/2016.
 */
class FlatBonus: IRollable {
    val bonus: Int

    constructor(value: Int) {
        bonus = value
    }

    override fun toString(): String {
        return bonus.toString()
    }

    override fun roll(): List<Int> {
        return listOf(bonus)
    }
}