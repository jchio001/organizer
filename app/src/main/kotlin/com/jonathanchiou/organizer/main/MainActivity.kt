package com.jonathanchiou.organizer.main

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.DrawerLayout.DrawerListener
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import butterknife.*
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.scheduler.SchedulerActivity
import com.jonathanchiou.organizer.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout

    @BindView(R.id.main_navigation_view)
    lateinit var mainNavigationView: NavigationView

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindDrawable(R.drawable.ic_menu)
    lateinit var upButtonIcon: Drawable

    @BindColor(R.color.white)
    @JvmField
    var white = 0

    var selectedItemId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        // Have to dynamically change the icon's color since I'm dynamically changing the icon.
        upButtonIcon.setColorFilter(white, PorterDuff.Mode.SRC_ATOP)
        actionBar.setHomeAsUpIndicator(upButtonIcon)

        mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            selectedItemId = menuItem.itemId
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        drawerLayout.addDrawerListener(object: DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                when (selectedItemId) {
                    R.id.settings -> startActivity(Intent(this@MainActivity,
                                                          SettingsActivity::class.java))
                }
                selectedItemId = -1
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerStateChanged(drawerView: Int) {
            }
        })
    }

    @OnClick(R.id.scheduler_fab)
    fun onSchedulerFabClicked() {
        startActivity(Intent(this, SchedulerActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}