package com.jonathanchiou.organizer.api

import android.content.Context
import android.preference.PreferenceManager
import com.jonathanchiou.organizer.api.model.JwtPayload
import com.jonathanchiou.organizer.api.model.ServiceFactory
import com.jonathanchiou.organizer.api.model.Token
import com.squareup.moshi.Moshi
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

class ClientManager(context: Context) {

    val moshi by lazy {
        Moshi.Builder()
            .build()
    }

    val serviceFactory: ServiceFactory by lazy {
        ServiceFactory(tokenInterceptor,
                       HttpLoggingInterceptor().setLevel(Level.BODY))
    }

    val lazyFoodOrganizerService = lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        serviceFactory.create(OrganizerService::class.java)
    }

    val organizerClient: OrganizerClient by lazy {
        OrganizerClient(lazyFoodOrganizerService.value)
    }

    val tokenInterceptor = TokenInterceptor(PreferenceManager.getDefaultSharedPreferences(context),
                                            moshi.adapter(JwtPayload::class.java),
                                            lazyFoodOrganizerService)

    fun isAlreadyLoggedIn(): Boolean {
        return tokenInterceptor.token != null
    }

    fun setToken(token: Token) {
        tokenInterceptor.token = token.token
    }

    fun logout() {
        tokenInterceptor.token = null
    }

    companion object {
        @JvmField
        protected var clientManager: ClientManager? = null

        fun initialize(context: Context) {
            clientManager = ClientManager(context)
        }

        fun get(): ClientManager {
            if (clientManager == null) {
                throw IllegalStateException("ClientManager not initialized!")
            }

            return clientManager!!
        }
    }
}
