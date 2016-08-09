package io.deuxsept.dndice.Main

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.deuxsept.dndice.R

class HomeFragment : Fragment() {

    /**
     * Home Views
     */
    lateinit var mRollButton: View
    lateinit var mDisplay: TextView

    /**
     * Result Views
     */
    lateinit var mResultView: View
    lateinit var mFormula: TextView
    lateinit var mResult: TextView
    lateinit var mDetail: TextView
    lateinit var mFavoriteFab: FloatingActionButton
    lateinit var mCloseResFab: FloatingActionButton
    lateinit var mReplayFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_home, container, false)

        mDisplay = view.findViewById(R.id.display) as TextView
        mRollButton = view.findViewById(R.id.roll_button)
        mRollButton.setOnClickListener {
            openResultView()
        }

        initResultViewVars(view)

        return view
    }

    companion object {
        private var mFragment: HomeFragment? = null

        fun newInstance(): HomeFragment {
            if (mFragment == null) {
                mFragment = HomeFragment()
            }
            return mFragment as HomeFragment
        }
    }

    fun openResultView() {
        mResultView.visibility = View.VISIBLE
    }

    fun initResultViewVars(view: View) {
        mResultView = view.findViewById(R.id.result_view)
        mFormula = view.findViewById(R.id.formula_var) as TextView
        mResult = view.findViewById(R.id.result_var) as TextView
        mDetail = view.findViewById(R.id.detail_var) as TextView

        mFavoriteFab = view.findViewById(R.id.replay_result_fab) as FloatingActionButton
        mFavoriteFab.setOnClickListener {
            favoriteRoll()
        }
        mCloseResFab = view.findViewById(R.id.close_result_fab) as FloatingActionButton
        mCloseResFab.setOnClickListener {
            closeResultView()
        }
        mReplayFab = view.findViewById(R.id.fav_result_fab) as FloatingActionButton
        mReplayFab.setOnClickListener {
            reRoll()
        }
    }

    fun favoriteRoll() {

    }

    fun closeResultView() {
        mResultView.visibility = View.GONE
    }

    fun reRoll() {

    }
}
