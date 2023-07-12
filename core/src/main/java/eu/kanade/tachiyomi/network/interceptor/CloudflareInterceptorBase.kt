package eu.kanade.tachiyomi.network.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class CloudflareInterceptorBase(context: Context, defaultUserAgentProvider: () -> String) :
    Interceptor {

    fun shouldIntercept(response: Response): Boolean {
        // Check if Cloudflare anti-bot is on
        return response.code in ERROR_CODES && response.header("Server") in SERVER_CHECK
    }

    open fun intercept(chain: Interceptor.Chain, request: Request, response: Response): Response {
        val newRequest = request.newBuilder()
            .header("User-Agent", userAgent)
            .build()

        val newResponse = chain.proceed(newRequest)

        if (shouldIntercept(newResponse)) {
            throw CloudflareBypassException()
        }

        return chain.proceed(request)
    }

    fun CountDownLatch.awaitFor30Seconds() {
        await(30, TimeUnit.SECONDS)
    }

    protected val userAgent = defaultUserAgentProvider()
    protected val ERROR_CODES = listOf(403, 503)
    protected val SERVER_CHECK = arrayOf("cloudflare-nginx", "cloudflare")
    protected val COOKIE_NAMES = listOf("cf_clearance")

    class CloudflareBypassException : Exception()
}
