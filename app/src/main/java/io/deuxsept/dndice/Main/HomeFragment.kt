package io.deuxsept.dndice.Main

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import io.deuxsept.dndice.R

class HomeFragment : Fragment() {

    lateinit var mResultView: View
    lateinit var mResultVar: TextView
    lateinit var mRollButton: View
    lateinit var mCloseResFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_home, container, false)

        mResultView = view.findViewById(R.id.result_view)
        mResultVar = view.findViewById(R.id.result_var) as TextView
        mRollButton = view.findViewById(R.id.roll_button)
        mRollButton.setOnClickListener {
            openResultView()
        }
        mCloseResFab = view.findViewById(R.id.close_result_fab) as FloatingActionButton
        mCloseResFab.setOnClickListener {
            closeResultView()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ButterKnife.unbind(this)
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

    fun closeResultView() {
        mResultView.visibility = View.GONE
    }
}
