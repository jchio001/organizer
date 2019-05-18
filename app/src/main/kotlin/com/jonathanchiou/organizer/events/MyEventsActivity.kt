package com.jonathanchiou.organizer.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.jonathanchiou.organizer.R

class MyEventsActivity: AppCompatActivity() {

    @BindView(R.id.my_events_tablayout)
    lateinit var myEventTabLayout: TabLayout

    @BindView(R.id.my_events_viewpager)
    lateinit var myEventsViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)
        ButterKnife.bind(this)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.elevation = 0.0f
        }

        myEventTabLayout.setupWithViewPager(myEventsViewPager)
        myEventsViewPager.adapter = MyEventsFragmentAdapter(supportFragmentManager, this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
