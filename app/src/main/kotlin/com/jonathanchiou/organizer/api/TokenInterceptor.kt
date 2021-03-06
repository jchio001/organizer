package com.jonathanchiou.organizer.api

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.jonathanchiou.organizer.api.model.JwtPayload
import com.squareup.moshi.JsonAdapter
import io.reactivex.exceptions.Exceptions
import okhttp3.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

fun String.toJwtPayload(jwtPayloadAdapter: JsonAdapter<JwtPayload>): JwtPayload {
    try {
        val jwtArray = this.split(".")
        val payloadData = Base64.decode(jwtArray[1], Base64.DEFAULT) // 2nd part is the payload!
        val payloadJsonString = String(payloadData, Charset.forName("UTF-8"))
        return jwtPayloadAdapter.fromJson(payloadJsonString)!!
    } catch (e: IOException) {
        Log.e("TokenInterceptor", "Failed to decode token")
        throw Exceptions.propagate(e)
    }
}

fun createStubbedResponse(request: Request,
                          statusCode: Int,
                          message: String): Response {
    return Response.Builder()
        .protocol(Protocol.HTTP_1_1)
        .request(request)
        .code(statusCode)
        .message("")
        .body(ResponseBody.create(MediaType.get("application/json"),
                                  "{'error: $message}"))
        .build()
}

class TokenInterceptor(sharedPreferences: SharedPreferences,
                       val jwtPayloadAdapter: JsonAdapter<JwtPayload>,
                       val lazyOrganizerService: Lazy<OrganizerService>): Interceptor {

    private val sharedPreferencesReference = WeakReference(sharedPreferences)

    @Volatile
    private var jwtPayload: JwtPayload? = null

    @Volatile
    var token: String? = null
        set(value) {
            field = value
            this.jwtPayload = value?.toJwtPayload(jwtPayloadAdapter)
            sharedPreferencesReference.get()?.edit()!!.putString(TOKEN_KEY, token).apply()
        }

    init {
        token = sharedPreferences.getString(TOKEN_KEY, null)
        this.jwtPayload = token?.toJwtPayload(jwtPayloadAdapter)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request.header(INTERNAL_REQUIRED_HEADER)?.let {
            if (it == AUTHORIZATION) {
                if (token == null) {
                    return createStubbedResponse(request,
                                                 HttpsURLConnection.HTTP_UNAUTHORIZED,
                                                 "Missing token.")
                }

                val now = System.currentTimeMillis() / 1000

                if (now > jwtPayload!!.expirationTime) {
                    return createStubbedResponse(request,
                                                 HttpsURLConnection.HTTP_UNAUTHORIZED,
                                                 "Token has expired.")
                }

                if (request.header(INTERNAL_SKIP_REFRESH_HEADER) != "true") {
                    if (now - jwtPayload!!.issuedTime >= TEN_DAYS) {
                        // TODO: consider using a semaphore instead?
                        synchronized(this@TokenInterceptor) {
                            if (now - jwtPayload!!.issuedTime >= TEN_DAYS) {
                                try {
                                    // TODO: Implement the token exchange endpoint on the backend!
                                    val freshToken = lazyOrganizerService.value
                                        .refreshToken()
                                        .blockingFirst()
                                        .body()!!
                                        .token

                                    sharedPreferencesReference.get()?.let {
                                        it.edit()
                                            .putString(TOKEN_KEY, freshToken)
                                            .apply()
                                    }

                                    this.token = freshToken
                                    this.jwtPayload = freshToken.toJwtPayload(jwtPayloadAdapter)
                                } catch (e: Exception) {
                                    Log.e("TokenInterceptor", "Failed to refresh token")
                                }
                            }
                        }
                    }
                }

                request = request.newBuilder()
                    .removeHeader(INTERNAL_REQUIRED_HEADER)
                    .removeHeader(INTERNAL_SKIP_REFRESH_HEADER)
                    .addHeader(AUTHORIZATION, token!!)
                    .build()
            }
        }

        return chain.proceed(request)
    }

    companion object {
        const val TOKEN_KEY = "food_organizer_token"
        const val INTERNAL_REQUIRED_HEADER = "required"
        const val INTERNAL_SKIP_REFRESH_HEADER = "skip-refresh"
        const val AUTHORIZATION = "authorization"
        const val TEN_DAYS = 864000
    }
}