package com.jonathanchiou.organizer.settings

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.login.LoginActivity

class SettingsActivity : AppCompatActivity() {

    @BindView(R.id.settings_navigation_view)
    lateinit var settingsNavigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        settingsNavigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.logout) {
                ClientManager.get().logout()
                startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                ActivityCompat.finishAffinity(this)
            }

            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
