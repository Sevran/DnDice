package io.deuxsept.dndice.Model

/**
 * Created by Flo on 13/08/2016.
 */
class FlatBonus: IRollable {
    override var results: MutableList<Int>
    val bonus: Int

    constructor(value: Int) {
        bonus = value
        this.results = mutableListOf()
    }

    override fun toString(): String {
        return bonus.toString()
    }

    override fun roll(): List<Int> {
        this.results.clear()
        this.results.add(bonus)
        return listOf(bonus)
    }

    override fun results_as_string(): String {
        return bonus.toString()
    }
}