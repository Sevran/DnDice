package io.deuxsept.dndice.MainView

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.PopupMenu
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.*
import android.widget.EditText
import android.widget.TextView
import io.deuxsept.dndice.Database.DatabaseHelper
import io.deuxsept.dndice.Model.DiceRoll
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.Utils
import io.deuxsept.dndice.Utils.find
import java.util.*

class HomeFragment : android.support.v4.app.Fragment() {
    /**
     * Home Views
     */
        val mDisplay: TextView by lazy    { find<TextView>(R.id.display)}
        val mBackspace: View by lazy      { find<View>(R.id.backspace_button) }
        val mFav: View by lazy            { find<View>(R.id.fav_button) }
        val mDisplayWarning: View by lazy { find<View>(R.id.display_empty_warning) }
        val mRollButton: View by lazy     { find<View>(R.id.roll_button) }
        val mLastRoll: TextView by lazy   { activity.find<TextView>(R.id.last_roll)}
        val mResultView: View by lazy     { find<View>(R.id.result_view) }
        val mFormula: TextView by lazy    { find<TextView>(R.id.formula_var) }
        val mResult: TextView by lazy     { find<TextView>(R.id.result_var) }
        val mDetail: TextView by lazy     { find<TextView>(R.id.detail_var) }
        val mFavoriteFab: FloatingActionButton by lazy     { find<FloatingActionButton>(R.id.fav_result_fab) }
        val mCloseResFab: FloatingActionButton by lazy     { find<FloatingActionButton>(R.id.close_result_fab) }
        val mReplayFab: FloatingActionButton by lazy       { find<FloatingActionButton>(R.id.replay_result_fab)}

        var mWidth: Int = 0
        var mHeight: Int = 0

    inner class HomeState {
        lateinit var mDb: DatabaseHelper

        var mDataStack: Stack<String> = Stack()
        var mDisplayWarningShowed: Boolean = false
        var mResultViewOpened: Boolean = false
    }

    var mState = HomeState()

    companion object {
        private var mFragment: HomeFragment? = null

        fun newInstance(): HomeFragment {
            if (mFragment == null) {
                mFragment = HomeFragment()
            }
            return mFragment as HomeFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mState.mDb = DatabaseHelper.getInstance(context)!!

        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        mWidth = metrics.widthPixels
        mHeight = metrics.heightPixels
    }

    override fun onResume() {
        super.onResume()
        refresh_formula()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater!!.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventHandlers()
    }

    /*
     * Sets up the various even handlers for the UI
     */
    private fun initEventHandlers() {
        // Delete one element on single backspace tap
        mBackspace.setOnClickListener {
            if (mState.mDataStack.size > 0) {
                mState.mDataStack.pop()
                refresh_formula()
            }
        }
        // Delete entire formula on long backspace press
        mBackspace.setOnLongClickListener {
            mState.mDataStack.clear()
            refresh_formula()
            true
        }

        mLastRoll.setOnClickListener {
            if (mLastRoll.text != "" && !mState.mResultViewOpened) {
                openResultView()
            }
        }

        // Fill the stack with appropriate data on favorite select
        mFav.setOnClickListener {
            val menu = PopupMenu(context, mFav)
            menu.setOnMenuItemClickListener { item ->
                mState.mDataStack.clear()
                push_with_auto_symbols(mState.mDb.getFavorite(item.itemId)?.formula)

                true
            }
            for ((formula, result, detail, name, id) in mState.mDb.getAllFavoritesRolls()) {
                menu.menu.add(0, id, Menu.NONE, "$name ($formula)")
            }
            menu.show()
        }

        // Roll the dices on tap
        mRollButton.setOnClickListener {
            if (mState.mDataStack.size > 0) {
                executeRoll()
                if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_no_roll_window", false)) {
                    openResultView()
                }
            } else {
                showDisplayWarning()
            }
        }

        mFavoriteFab.setOnClickListener {
            favoriteRoll()
        }
        mCloseResFab.setOnClickListener {
            closeResultView()
        }
        mReplayFab.setOnClickListener {
            executeRoll()
        }
    }

    /**
     * UI Modifications
     */
    fun showDisplayWarning() {
        mState.mDisplayWarningShowed = true
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
        mState.mDisplayWarningShowed = false
        mDisplayWarning.animate()
                .setDuration(300)
                .alpha(0f)
                .setInterpolator(AccelerateInterpolator()).start()
    }

    fun openResultView() {
        Utils.circularReveal(mResultView, mWidth /2, mHeight)
        mFavoriteFab.show()
        mCloseResFab.show()
        mReplayFab.show()
        mState.mResultViewOpened = true
    }

    fun closeResultView() {
        raise()
        mState.mDataStack.clear()
        refresh_formula()
        mState.mResultViewOpened = false
    }

    fun raise() {
        val objectAnimator = ObjectAnimator.ofInt(mResultView, "bottom", mResultView.bottom, mResultView.top)
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) { }
            override fun onAnimationCancel(p0: Animator?) { }
            override fun onAnimationStart(animation: Animator) { }
            override fun onAnimationEnd(animation: Animator) {
                mResultView.visibility = View.GONE
            }
        })
        objectAnimator.start()
    }

    /**
     * Data Operations
     */
    fun executeRoll() {
        val dice: DiceRoll = DiceRoll.from_string(mState.mDataStack.joinToString(""))
        val result = dice.roll()
        mFormula.text = dice.formula()
        mDetail.text = result.as_readable_string()
        mResult.text = result.as_total_string()
        mLastRoll.text = result.as_total_string()

        if (!result.as_readable_string().equals("") && !result.as_total().toString().equals("")) {
            mState.mDb.addRecentRoll(RollModel(
                    dice.formula(),
                    result.as_total_string(),
                    result.as_readable_string()
            ))
        }

    }

    fun favoriteRoll() {
        val dialog: Dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_add_favorite)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
        val name: EditText = dialog.findViewById(R.id.roll_name) as EditText
        name.setText(mFormula.text.toString(), TextView.BufferType.EDITABLE)
        name.selectAll()
        dialog.findViewById(R.id.add_roll_button).setOnClickListener {
            mState.mDb.addFavoriteRoll(RollModel(mFormula.text.toString(),"","",name.text.toString()))
            dialog.dismiss()
        }
    }

    fun push_element_to_stack(view: View) {
        if (mState.mDisplayWarningShowed)
            hideDisplayWarning()

        // Check if we're not trying to add too long of a number, without preventing from adding a +/-
        if (view.id != R.id.button_moins && view.id != R.id.button_plus)
            if (mState.mDataStack.size - mState.mDataStack.lastIndexOf("+") > 4 &&
                    mState.mDataStack.size - mState.mDataStack.lastIndexOf("-") > 4)
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

    fun push_with_auto_symbols(value: String?) {
        if (value == null) return
        if (mState.mDataStack.size > 0 && mState.mDataStack.peek().contains('d') && value != "+" && value != "-") {
            mState.mDataStack.push("+")
        }
        mState.mDataStack.push(value)
    }

    fun refresh_formula() {
        var formula = ""
        mState.mDataStack.forEach {
            item -> formula += if (item == "+" || item == "-") " $item " else item
        }
        mDisplay.text = formula
    }
}
