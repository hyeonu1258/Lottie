package com.hyeonu.lottie.view

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hyeonu.lottie.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mainFragment: Fragment? = null
    private var compareFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView.setNavigationItemSelectedListener(this)

        mainFragment = MainFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, mainFragment).commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.palette -> {
                mainFragment = mainFragment ?: MainFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, mainFragment).commit()
            }
            R.id.lottieNFresco -> {
                compareFragment = compareFragment ?: CompareFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, compareFragment).commit()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
