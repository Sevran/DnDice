package io.deuxsept.dndice.MainView

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import io.deuxsept.dndice.R

/**
 * 2.7.0 Toujours plus haut
 * La république me suce le tuyau
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var HOME_FRAGMENT: Int = 0
    var FAVORITE_FRAGMENT: Int = 1
    var RECENT_FRAGMENT: Int = 2

    var mCurrentfragment: Int = 0
    lateinit var mHomeFragment: HomeFragment
    lateinit var mToolbar: Toolbar
    lateinit var mDrawer: DrawerLayout
    lateinit var mNavigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)

        mDrawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()

        mNavigationView = findViewById(R.id.nav_view) as NavigationView
        mNavigationView.setNavigationItemSelectedListener(this)

        val ft = supportFragmentManager.beginTransaction()
        mHomeFragment = HomeFragment.newInstance()
        ft.add(R.id.fragment_container, mHomeFragment)
        ft.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else if (mCurrentfragment != HOME_FRAGMENT) {
            switchFragment(HomeFragment.newInstance(), R.string.app_name, HOME_FRAGMENT)
        } else if (HomeFragment.mResultViewOpened) {
            mHomeFragment.closeResultView()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_home -> {
                switchFragment(HomeFragment.newInstance(), R.string.app_name, HOME_FRAGMENT)
            }
            R.id.nav_favorite -> {

            }
            R.id.nav_recent -> {
                switchFragment(RecentFragment.newInstance(), R.string.nav_last_rolls, RECENT_FRAGMENT)
            }
            R.id.nav_settings -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_beer -> {

            }
        }
        mDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun switchFragment(fragment: Fragment, fragmentName: Int, fragmentPos: Int) {
        if (mCurrentfragment != fragmentPos) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commitAllowingStateLoss()
            mToolbar.setTitle(fragmentName)
            mCurrentfragment = fragmentPos
            mNavigationView.menu.getItem(fragmentPos).isChecked = true
        }
    }

    fun push_element_to_stack(view: View) {
        mHomeFragment.push_element_to_stack(view)
    }
}