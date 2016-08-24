package io.deuxsept.dndice.SettingsView

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import io.deuxsept.dndice.R
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment.newInstance())
            .commit()
    }
}
