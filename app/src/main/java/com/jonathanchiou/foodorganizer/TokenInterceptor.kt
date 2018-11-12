package com.jonathanchiou.foodorganizer

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import com.squareup.moshi.JsonAdapter
import io.reactivex.exceptions.Exceptions
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.charset.Charset


class TokenInterceptor(context: Context,
                       val jwtPayloadAdapter: JsonAdapter<JwtPayload>) : Interceptor {

    private val sharedPreferencesReference : WeakReference<SharedPreferences>

    private var token : String? = null
    private var jwtPayload : JwtPayload? = null

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        token = sharedPreferences.getString(TOKEN_KEY, null)
        token?.let(::convertStringToJwtPayload)

        sharedPreferencesReference =
                WeakReference(PreferenceManager.getDefaultSharedPreferences(context))
    }

    private fun convertStringToJwtPayload(token: String) : JwtPayload {
        try {
            val jwtArray = token.split("\\.")
            val payloadData = Base64.decode(jwtArray[1], Base64.DEFAULT) //decode body
            val payloadJsonString = String(payloadData, Charset.forName("UTF-8"))
            return jwtPayloadAdapter.fromJson(payloadJsonString)!!
        } catch (e: IOException) {
            Log.e("TokenInterceptor", "Failed to decode token")
            throw Exceptions.propagate(e)
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

    fun setToken(token: String) {
        this.token = token
        this.jwtPayload = convertStringToJwtPayload(token)
        sharedPreferencesReference.get()?.edit()!!.putString(TOKEN_KEY, token).apply()
    }

    companion object {
        val TOKEN_KEY = "food_organizer_token"
    }
}