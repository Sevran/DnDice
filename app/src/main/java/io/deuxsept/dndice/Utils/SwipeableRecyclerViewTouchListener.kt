package io.deuxsept.dndice.Utils

/*
 * Copyright 2013 Google Inc.
 * Copyright 2015 Bruno Romeu Nunes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.SystemClock
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ListView

import java.util.ArrayList
import java.util.Collections


/**
 * A [View.OnTouchListener] that makes the list items in a [android.support.v7.widget.RecyclerView]
 * dismissable by swiping.
 *
 *
 *
 * Example usage:
 *
 *
 *
 * SwipeDismissRecyclerViewTouchListener touchListener =
 * new SwipeDismissRecyclerViewTouchListener(
 * listView,
 * new SwipeDismissRecyclerViewTouchListener.OnDismissCallback() {
 * public void onDismiss(ListView listView, int[] reverseSortedPositions) {
 * for (int position : reverseSortedPositions) {
 * adapter.remove(adapter.getItem(position));
 * }
 * adapter.notifyDataSetChanged();
 * }
 * });
 * listView.setOnTouchListener(touchListener);
 * listView.setOnScrollListener(touchListener.makeScrollListener());
 *
 *
 *
 *
 * This class Requires API level 12 or later due to use of [ ].
 */
class SwipeableRecyclerViewTouchListener
/**
 * Constructs a new swipe touch listener for the given android.support.v7.widget.RecyclerView

 * @param RecyclerView The recycler view whose items should be dismissable by swiping.
 * *
 * @param Listener     The listener for the swipe events.
 */
