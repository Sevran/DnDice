package io.deuxsept.dndice.Model

/**
 * Created by Luo
 * 11/08/2016.
 */

data class RollModel(var formula: String = "",
                     var result: String = "",
                     var detail: String = "",
                     var id: Int = 0,
                     var timestamp: Long = 0,
                     var fav: Boolean = false)