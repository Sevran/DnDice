package io.deuxsept.dndice.MainView

import android.content.Context
import android.graphics.Interpolator
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.*
import android.widget.TextView
import io.deuxsept.dndice.Database.DatabaseHelper
import io.deuxsept.dndice.Model.DiceRoll
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.Utils
import java.util.*

class HomeFragment : Fragment() {

    /**
     * Home Views
     */
    lateinit var mDisplay: TextView
    lateinit var mDisplayWarning: View
    var mDisplayWarningShowed: Boolean = false
    lateinit var mRollButton: View

    /**
     * Result Views
     */
    lateinit var mResultView: View
    lateinit var mFormula: TextView
    lateinit var mResult: TextView
    lateinit var mDetail: TextView
    lateinit var mFavoriteFab: FloatingActionButton
    lateinit var mCloseResFab: FloatingActionButton
    lateinit var mReplayFab: FloatingActionButton
    lateinit var mDb: DatabaseHelper

    var width: Int = 0
    var height: Int = 0

    var data_stack: Stack<String> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = DatabaseHelper.getInstance(context)!!
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        width = metrics.widthPixels
        height = metrics.heightPixels
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_home, container, false)

        mDisplayWarning = view.findViewById(R.id.display_empty_warning)
        mDisplay = view.findViewById(R.id.display) as TextView
        mDisplay.setOnClickListener {
            if (data_stack.size > 0) {
                data_stack.pop()
                refresh_formula()
            }
        }
        mDisplay.setOnLongClickListener {
            data_stack.clear()
            refresh_formula()
            true
        }

        mRollButton = view.findViewById(R.id.display)
        mRollButton = view.findViewById(R.id.roll_button)
        mRollButton.setOnClickListener {
            if (data_stack.size > 0) {
                executeRoll()
                openResultView()
            } else {
                showDisplayWarning()
            }
        }

        initResultViewVars(view)

        return view
    }

    fun showDisplayWarning() {
        mDisplayWarningShowed = true
        mDisplayWarning.animate()
                .setDuration(300)
                .alpha(1f)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    val blinkAnimation: AlphaAnimation = AlphaAnimation(1f, 0f)
                    blinkAnimation.duration = 300
                    blinkAnimation.interpolator = LinearInterpolator()
                    blinkAnimation.repeatCount = 3
                    blinkAnimation.repeatMode = Animation.REVERSE
                    mDisplayWarning.startAnimation(blinkAnimation)
                }.start()
    }

    fun hideDisplayWarning() {
        mDisplayWarningShowed = false
        mDisplayWarning.animate()
                .setDuration(300)
                .alpha(0f)
                .setInterpolator(AccelerateInterpolator()).start()
    }

    companion object {
        private var mFragment: HomeFragment? = null
        var mResultViewOpened: Boolean = false

        fun newInstance(): HomeFragment {
            if (mFragment == null) {
                mFragment = HomeFragment()
            }
            return mFragment as HomeFragment
        }
    }

    fun executeRoll() {
        val dice: DiceRoll = DiceRoll.from_string(data_stack.joinToString(""))
        val result = dice.roll()
        mFormula.text = dice.formula()
        mDetail.text = result.as_readable_string()
        mResult.text = result.as_total().toString()

        if (!result.as_readable_string().equals("") && !result.as_total().toString().equals(""))
            mDb.addRecentRoll(RollModel(
                    mFormula.text.toString(),
                    mResult.text.toString(),
                    mDetail.text.toString()
            ))
    }

    fun openResultView() {
        Utils.circularReveal(mResultView, width/2, height)
        mFavoriteFab.show()
        mCloseResFab.show()
        mReplayFab.show()
        mResultViewOpened = true
    }

    fun initResultViewVars(view: View) {
        mResultView = view.findViewById(R.id.result_view)
        mFormula = view.findViewById(R.id.formula_var) as TextView
        mResult = view.findViewById(R.id.result_var) as TextView
        mDetail = view.findViewById(R.id.detail_var) as TextView

        mFavoriteFab = view.findViewById(R.id.replay_result_fab) as FloatingActionButton
        mFavoriteFab.setOnClickListener {
            favoriteRoll()
        }
        mCloseResFab = view.findViewById(R.id.close_result_fab) as FloatingActionButton
        mCloseResFab.setOnClickListener {
            closeResultView()
        }
        mReplayFab = view.findViewById(R.id.fav_result_fab) as FloatingActionButton
        mReplayFab.setOnClickListener {
            executeRoll()
        }
    }

    fun favoriteRoll() {

    }

    fun closeResultView() {
        mFavoriteFab.hide()
        mCloseResFab.hide()
        mReplayFab.hide()
        val fabPos = mResultView.height - Utils.convertDpToPixel(90, context)
        Utils.circularUnreveal(mResultView, mResultView.width/2, fabPos.toInt())
        data_stack.clear()
        refresh_formula()
        mResultViewOpened = false
    }

    fun push_element_to_stack(view: View) {
        if (mDisplayWarningShowed) hideDisplayWarning()
        // Check if we're not trying to add too long of a number, without preventing from adding a +/-
        if (view.id != R.id.button_moins && view.id != R.id.button_plus)
            if (data_stack.size - data_stack.lastIndexOf("+") > 4 && data_stack.size - data_stack.lastIndexOf("-") > 4)
                return

        push_with_auto_symbols(when(view.id) {
            R.id.button_0 -> "0"
            R.id.button_1 -> "1"
            R.id.button_2 -> "2"
            R.id.button_3 -> "3"
            R.id.button_4 -> "4"
            R.id.button_5 -> "5"
            R.id.button_6 -> "6"
            R.id.button_7 -> "7"
            R.id.button_8 -> "8"
            R.id.button_9 -> "9"
            R.id.button_plus -> "+"
            R.id.button_moins -> "-"
            R.id.d2 -> "d2"
            R.id.d3 -> "d3"
            R.id.d4 -> "d4"
            R.id.d6 -> "d6"
            R.id.d8 -> "d8"
            R.id.d10 -> "d10"
            R.id.d12 -> "d12"
            R.id.d20 -> "d20"
            R.id.d100 -> "d100"
            else -> ""
        })

        refresh_formula()
    }

    fun push_with_auto_symbols(value: String) {
        if(data_stack.size > 0) {
            val last = data_stack.peek()

            if (last.contains('d') && value != "+" && value != "-")
                data_stack.push("+")
        }

        data_stack.push(value)
    }

    fun refresh_formula() {
        mDisplay.text = data_stack.joinToString("")
    }
}
