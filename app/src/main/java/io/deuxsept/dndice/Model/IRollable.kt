package io.deuxsept.dndice.Model

/**
 * Created by Flo on 13/08/2016.
 */
interface IRollable {
    fun roll(): List<Int>
}