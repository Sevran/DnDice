package io.deuxsept.dndice.Main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.deuxsept.dndice.R

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
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
}
