package io.deuxsept.dndice.Utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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

        fun Log_i(tag: String, string: String) {
            if (DEBUG) Log.e(tag, string)
        }
        fun Log_d(tag: String, string: String) {
            if (DEBUG) Log.e(tag, string)
        }
        fun Log_e(tag: String, string: String) {
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

        fun circularUnreveal(view: View, width: Int, height: Int) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val initialRadius = Math.hypot(width.toDouble(), height.toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(view, width, height, initialRadius, 0.toFloat())
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
                anim.start()
            } else {
                view.visibility = View.GONE
            }
        }
    }
}
