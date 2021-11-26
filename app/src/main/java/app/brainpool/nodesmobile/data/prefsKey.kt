package app.brainpool.nodesmobile.data

import com.pixplicity.easyprefs.library.Prefs

object PrefsKey {
    const val AUTH_KEY = "AUTH_KEY"
    const val USER_ID = "USER_ID"
    const val USER = "USER"
    const val THEME = "THEME"
    const val EMAIL = "EMAIL"
    const val PASSWORD = "PASSWORD"

    var darkMode = Prefs.getInt(THEME, 0)
        set(value) = Prefs.edit().putInt(THEME, value).apply()

}
