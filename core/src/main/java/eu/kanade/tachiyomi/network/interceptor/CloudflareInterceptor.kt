package eu.kanade.tachiyomi.network.interceptor

import android.content.Context
import eu.kanade.tachiyomi.core.R
import eu.kanade.tachiyomi.network.AndroidCookieJar
import eu.kanade.tachiyomi.network.FlareSolverrPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.IOException

class CloudflareInterceptor(
    private val context: Context,
    private val cookieManager: AndroidCookieJar,
    defaultUserAgentProvider: () -> String,
) : CloudflareInterceptorBase(context, defaultUserAgentProvider) {
    private val flareSolverrPreferences = Injekt.get<FlareSolverrPreferences>()

    override fun intercept(
        chain: Interceptor.Chain,
        request: Request,
        response: Response,
    ): Response {
        try {
            response.close()
            cookieManager.remove(request.url, COOKIE_NAMES, 0)
            val oldCookie = cookieManager.get(request.url)
                .firstOrNull { it.name == "cf_clearance" }

            if (flareSolverrPreferences.enabled().get()) {
                FlareSolverrInterceptor(context, cookieManager, { userAgent })
                    .resolveWithFlareSolverr(request)
            } else {
                WebViewInterceptor(context, cookieManager, { userAgent })
                    .resolveWithWebView(request, oldCookie)
            }
            return chain.proceed(request)
        }
        // Because OkHttp's enqueue only handles IOExceptions, wrap the exception so that
        // we don't crash the entire app
        catch (e: CloudflareBypassException) {
            throw IOException(context.getString(R.string.information_cloudflare_bypass_failure), e)
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        return if (shouldIntercept(response)) {
            intercept(chain, request, response)
        } else {
            response
        }
    }
}
