package io.deuxsept.dndice.MainView

import android.os.Bundle
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers
import io.deuxsept.dndice.R

class SettingsFragment : PreferenceFragmentCompatDividers() {

    companion object {
        private var mFragment: SettingsFragment? = null

        fun newInstance(): SettingsFragment {
            if (mFragment == null) {
                mFragment = SettingsFragment()
            }
            return mFragment as SettingsFragment
        }
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}