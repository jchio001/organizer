package com.jonathanchiou.organizer.login

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
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.State
import com.jonathanchiou.organizer.api.model.UIModel
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.Token
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
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

    protected var compositeDisposable: CompositeDisposable = CompositeDisposable()

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

                if (compositeDisposable.isDisposed) {
                    compositeDisposable = CompositeDisposable()
                }

                clientManager.foodOrganizerClient
                        .connect(account.idToken!!)
                        .subscribeWith(object : Observer<UIModel<Token>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onNext(uiModel: UIModel<Token>) {
                                when (uiModel.state) {
                                    State.PENDING -> loginListener?.onLoginPending()
                                    State.SUCCESS -> {
                                        clientManager.setToken(uiModel.model!!)
                                        loginListener?.onLoginSuccess()
                                    }
                                    else -> loginListener?.onLoginFailure()
                                }
                            }

                            override fun onError(e: Throwable) {
                            }

                            override fun onComplete() {
                            }
                        })
            } catch (e: ApiException) {
                loginListener?.onLoginFailure()
            }
        }
    }

    fun cancelPendingRequest() {
        compositeDisposable.dispose()
    }

    companion object {
        const val RC_SIGN_IN = 9001;
    }
}