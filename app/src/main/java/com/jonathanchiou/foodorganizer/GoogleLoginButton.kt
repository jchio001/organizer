package com.jonathanchiou.foodorganizer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleLoginButton(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs)  {

    interface LoginListener {
        fun onLoginSuccess(idToken: String)
        fun onLoginFailure(e: ApiException)
        fun onLoginCancel()
    }

    @JvmField
    protected val googleSignInClient : GoogleSignInClient

    private var loginListener : LoginListener? = null

    init {
        View.inflate(context, R.layout.button_google, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_server_client_id))
                .build()
        googleSignInClient = GoogleSignIn.getClient(context, signInOptions)

        setOnClickListener { _ ->
            val signInIntent = googleSignInClient.signInIntent
            (context as Activity).startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    fun listen(loginListener: LoginListener) {
        this.loginListener = loginListener
    }

    fun onActivityResult(requestCode: Int,
                         resultCode: Int,
                         data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val signInTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = signInTask.getResult(ApiException::class.java)
                loginListener?.onLoginSuccess(account!!.idToken!!)
            } catch (e: ApiException) {
                loginListener?.onLoginFailure(e)
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001;
    }
}