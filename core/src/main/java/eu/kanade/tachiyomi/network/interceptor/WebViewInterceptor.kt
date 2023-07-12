package eu.kanade.tachiyomi.network.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.ContextCompat
import eu.kanade.tachiyomi.core.R
import eu.kanade.tachiyomi.network.AndroidCookieJar
import eu.kanade.tachiyomi.util.system.DeviceUtil
import eu.kanade.tachiyomi.util.system.WebViewClientCompat
import eu.kanade.tachiyomi.util.system.WebViewUtil
import eu.kanade.tachiyomi.util.system.isOutdated
import eu.kanade.tachiyomi.util.system.setDefaultSettings
import eu.kanade.tachiyomi.util.system.toast
import okhttp3.Cookie
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import tachiyomi.core.util.lang.launchUI
import java.util.Locale
import java.util.concurrent.CountDownLatch

class WebViewInterceptor(
    private val context: Context,
    private val cookieManager: AndroidCookieJar,
    private val defaultUserAgentProvider: () -> String,
) : CloudflareInterceptorBase(context, defaultUserAgentProvider) {
    private val executor = ContextCompat.getMainExecutor(context)

    /**
     * When this is called, it initializes the WebView if it wasn't already. We use this to avoid
     * blocking the main thread too much. If used too often we could consider moving it to the
     * Application class.
     */
    private val initWebView by lazy {
        // Crashes on some devices. We skip this in some cases since the only impact is slower
        // WebView init in those rare cases.
        // See https://bugs.chromium.org/p/chromium/issues/detail?id=1279562
        if (DeviceUtil.isMiui || Build.VERSION.SDK_INT == Build.VERSION_CODES.S && DeviceUtil.isSamsung) {
            return@lazy
        }

        try {
            WebSettings.getDefaultUserAgent(context)
        } catch (_: Exception) {
            // Avoid some crashes like when Chrome/WebView is being updated.
        }
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (!shouldIntercept(response)) {
            return response
        }

        if (!WebViewUtil.supportsWebView(context)) {
            launchUI {
                context.toast(R.string.information_webview_required, Toast.LENGTH_LONG)
            }
            return response
        }
        initWebView

        return intercept(chain, request, response)
    }

    private fun parseHeaders(headers: Headers): Map<String, String> {
        return headers
            // Keeping unsafe header makes webview throw [net::ERR_INVALID_ARGUMENT]
            .filter { (name, value) ->
                isRequestHeaderSafe(name, value)
            }
            .groupBy(keySelector = { (name, _) -> name }) { (_, value) -> value }
            .mapValues { it.value.getOrNull(0).orEmpty() }
    }

    private fun createWebView(request: Request): WebView {
        return WebView(context).apply {
            setDefaultSettings()
            // Avoid sending empty User-Agent, Chromium WebView will reset to default if empty
            settings.userAgentString = request.header("User-Agent") ?: defaultUserAgentProvider()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun resolveWithWebView(originalRequest: Request, oldCookie: Cookie?) {
        // We need to lock this thread until the WebView finds the challenge solution url, because
        // OkHttp doesn't support asynchronous interceptors.
        val latch = CountDownLatch(1)

        var webview: WebView? = null

        var challengeFound = false
        var cloudflareBypassed = false
        var isWebViewOutdated = false

        val origRequestUrl = originalRequest.url.toString()
        val headers = parseHeaders(originalRequest.headers)

        executor.execute {
            webview = createWebView(originalRequest)

            webview?.webViewClient = object : WebViewClientCompat() {
                override fun onPageFinished(view: WebView, url: String) {
                    fun isCloudFlareBypassed(): Boolean {
                        return cookieManager.get(origRequestUrl.toHttpUrl())
                            .firstOrNull { it.name == "cf_clearance" }
                            .let { it != null && it != oldCookie }
                    }

                    if (isCloudFlareBypassed()) {
                        cloudflareBypassed = true
                        latch.countDown()
                    }

                    if (url == origRequestUrl && !challengeFound) {
                        // The first request didn't return the challenge, abort.
                        latch.countDown()
                    }
                }

                override fun onReceivedErrorCompat(
                    view: WebView,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String,
                    isMainFrame: Boolean,
                ) {
                    if (isMainFrame) {
                        if (errorCode in ERROR_CODES) {
                            // Found the Cloudflare challenge page.
                            challengeFound = true
                        } else {
                            // Unlock thread, the challenge wasn't found.
                            latch.countDown()
                        }
                    }
                }
            }

            webview?.loadUrl(origRequestUrl, headers)
        }

        latch.awaitFor30Seconds()

        executor.execute {
            if (!cloudflareBypassed) {
                isWebViewOutdated = webview?.isOutdated() == true
            }

            webview?.run {
                stopLoading()
                destroy()
            }
        }

        // Throw exception if we failed to bypass Cloudflare
        if (!cloudflareBypassed) {
            // Prompt user to update WebView if it seems too outdated
            if (isWebViewOutdated) {
                context.toast(R.string.information_webview_outdated, Toast.LENGTH_LONG)
            }

            throw CloudflareBypassException()
        }
    }
}

// Based on [IsRequestHeaderSafe] in https://source.chromium.org/chromium/chromium/src/+/main:services/network/public/cpp/header_util.cc
private fun isRequestHeaderSafe(_name: String, _value: String): Boolean {
    val name = _name.lowercase(Locale.ENGLISH)
    val value = _value.lowercase(Locale.ENGLISH)
    if (name in unsafeHeaderNames || name.startsWith("proxy-")) return false
    if (name == "connection" && value == "upgrade") return false
    return true
}

private val unsafeHeaderNames = listOf("content-length", "host", "trailer", "te", "upgrade", "cookie2", "keep-alive", "transfer-encoding", "set-cookie")
