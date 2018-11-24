package com.jonathanchiou.organizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClientManager.initialize(this)
        startActivity(Intent(this,
                             if (ClientManager.get().isAlreadyLoggedIn()) MainActivity::class.java
                             else LoginActivity::class.java))
        finish()
    }
}
