package io.deuxsept.dndice.MainView

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.Utils

class SettingsFragment : PreferenceFragmentCompatDividers(), SharedPreferences.OnSharedPreferenceChangeListener {

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

    override fun onResume() {
        super.onResume()
        // Set up a listener whenever a key changes
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Set up a listener whenever a key changes
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key.equals("pref_color_theme")) {
            //todo c'est pas opti vu que le fragment bug mais il est 00:33 quoi :/
            activity.recreate()
        }
    }
}