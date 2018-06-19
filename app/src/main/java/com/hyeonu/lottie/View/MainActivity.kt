package com.hyeonu.lottie.View

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.hyeonu.lottie.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.alphaVideoView.onResume()
        binding.alphaVideoView.start()
    }

    override fun onPause() {
        super.onPause()
        binding.alphaVideoView.onPause()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.palette -> {
                var intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            R.id.lottieNFresco -> {
                var intent = Intent(this, CompareActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
