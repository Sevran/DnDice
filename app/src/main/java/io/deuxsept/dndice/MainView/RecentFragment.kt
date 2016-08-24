package io.deuxsept.dndice.MainView

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.deuxsept.dndice.Adapter.RecentAdapter
import io.deuxsept.dndice.Database.DatabaseHelper
import io.deuxsept.dndice.R

/**
 * Created by Luo
 * 13/08/2016.
 */
class RecentFragment : Fragment() {

    lateinit var mDb: DatabaseHelper
    lateinit var mNoneView: View
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RecentAdapter

    companion object {
        private var mFragment: RecentFragment? = null

        fun newInstance(): RecentFragment {
            if (mFragment == null) {
                mFragment = RecentFragment()
            }
            return mFragment as RecentFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = DatabaseHelper.getInstance(context)!!
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_recent, container, false)

        mNoneView = view.findViewById(R.id.recent_none_view)
        mRecyclerView = view.findViewById(R.id.recent_recycler) as RecyclerView

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = layoutManager
        mAdapter = RecentAdapter(this)
        mRecyclerView.adapter = mAdapter

        getRollsFromDatabase()
        return view
    }

    private fun getRollsFromDatabase() {
        val rolls = mDb.getAllRecentRolls()
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
}