package com.jonathanchiou.foodorganizer

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.common.api.ApiException
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LoginActivity : AppCompatActivity() {

    @BindView(R.id.google_login_button)
    lateinit var googleLoginButton : GoogleLoginButton

    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        ClientManager.initialize(this)
        val clientManager = ClientManager.get()

        googleLoginButton.listen(object: GoogleLoginButton.LoginListener {
            override fun onLoginSuccess(idToken : String) {
                clientManager.foodOrganizerClient
                        .connect(idToken)
                        .subscribeWith(object: Observer<UIModel<Token>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onNext(uiModel: UIModel<Token>) {
                                when (uiModel.state) {
                                    State.SUCCESS -> startActivity(
                                            Intent(this@LoginActivity,
                                                   MainActivity::class.java))
                                }
                            }

                            override fun onError(e: Throwable) {
                            }

                            override fun onComplete() {
                            }
                        })
            }

            override fun onLoginFailure(e: ApiException) {
            }

            override fun onLoginCancel() {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleLoginButton.onActivityResult(requestCode, resultCode, data)
    }
}
