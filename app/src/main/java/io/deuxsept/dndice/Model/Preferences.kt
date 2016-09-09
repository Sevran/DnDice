package io.deuxsept.dndice.Model

import android.content.Context
import android.support.v7.preference.PreferenceManager

/**
 * Provides utilities to easily access shared preferences
 */
class Preferences {
    /**
     * Context used to retrieve the default shared preferences
     */
    private var _ctx: Context? = null

    /**
     * Is the Night mode forced?
     */
    val NightModeForced: Boolean
        get() = checkContextAndGet("pref_color_theme", false)

    /**
     * Do we hide the roll window and only update the toolbar roll info?
     */
    val HideRollWindow: Boolean
        get() = checkContextAndGet("pref_no_roll_window", false)

    /**
     * Do we roll as soon as we select a favourite?
     */
    val QuickFavouritesRoll: Boolean
        get() = checkContextAndGet("pref_quick_favorite_roll", false)

    /**
     * Ensures that the context has properly been set, then returns the value with the
     * appropriate type
     * @throws NoSuchMethodException if it doesn't know what to do with this type
     */
    private inline fun <reified T> checkContextAndGet(key: String, default: T): T {
        if (_ctx == null)
            throw IllegalStateException("Please setup the context before getting a key")

        var prefs = PreferenceManager.getDefaultSharedPreferences(_ctx)

        return when (default) {
            is Boolean -> prefs.getBoolean(key, default) as T
            is Int     -> prefs.getInt(key, default) as T
            else -> throw NoSuchMethodException("Not implemented for this particular type")
        }
    }

    private constructor(context: Context) {
        this._ctx = context
    }

    companion object {
        var prefs: Preferences? = null

        fun getInstance(context: Context): Preferences {
            return when(prefs) {
                null -> Preferences(context)
                else -> prefs as Preferences
            }
        }
    }
}