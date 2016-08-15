package io.deuxsept.dndice.Utils

/**
 * Created by Luo
 * 15/08/2016.
 */
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}