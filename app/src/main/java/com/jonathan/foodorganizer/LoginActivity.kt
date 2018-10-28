package com.jonathan.foodorganizer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import butterknife.BindView
import butterknife.ButterKnife

class LoginActivity : AppCompatActivity() {

    @BindView(R.id.google_login_button)
    lateinit var googleLoginButton : GoogleLoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        googleLoginButton.listen(object: GoogleLoginButton.LoginListener {
            override fun onLoginSuccess(idToken : String) {
                PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                        .edit().putString("google_token", idToken)
                        .apply()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onLoginFailure() {
            }

            override fun onLoginCancel() {
            }
        })
    }
}
