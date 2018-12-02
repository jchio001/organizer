package com.jonathanchiou.organizer.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.ApiUIModel
import io.reactivex.disposables.Disposable

class GoogleLoginButton(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    interface LoginListener {
        fun onLoginPending()
        fun onLoginSuccess()
        fun onLoginFailure()
        fun onLoginCancel()
    }

    protected val googleSignInClient: GoogleSignInClient

    protected lateinit var clientManager: ClientManager

    protected var loginListener: LoginListener? = null

    protected var currentDisposable: Disposable? = null

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

    fun attachClient(clientManager: ClientManager): GoogleLoginButton {
        this.clientManager = clientManager
        return this
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
                val account = signInTask.getResult(ApiException::class.java)!!

                currentDisposable?.dispose()

                currentDisposable = clientManager.organizerClient
                    .connect(account.idToken!!)
                    .subscribe {
                        when (it.state) {
                            ApiUIModel.State.PENDING -> loginListener?.onLoginPending()
                            ApiUIModel.State.SUCCESS -> {
                                clientManager.setToken(it.model!!)
                                loginListener?.onLoginSuccess()
                            }
                            else -> loginListener?.onLoginFailure()
                        }
                    }
            } catch (e: ApiException) {
                loginListener?.onLoginFailure()
            }
        }
    }

    fun cancelPendingRequest() {
        currentDisposable?.dispose()
    }

    companion object {
        const val RC_SIGN_IN = 9001;
    }
}