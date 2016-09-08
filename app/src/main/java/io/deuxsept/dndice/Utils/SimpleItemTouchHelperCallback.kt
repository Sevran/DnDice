package io.deuxsept.dndice.Utils

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import io.deuxsept.dndice.Adapter.FavoriteAdapter

/**
 * Created by Luo
 * on 15/08/2016.
 */
class SimpleItemTouchHelperCallback(private val mAdapter: FavoriteAdapter) : ItemTouchHelper.Callback() {
    var dragFrom = -1
    var dragTo = -1

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if (dragFrom === -1) {
            dragFrom = fromPosition
        }
        dragTo = toPosition

        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (dragFrom !== -1 && dragTo !== -1 && dragFrom !== dragTo) {
            mAdapter.onItemMoved(dragFrom, dragTo)
        }
        dragTo = -1
        dragFrom = dragTo -1

    }
}