(private val mRecyclerView: RecyclerView, private val mFgID: Int, private val mBgID: Int,
        private val mSwipeListener: SwipeableRecyclerViewTouchListener.SwipeListener) : RecyclerView.OnItemTouchListener {

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    // Cached ViewConfiguration and system-wide constant values
    private val mSlop: Int
    private val mMinFlingVelocity: Int
    private val mMaxFlingVelocity: Int
    private val ANIMATION_FAST: Long = 300
    private val ANIMATION_WAIT: Long = 300
    private var mViewWidth = 1 // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private val mPendingDismisses = ArrayList<PendingDismissData>()
    private var mDismissAnimationRefCount = 0
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var mSwiping: Boolean = false
    private var mSwipingSlop: Int = 0
    private var mVelocityTracker: VelocityTracker? = null
    private var mDownPosition: Int = 0
    private var mDownView: View? = null
    private var mPaused: Boolean = false
    private var mFinalDelta: Float = 0.toFloat()

    // Foreground view (to be swiped)
    // background view (to show)
    private var mFgView: View? = null
    private var mBgView: View? = null

    init {
        val vc = ViewConfiguration.get(mRecyclerView.context)
        mSlop = vc.scaledTouchSlop
        mMinFlingVelocity = vc.scaledMinimumFlingVelocity * 16
        mMaxFlingVelocity = vc.scaledMaximumFlingVelocity


        /**
         * This will ensure that this SwipeableRecyclerViewTouchListener is paused during list view scrolling.
         * If a scroll listener is already assigned, the caller should still pass scroll changes through
         * to this listener.
         */
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                setEnabled(newState != RecyclerView.SCROLL_STATE_DRAGGING)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            }
        })
    }

    /**
     * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
     * @param enabled Whether or not to watch for gestures.
     */
    fun setEnabled(enabled: Boolean) {
        mPaused = !enabled
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, motionEvent: MotionEvent): Boolean {
        return handleTouchEvent(motionEvent)
    }

    override fun onTouchEvent(rv: RecyclerView, motionEvent: MotionEvent) {
        handleTouchEvent(motionEvent)
    }

    private fun handleTouchEvent(motionEvent: MotionEvent): Boolean {
        if (mViewWidth < 2) {
            mViewWidth = mRecyclerView.width
        }

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (!mPaused) {
                    // Find the child view that was touched (perform a hit test)
                    val rect = Rect()
                    val childCount = mRecyclerView.childCount
                    val listViewCoords = IntArray(2)
                    mRecyclerView.getLocationOnScreen(listViewCoords)
                    val x = motionEvent.rawX.toInt() - listViewCoords[0]
                    val y = motionEvent.rawY.toInt() - listViewCoords[1]
                    var child: View
                    for (i in 0..childCount - 1) {
                        child = mRecyclerView.getChildAt(i)
                        child.getHitRect(rect)
                        if (rect.contains(x, y)) {
                            mDownView = child
                            break
                        }
                    }

                    if (mDownView != null) {
                        mDownX = motionEvent.rawX
                        mDownY = motionEvent.rawY
                        mDownPosition = mRecyclerView.getChildAdapterPosition(mDownView)
                        mVelocityTracker = VelocityTracker.obtain()
                        mVelocityTracker!!.addMovement(motionEvent)
                        mFgView = mDownView!!.findViewById(mFgID)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mVelocityTracker != null) {
                    if (mDownView != null && mSwiping) {
                        // cancel
                        mFgView!!.animate().translationX(0f).setDuration(ANIMATION_FAST).setListener(null)
                    }
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                    mDownX = 0f
                    mDownY = 0f
                    mDownView = null
                    mDownPosition = ListView.INVALID_POSITION
                    mSwiping = false
                    mBgView = null
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mVelocityTracker != null) {
                    mFinalDelta = motionEvent.rawX - mDownX
                    mVelocityTracker!!.addMovement(motionEvent)
                    mVelocityTracker!!.computeCurrentVelocity(1000)
                    val velocityX = mVelocityTracker!!.xVelocity
                    val absVelocityX = Math.abs(velocityX)
                    val absVelocityY = Math.abs(mVelocityTracker!!.yVelocity)
                    var dismiss = false
                    var dismissRight = false
                    if (Math.abs(mFinalDelta) > mViewWidth / 2 && mSwiping) {
                        dismiss = true
                        dismissRight = mFinalDelta > 0
                    } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                            && absVelocityY < absVelocityX && mSwiping) {
                        // dismiss only if flinging in the same direction as dragging
                        dismiss = velocityX < 0 == mFinalDelta < 0
                        dismissRight = mVelocityTracker!!.xVelocity > 0
                    }
                    if (dismiss &&
                            mDownPosition != ListView.INVALID_POSITION &&
                            mSwipeListener.canSwipe(mDownPosition)) {
                        // dismiss
                        val downView = mDownView // mDownView gets null'd before animation ends
                        val downPosition = mDownPosition
                        ++mDismissAnimationRefCount
                        mBgView!!.animate().alpha(1f).duration = ANIMATION_FAST
                        mFgView!!.animate().translationX((if (dismissRight) mViewWidth else -mViewWidth).toFloat()).setDuration(ANIMATION_FAST).setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                performDismiss(downView!!, downPosition)
                            }
                        })
                    } else {
                        // cancel
                        mFgView!!.animate().translationX(0f).setDuration(ANIMATION_FAST).setListener(null)
                    }
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                    mDownX = 0f
                    mDownY = 0f
                    mDownView = null
                    mDownPosition = ListView.INVALID_POSITION
                    mSwiping = false
                    mBgView = null
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mVelocityTracker != null && !mPaused) {
                    mVelocityTracker!!.addMovement(motionEvent)
                    val deltaX = motionEvent.rawX - mDownX
                    val deltaY = motionEvent.rawY - mDownY
                    if ((!mSwiping && Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) && deltaX >= 0) {
                        mSwiping = true
                        mSwipingSlop = if (deltaX > 0) mSlop else -mSlop
                    }

                    if (mSwiping) {
                        if (mBgView == null) {
                            mBgView = mDownView!!.findViewById(mBgID)
                            mBgView!!.visibility = View.VISIBLE
                        }
                        mFgView!!.translationX = deltaX - mSwipingSlop
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun performDismiss(dismissView: View, dismissPosition: Int) {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.

        val backgroundView = dismissView.findViewById(mBgID)
        val lp = dismissView.layoutParams
        val originalHeight = dismissView.height
        val deleteAble = booleanArrayOf(true)
        val animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(ANIMATION_FAST)

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                --mDismissAnimationRefCount

                if (mDismissAnimationRefCount > 0) return

                mDismissAnimationRefCount = 0
                // No active animations, process all pending dismisses.
                // Sort by descending position
                Collections.sort(mPendingDismisses)

                val dismissPositions = IntArray(mPendingDismisses.size)
                for (i in mPendingDismisses.indices.reversed()) {
                    dismissPositions[i] = mPendingDismisses[i].position
                }
                mSwipeListener.onDismissedBySwipe(mRecyclerView, dismissPositions)

                // Reset mDownPosition to avoid MotionEvent.ACTION_UP trying to start a dismiss
                // animation with a stale position
                mDownPosition = ListView.INVALID_POSITION

                var lparams: ViewGroup.LayoutParams
                for (pendingDismiss in mPendingDismisses) {
                    // Reset view presentation
                    pendingDismiss.view.findViewById(mFgID).translationX = 0f
                    lparams = pendingDismiss.view.layoutParams
                    lparams.height = originalHeight
                    pendingDismiss.view.layoutParams = lparams
                }

                // Send a cancel event
                val time = SystemClock.uptimeMillis()
                val cancelEvent = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
                mRecyclerView.dispatchTouchEvent(cancelEvent)

                mPendingDismisses.clear()
            }
        })

        // Animate the dismissed list item to zero-height
        animator.addUpdateListener { valueAnimator ->
            lp.height = valueAnimator.animatedValue as Int
            dismissView.layoutParams = lp
        }

        val pendingDismissData = PendingDismissData(dismissPosition, dismissView)
        mPendingDismisses.add(pendingDismissData)

        //fade out background view
        backgroundView.animate().alpha(0f).setDuration(ANIMATION_WAIT).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (deleteAble[0]) animator.start()
            }
        })

        //cancel animate when click(actually touch) background view.
        backgroundView.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    deleteAble[0] = false
                    --mDismissAnimationRefCount
                    mPendingDismisses.remove(pendingDismissData)
                    backgroundView.playSoundEffect(0)
                    backgroundView.setOnTouchListener(null)
                }
            }
            false
        }
    }

    /**
     * The callback interface used by [SwipeableRecyclerViewTouchListener] to inform its client
     * about a swipe of one or more list item positions.
     */
    interface SwipeListener {
        /**
         * Called to determine whether the given position can be swiped.
         */
        fun canSwipe(position: Int): Boolean

        /**
         * Called when the item has been dismissed by swiping to the left.

         * @param recyclerView           The originating android.support.v7.widget.RecyclerView.
         * *
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         * *                               order for convenience.
         */
        fun onDismissedBySwipe(recyclerView: RecyclerView, reverseSortedPositions: IntArray)

    }

    internal inner class PendingDismissData(var position: Int, var view: View) : Comparable<PendingDismissData> {
        override fun compareTo(other: PendingDismissData): Int {
            // Sort by descending position
            return other.position - position
        }
    }
}