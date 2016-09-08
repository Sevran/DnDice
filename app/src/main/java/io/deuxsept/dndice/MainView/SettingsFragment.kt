package io.deuxsept.dndice.MainView

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers
import io.deuxsept.dndice.R

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
            var intent = activity.intent;
            activity.finish()
            activity.startActivity(intent)
        }
    }
}