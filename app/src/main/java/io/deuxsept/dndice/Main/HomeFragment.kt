package io.deuxsept.dndice.Main

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.Utils

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

    var width: Int = 0
    var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        width = metrics.widthPixels
        height = metrics.heightPixels
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
        Utils.circularReveal(mResultView, width/2, height)
        mFavoriteFab.show()
        mCloseResFab.show()
        mReplayFab.show()
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
        mFavoriteFab.hide()
        mCloseResFab.hide()
        mReplayFab.hide()
        Utils.circularUnreveal(mResultView, width/2, height)
    }

    fun reRoll() {

    }
}
