package io.deuxsept.dndice.MainView

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import io.deuxsept.dndice.R

/**
 * Main activity
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var HOME_FRAGMENT: Int = 0
    var FAVORITE_FRAGMENT: Int = 1
    var RECENT_FRAGMENT: Int = 2
    var SETINGS_FRAGMENT: Int = 3

    var mCurrentFragmentPos: Int = 0
    lateinit var mHomeFragment: HomeFragment
    lateinit var mToolbar: Toolbar
    lateinit var mDrawer: DrawerLayout
    lateinit var mNavigationView: NavigationView
    lateinit var mLastRoll: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_color_theme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        mLastRoll = findViewById(R.id.last_roll) as TextView

        mDrawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()

        mNavigationView = findViewById(R.id.nav_view) as NavigationView
        mNavigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            val ft = supportFragmentManager.beginTransaction()
            mHomeFragment = HomeFragment.newInstance()
            ft.add(R.id.fragment_container, mHomeFragment)
            ft.commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else if (mCurrentFragmentPos != HOME_FRAGMENT) {
            switchFragment(HomeFragment.newInstance(), R.string.app_name, HOME_FRAGMENT)
        } else if (mHomeFragment.mState.mResultViewOpened) {
            mHomeFragment.closeResultView()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        mLastRoll.visibility = View.GONE
        when (id) {
            R.id.nav_home -> {
                switchFragment(HomeFragment.newInstance(), R.string.app_name, HOME_FRAGMENT)
                mLastRoll.visibility = View.VISIBLE
            }
            R.id.nav_favorite -> {
                switchFragment(FavoriteFragment.newInstance(), R.string.nav_favorites, FAVORITE_FRAGMENT)
            }
            R.id.nav_recent -> {
                switchFragment(RecentFragment.newInstance(), R.string.nav_last_rolls, RECENT_FRAGMENT)
            }
            R.id.nav_settings -> {
                switchFragment(SettingsFragment.newInstance(), R.string.nav_settings, SETINGS_FRAGMENT)
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

    fun switchFragment(fragment: Fragment, fragmentName: Int, fragmentPos: Int) {
        if (mCurrentFragmentPos != fragmentPos) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commitAllowingStateLoss()
            mToolbar.setTitle(fragmentName)
            mCurrentFragmentPos = fragmentPos
            mNavigationView.menu.getItem(fragmentPos).isChecked = true
        }
    }

    @Suppress("unused")
    fun push_element_to_stack(view: View) {
        mHomeFragment.push_element_to_stack(view)
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