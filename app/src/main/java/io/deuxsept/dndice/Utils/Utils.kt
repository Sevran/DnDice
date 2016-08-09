package io.deuxsept.dndice.Utils

import android.util.Log

/**
 * Created by Luo
 * 09/08/2016.
 */

class Utils() {

    companion object {
        val DEBUG: Boolean = true

        fun Log_i(tag: String, string: String) {
            if (DEBUG) Log.e(tag, string)
        }
        fun Log_d(tag: String, string: String) {
            if (DEBUG) Log.e(tag, string)
        }
        fun Log_e(tag: String, string: String) {
            if (DEBUG) Log.e(tag, string)
        }
    }
}
