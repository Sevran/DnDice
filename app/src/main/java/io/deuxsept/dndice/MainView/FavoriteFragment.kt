package io.deuxsept.dndice.MainView

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.deuxsept.dndice.Adapter.FavoriteAdapter
import io.deuxsept.dndice.Database.DatabaseHelper
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.SimpleItemTouchHelperCallback
import io.deuxsept.dndice.Utils.SwipeableRecyclerViewTouchListener

/**
 * Created by Luo
 * on 15/08/2016.
 */
class FavoriteFragment : Fragment(), FavoriteAdapter.OnDragStartListener {

    lateinit var mDb: DatabaseHelper
    lateinit var mNoneView: View
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: FavoriteAdapter
    private lateinit var mItemTouchHelper: ItemTouchHelper

    companion object {
        private var mFragment: FavoriteFragment? = null

        fun newInstance(): FavoriteFragment {
            if (mFragment == null) {
                mFragment = FavoriteFragment()
            }
            return mFragment as FavoriteFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = DatabaseHelper.getInstance(context)!!
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_favorite, container, false)

        mNoneView = view.findViewById(R.id.favorite_none_view)
        mRecyclerView = view.findViewById(R.id.favorite_recycler) as RecyclerView

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = layoutManager
        mAdapter = FavoriteAdapter(this, this)
        mRecyclerView.adapter = mAdapter

        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(mAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(mRecyclerView)


        val swipeTouchListener = SwipeableRecyclerViewTouchListener(mRecyclerView, R.id.foreground, R.id.background,
                object : SwipeableRecyclerViewTouchListener.SwipeListener {
                    override fun onDismissedBySwipe(recyclerView: RecyclerView, reverseSortedPositions: IntArray) {
                        for (position in reverseSortedPositions) {
                            val model: RollModel = mAdapter.getItem(position)
                            mDb.deleteFavoriteRoll(model.id)
                            mAdapter.removeItem(position)
                            shouldShowEmptyState()
                            val snackbar = Snackbar.make(view, "Favorite deleted.", Snackbar.LENGTH_LONG).setAction("UNDO") {
                                mDb.addFavoriteRoll(model)
                                mAdapter.addItem(position, model)
                                shouldShowEmptyState()
                            }
                            snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                            snackbar.show()
                        }
                        mAdapter.notifyDataSetChanged()
                    }

                    override fun canSwipe(position: Int): Boolean {
                        return true
                    }
                })
        mRecyclerView.addOnItemTouchListener(swipeTouchListener)

        getRollsFromDatabase()
        return view
    }

    private fun getRollsFromDatabase() {
        val rolls = mDb.getAllFavoritesRolls()
        if (rolls.count() !== 0)
            mAdapter.addAll(rolls)
        shouldShowEmptyState()
    }

    fun shouldShowEmptyState() {
        if (mAdapter.itemCount === 0)
            mNoneView.visibility = View.VISIBLE
        else
            mNoneView.visibility = View.GONE
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper.startDrag(viewHolder)
    }
}