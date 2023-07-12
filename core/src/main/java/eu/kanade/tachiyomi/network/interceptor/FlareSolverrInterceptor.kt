package eu.kanade.tachiyomi.network.interceptor

import android.content.Context
import eu.kanade.tachiyomi.network.AndroidCookieJar
import eu.kanade.tachiyomi.network.FlareSolverrPreferences
import okhttp3.Callback
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.IOException
import java.util.concurrent.CountDownLatch

class FlareSolverrInterceptor(
    private val context: Context,
    private val cookieManager: AndroidCookieJar,
    private val defaultUserAgentProvider: () -> String,
) : CloudflareInterceptorBase(context, defaultUserAgentProvider) {
    private val flareSolverrPreferences = Injekt.get<FlareSolverrPreferences>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        return if (shouldIntercept(response)) {
            intercept(chain, request, response)
        } else {
            response
        }
    }

    internal fun resolveWithFlareSolverr(originalRequest: Request) {
        val flareSolverrUrl = flareSolverrPreferences.url().get()
        val flareSolverrPort = flareSolverrPreferences.captchaPort().get()
        val url = "$flareSolverrUrl:$flareSolverrPort/v1"
        val latch = CountDownLatch(1)

        val requestUrl = originalRequest.url.toString()
        val requestBody = JSONObject()
            .put("cmd", "request.get")
            .put("url", requestUrl)
            .put("returnOnlyCookies", true)
            .put("maxTimeout", flareSolverrPreferences.captchaMaxTimeout().get().toInt())

        val request = Request.Builder()
            .url(url)
            .post(
                requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()),
            )
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    latch.countDown()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val responseBody = response.body?.string()
                    println(responseBody)
                    response.close()
                    val responseJson = JSONObject(responseBody)
                    if (responseJson.getString("status").equals("ok")) {
                        val solution = responseJson.getJSONObject("solution")
                        val userAgent = solution.getString("userAgent")

                        val cookies = solution.getJSONArray("cookies")
                        val cookieList = mutableListOf<Cookie>()

                        for (i in 0 until cookies.length()) {
                            val cookie = cookies.getJSONObject(i)
                            cookieList.add(
                                Cookie.Builder()
                                    // FlareSolverr returns domain with a leading dot
                                    .domain(cookie.getString("domain").removePrefix("."))
                                    .path(cookie.getString("path"))
                                    .name(cookie.getString("name"))
                                    .value(cookie.getString("value"))
                                    .build(),
                            )
                        }
                        println(cookieList)
                        cookieManager.saveFromResponse(requestUrl.toHttpUrl(), cookieList)
                        latch.countDown()
                    } else {
                        latch.countDown()
                    }
                }
            },
        )
        latch.awaitFor30Seconds()
    }
}
