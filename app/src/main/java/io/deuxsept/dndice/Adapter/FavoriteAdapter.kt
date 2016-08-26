package io.deuxsept.dndice.Adapter

import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import io.deuxsept.dndice.Database.DatabaseHelper
import io.deuxsept.dndice.MainView.FavoriteFragment
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.R
import io.deuxsept.dndice.Utils.ItemTouchHelperAdapter
import java.util.*

/**
 * Created by Luo
 * on 15/08/2016.
 */
class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>, ItemTouchHelperAdapter {

    private lateinit var mFragment: FavoriteFragment
    private lateinit var mView: View
    private val mList = ArrayList<RollModel>()
    private var lastPosition = -1
    private lateinit var mDragStartListener: OnDragStartListener
    private lateinit var mLocale: Locale
    private lateinit var mDb: DatabaseHelper

    @Suppress("DEPRECATION")
    constructor(fragment: FavoriteFragment, view: View, dragStartListener: OnDragStartListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mLocale = fragment.context.resources.configuration.locales.get(0)
        else
            mLocale = fragment.context.resources.configuration.locale
        mFragment = fragment
        mView = view
        mDragStartListener = dragStartListener
        mDb = DatabaseHelper(fragment.context)
    }

    interface OnDragStartListener {
        fun onDragStarted(viewHolder: RecyclerView.ViewHolder)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mLayout: View
        internal var mFormula: TextView
        internal var mReorderButton: ImageView
        internal var mName: TextView

        init {
            mLayout = itemView.findViewById(R.id.row_layout)
            mFormula = itemView.findViewById(R.id.recent_formula) as TextView
            mReorderButton = itemView.findViewById(R.id.fav_reorder_button) as ImageView
            mName = itemView.findViewById(R.id.favorite_name) as TextView
        }

        internal fun clearAnimation() {
            mLayout.clearAnimation()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val itemView = LayoutInflater.from(mFragment.context).inflate(R.layout.row_favorite, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: RollModel = mList[position]
        holder.mFormula.text = model.formula
        holder.mName.text = if (model.name.trim() != "") model.name else "Unnamed"
        holder.mReorderButton.setOnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) === MotionEvent.ACTION_DOWN)
                mDragStartListener.onDragStarted(holder)
            false
        }

        val animation = AnimationUtils.loadAnimation(mFragment.context, R.anim.item_slide_in_from_left)
        setAnimation(holder.mLayout, position, animation)
    }

    private fun setAnimation(viewToAnimate: View, position: Int, animation: Animation) {
        if (position > lastPosition) {
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.clearAnimation()
    }

    override fun onItemDismiss(position: Int) {
        val model: RollModel = mList[position]
        mDb.deleteFavoriteRoll(model.id)
        mList.removeAt(position)
        notifyItemRemoved(position)
        mFragment.shouldShowEmptyState()
        val snackbar = Snackbar.make(mView, "Favorite deleted.", Snackbar.LENGTH_LONG).setAction("UNDO") {
            mDb.addFavoriteRoll(model)
            mList.add(model)
            notifyItemInserted(mList.size)
            mFragment.shouldShowEmptyState()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(mFragment.context, R.color.colorAccent))
        snackbar.show()
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