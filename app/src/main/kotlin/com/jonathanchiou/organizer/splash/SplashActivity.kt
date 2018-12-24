package com.jonathanchiou.organizer.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.login.LoginActivity
import com.jonathanchiou.organizer.main.MainActivity

class SplashActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClientManager.initialize(this)
        startActivity(Intent(this,
                             if (ClientManager.get().isAlreadyLoggedIn()) MainActivity::class.java
                             else LoginActivity::class.java))
        finish()
    }
}
