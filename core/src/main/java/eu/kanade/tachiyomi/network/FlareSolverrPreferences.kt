package eu.kanade.tachiyomi.network
import tachiyomi.core.preference.Preference
import tachiyomi.core.preference.PreferenceStore

class FlareSolverrPreferences(private val preferenceStore: PreferenceStore) {
    fun flareSolverrDeafaultUrl(): String {
        return "http://localhost"
    }

    fun enabled(): Preference<Boolean> {
        return preferenceStore.getBoolean("flare_solverr_enabled", false)
    }

    fun userAgent(): Preference<String> {
        return preferenceStore.getString("flare_solverr_user_agent", NetworkPreferences(preferenceStore).defaultUserAgent().get())
    }

    fun captchaPort(): Preference<String> {
        return preferenceStore.getString("flare_solverr_captcha_port", "8191")
    }

    fun url(): Preference<String> {
        return preferenceStore.getString("flare_solverr_url", flareSolverrDeafaultUrl())
    }

    fun captchaMaxTimeout(): Preference<String> {
        return preferenceStore.getString("flare_solverr_captcha_max_timeout", "60000")
    }
}
