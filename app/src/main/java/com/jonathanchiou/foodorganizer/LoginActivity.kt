package com.jonathanchiou.foodorganizer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.common.api.ApiException
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LoginActivity : AppCompatActivity() {

    @BindView(R.id.google_login_button)
    lateinit var googleLoginButton : GoogleLoginButton

    protected val progressDialog : ProgressDialog by lazy {
        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.connecting))
        progressDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        ClientManager.initialize(this)
        val clientManager = ClientManager.get()

        if (clientManager.isAlreadyLoggedIn()) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            return
        }

        googleLoginButton
                .attachClient(clientManager)
                .listen(object: GoogleLoginButton.LoginListener {
                    override fun onLoginPending() {
                        progressDialog.show()
                    }

                    override fun onLoginSuccess() {
                        progressDialog.dismiss()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }

                    override fun onLoginFailure() {
                        Toast.makeText(this@LoginActivity,
                                       "Failed to login. Try again later",
                                       Toast.LENGTH_SHORT)
                                .show()
                    }

                    override fun onLoginCancel() {
                    }
                })
    }

    override fun onStop() {
        super.onStop()
        googleLoginButton.cancelPendingRequest()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleLoginButton.onActivityResult(requestCode, resultCode, data)
    }
}
