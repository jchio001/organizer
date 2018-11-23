package com.jonathanchiou.foodorganizer

import android.content.Context
import android.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

class ClientManager(context: Context) {

    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    val serviceFactory: ServiceFactory by lazy {
        ServiceFactory(tokenInterceptor,
                       HttpLoggingInterceptor().setLevel(Level.BODY))
    }

    val lazyFoodOrganizerService = lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        serviceFactory.create(FoodOrganizerService::class.java)
    }

    val foodOrganizerClient: FoodOrganizerClient by lazy {
        FoodOrganizerClient(lazyFoodOrganizerService.value)
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
