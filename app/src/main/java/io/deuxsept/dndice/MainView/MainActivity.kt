package io.deuxsept.dndice.MainView

import android.app.Dialog
import android.os.Bundle
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.LastRollInfo
import io.deuxsept.dndice.Utils.SwitchFragmentAction
import io.deuxsept.dndice.Utils.find
import org.w3c.dom.Text

/**
 * Main activity
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    public val HOME_FRAGMENT: Int = 0
    public val FAVORITE_FRAGMENT: Int = 1
    public val RECENT_FRAGMENT: Int = 2
    public val SETTINGS_FRAGMENT: Int = 3

    var mCurrentFragmentPos: Int = -1 // Uninitialized at first.
    val mToolbar: Toolbar by lazy               { find<Toolbar>(R.id.toolbar)}
    val mDrawer: DrawerLayout by lazy           { find<DrawerLayout>(R.id.drawer_layout)}
    val mNavigationView: NavigationView by lazy { find<NavigationView>(R.id.nav_view)}
    val mLastRoll: TextView by lazy             { find<TextView>(R.id.last_roll)}
    var _fragmentHandles: MutableMap<Int, Fragment> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_color_theme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }

        setSupportActionBar(mToolbar)
        val toggle = ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()
        mNavigationView.setNavigationItemSelectedListener(this)

        var fragId: Int;
        fragId = when (savedInstanceState) {
            null -> HOME_FRAGMENT
            else -> savedInstanceState.getInt("current_fragment")
        }
        fragId = when (intent.extras) {
            null -> fragId
            else -> intent.extras.getInt("current_fragment")
        }

        switchFragment(HomeFragment.newInstance(), R.string.app_name, HOME_FRAGMENT)
        Log.wtf("ARGH", "Entering MainActivity.onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState == null) { return }

        // Save the current fragment to reload it after
        outState.putInt("current_fragment", mCurrentFragmentPos)
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else if (mCurrentFragmentPos != HOME_FRAGMENT) {
            switchFragment(HomeFragment.newInstance(), R.string.app_name, HOME_FRAGMENT)
        } else if (_fragmentHandles[HOME_FRAGMENT] != null && (_fragmentHandles[HOME_FRAGMENT] as HomeFragment).mResultViewOpened) {
            (_fragmentHandles[HOME_FRAGMENT] as HomeFragment).closeResultView()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var info = SwitchFragmentAction.Replace
        var insertedFragment: Fragment
        var insertedIdx: Int

        when (item.itemId) {
            R.id.nav_home ->     { insertedFragment = HomeFragment.newInstance(); insertedIdx = HOME_FRAGMENT }
            R.id.nav_favorite -> { insertedFragment = FavoriteFragment.newInstance(); insertedIdx = FAVORITE_FRAGMENT }
            R.id.nav_recent ->   { insertedFragment = RecentFragment.newInstance(); insertedIdx = RECENT_FRAGMENT }
            R.id.nav_settings -> { insertedFragment = SettingsFragment.newInstance(); insertedIdx = SETTINGS_FRAGMENT }
            else ->              { insertedFragment = HomeFragment.newInstance(); insertedIdx = HOME_FRAGMENT }
        }

        mLastRoll.visibility = View.GONE
        when (id) {
            R.id.nav_home, R.id.nav_favorite, R.id.nav_recent, R.id.nav_settings -> {
                switchFragment(insertedFragment, R.string.app_name, insertedIdx, info)
                mLastRoll.visibility = View.VISIBLE
            }
            R.id.nav_share -> {
                shareApp()
            }
            R.id.nav_beer -> {
                payUsABeer()
            }
            R.id.nav_about -> {
                about()
            }
        }
        mDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun switchFragment(fragment: Fragment, fragmentName: Int, fragmentPos: Int, action: SwitchFragmentAction = SwitchFragmentAction.Replace) {
        if (mCurrentFragmentPos == fragmentPos)
            return

        // Update our fragment handles
        _fragmentHandles[fragmentPos] = fragment

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        when (action) {
            SwitchFragmentAction.Replace -> fragmentTransaction.replace(R.id.fragment_container, fragment)
            SwitchFragmentAction.Insert -> fragmentTransaction.add(R.id.fragment_container, fragment)
        }
        fragmentTransaction.commitAllowingStateLoss()
        mToolbar.setTitle(fragmentName)
        mCurrentFragmentPos = fragmentPos
        mNavigationView.menu.getItem(fragmentPos).isChecked = true
    }

    @Suppress("unused")
    fun push_element_to_stack(view: View) {
        (_fragmentHandles[HOME_FRAGMENT] as HomeFragment).push_element_to_stack(view)
    }

    fun shareApp() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "Take a look at this new SEVRAN app ! http://SEVRANDEV.io"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DndDice Android App")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    fun payUsABeer() {
        val dialog: Dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_pay_us_beer)
        dialog.show()
    }

    fun about() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_about)
        dialog.show()
    }
}