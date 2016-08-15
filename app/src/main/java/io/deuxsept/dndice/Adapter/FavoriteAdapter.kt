package io.deuxsept.dndice.Adapter

import android.graphics.Color
import android.os.Build
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import io.deuxsept.dndice.MainView.FavoriteFragment
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.ItemTouchHelperAdapter
import io.deuxsept.dndice.Utils.ItemTouchHelperViewHolder
import java.util.*

/**
 * Created by Luo
 * on 15/08/2016.
 */

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>, ItemTouchHelperAdapter {

    private var mFragment: FavoriteFragment? = null
    private val mList = ArrayList<RollModel>()
    private var lastPosition = -1
    private lateinit var mDragStartListener: OnDragStartListener
    var mLocale: Locale? = null

    @Suppress("DEPRECATION")
    constructor(fragment: FavoriteFragment, dragStartListener: OnDragStartListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocale = fragment.context.resources.configuration.locales.get(0)
        } else {
            mLocale = fragment.context.resources.configuration.locale
        }
        mFragment = fragment
        mDragStartListener = dragStartListener
    }

    interface OnDragStartListener {
        fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mLayout: View
        internal var mFormula: TextView
        internal var mReorderButton: ImageView

        init {
            mLayout = itemView.findViewById(R.id.row_layout)
            mFormula = itemView.findViewById(R.id.recent_formula) as TextView
            mReorderButton = itemView.findViewById(R.id.fav_reorder_button) as ImageView
        }

        internal fun clearAnimation() {
            mLayout.clearAnimation()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val itemView = LayoutInflater.from(mFragment?.context).inflate(R.layout.row_favorite, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: RollModel = mList[position]
        holder.mFormula.text = model.formula
        holder.mReorderButton.setOnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) === MotionEvent.ACTION_DOWN) {
                mDragStartListener.onDragStarted(holder)
            }
            false
        }
        setAnimation(holder.mLayout, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(mFragment?.context, R.anim.item_slide_in_from_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.clearAnimation()
    }

    override fun onItemDismiss(position: Int) {
        mList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev = mList.removeAt(fromPosition)
        mList.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun addAll(models: List<RollModel>) {
        mList.addAll(models)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.count()
    }
}