package com.jonathanchiou.organizer.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.login.LoginActivity

class SettingsActivity : AppCompatActivity() {

    @BindView(R.id.settings_navigation_view)
    lateinit var settingsNavigationView: NavigationView

    protected val logOutDialog by lazy {
        AlertDialog.Builder(this@SettingsActivity)
            .setMessage(R.string.log_out_prompt)
            .setPositiveButton(R.string.log_out) { _, _ ->
                ClientManager.get().logout()
                startActivity(Intent(this, LoginActivity::class.java))
                ActivityCompat.finishAffinity(this)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        settingsNavigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.logout) {
                logOutDialog.show()
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
