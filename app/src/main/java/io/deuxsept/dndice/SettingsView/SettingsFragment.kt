package io.deuxsept.dndice.SettingsView

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
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
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.SimpleItemTouchHelperCallback
import io.deuxsept.dndice.Utils.Utils

class SettingsFragment : PreferenceFragment() {

    companion object {
        private var mFragment: SettingsFragment? = null

        fun newInstance(): SettingsFragment {
            if (mFragment == null) {
                mFragment = SettingsFragment()
            }
            return mFragment as SettingsFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false)
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_settings, container, false)
        return view
    }
}