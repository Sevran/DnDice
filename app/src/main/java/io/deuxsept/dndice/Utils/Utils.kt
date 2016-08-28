package io.deuxsept.dndice.Utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils

/**
 * Created by Luo
 * 09/08/2016.
 */

class Utils() {

    companion object {
        val DEBUG: Boolean = true

        fun log_i(tag: String, string: String) {
            if (DEBUG) Log.i(tag, string)
        }
        fun log_d(tag: String, string: String) {
            if (DEBUG) Log.d(tag, string)
        }
        fun log_e(tag: String, string: String) {
            if (DEBUG) Log.e(tag, string)
        }

        fun circularReveal(view: View, width: Int, height: Int) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val finalRadius = Math.hypot(width.toDouble(), height.toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(view, width, height, 0.toFloat(), finalRadius)
                view.visibility = View.VISIBLE
                anim.start()
            } else {
                view.visibility = View.VISIBLE
            }
        }

        fun convertDpToPixel(dp: Int, context: Context): Int {
            val resources = context.resources
            val metrics = resources.displayMetrics
            val px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
            return px
        }
    }
}
