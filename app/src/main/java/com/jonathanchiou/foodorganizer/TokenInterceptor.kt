package com.jonathanchiou.foodorganizer

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.squareup.moshi.JsonAdapter
import io.reactivex.exceptions.Exceptions
import okhttp3.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection


fun String.toJwtPayload(jwtPayloadAdapter: JsonAdapter<JwtPayload>) : JwtPayload {
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
                          message: String) : Response {
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
                       val lazyFoodOrganizerService: Lazy<FoodOrganizerService>) : Interceptor {

    private val sharedPreferencesReference = WeakReference(sharedPreferences)

    @Volatile
    private var jwtPayload : JwtPayload? = null

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

                val now = System.currentTimeMillis() / 1000;

                if (now > jwtPayload!!.expirationTime) {
                    return createStubbedResponse(request,
                                                 HttpsURLConnection.HTTP_UNAUTHORIZED,
                                                 "Token has expired.")
                }

                if (request.header(INTERNAL_REFRESH_TOKEN_HEADER) == "true") {
                    if (now - jwtPayload!!.issuedTime >= TEN_DAYS) {
                        synchronized(this@TokenInterceptor) {
                            if (now - jwtPayload!!.issuedTime >= TEN_DAYS) {
                                try {
                                    this.token = lazyFoodOrganizerService.value.refreshToken()
                                            .blockingFirst()
                                            .body()!!
                                            .token
                                    this.jwtPayload = this.token?.toJwtPayload(jwtPayloadAdapter)
                                } catch (e: Exception) {
                                    Log.e("TokenInterceptor", "Failed to refresh token");
                                }
                            }
                        }
                    }
                }

                request = request.newBuilder()
                        .removeHeader(INTERNAL_REQUIRED_HEADER)
                        .removeHeader(INTERNAL_REFRESH_TOKEN_HEADER)
                        .addHeader(AUTHORIZATION, token!!)
                        .build()
            }
        }

        return chain.proceed(request)
    }

    companion object {
        const val TOKEN_KEY = "food_organizer_token"
        const val INTERNAL_REQUIRED_HEADER = "required"
        const val INTERNAL_REFRESH_TOKEN_HEADER = "refresh-token"
        const val AUTHORIZATION = "authorization"
        const val TEN_DAYS = 864000
    }
}