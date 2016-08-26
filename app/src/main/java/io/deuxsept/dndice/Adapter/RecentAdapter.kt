package io.deuxsept.dndice.Adapter

import android.graphics.PorterDuff
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import io.deuxsept.dndice.Database.DatabaseHelper
import io.deuxsept.dndice.MainView.RecentFragment
import io.deuxsept.dndice.Model.RollModel
import io.deuxsept.dndice.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Luo
 * 13/08/2016.
 */
class RecentAdapter : RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private lateinit var mFragment: RecentFragment
    private val mList = ArrayList<RollModel>()
    private var lastPosition = -1
    private lateinit var mLocale: Locale
    private lateinit var mDb: DatabaseHelper

    companion object {
        var mListener: ViewHolder.onItemClick? = null
    }

    @Suppress("DEPRECATION")
    constructor(fragment: RecentFragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mLocale = fragment.context.resources.configuration.locales.get(0)
        else
            mLocale = fragment.context.resources.configuration.locale
        mFragment = fragment
        mDb = DatabaseHelper(fragment.context)
    }

    class ViewHolder(itemView: View, listener: ViewHolder.onItemClick) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mLayout: View
        internal var mResult: TextView
        internal var mFormula: TextView
        internal var mDetail: TextView
        internal var mTimestamp: TextView
        internal var mFavButton: ImageView

        init {
            mListener = listener
            mLayout = itemView.findViewById(R.id.row_layout)
            mResult = itemView.findViewById(R.id.recent_result) as TextView
            mFormula = itemView.findViewById(R.id.recent_formula) as TextView
            mDetail = itemView.findViewById(R.id.recent_detail) as TextView
            mTimestamp = itemView.findViewById(R.id.recent_timestamp) as TextView
            mFavButton = itemView.findViewById(R.id.recent_fav_button) as ImageView
            mFavButton.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mListener!!.onClick(v, adapterPosition)
        }

        internal fun clearAnimation() {
            mLayout.clearAnimation()
        }

        interface onItemClick {
            fun onClick(caller: View, position: Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapter.ViewHolder {
        val itemView = LayoutInflater.from(mFragment.context).inflate(R.layout.row_recent, parent, false)
        return ViewHolder(itemView, object : ViewHolder.onItemClick {
            override fun onClick(caller: View, position: Int) {
                val model: RollModel = mList[position]
                val fav: Boolean = !model.fav
                var i: Int = 0
                var was_added = false
                for (m in mList) {
                    if (m.formula == mList[position].formula) {
                        if (!was_added) {
                            if (fav) mDb.addFavoriteRoll(m)
                            else mDb.deleteFavoriteRoll(m.id)

                            was_added = true
                        }
                        m.fav = fav
                        notifyItemChanged(i)
                    }
                    i++
                }
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: RollModel = mList[position]
        holder.mResult.text = model.result
        holder.mFormula.text = model.formula
        holder.mDetail.text = model.detail
        if (model.fav)  {
            holder.mFavButton.setImageResource(R.drawable.ic_favorite_24dp)
            holder.mFavButton.setColorFilter(ContextCompat.getColor(mFragment.context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
        } else {
            holder.mFavButton.setImageResource(R.drawable.ic_favorite_outlined_24dp)
            holder.mFavButton.setColorFilter(ContextCompat.getColor(mFragment.context, R.color.md_grey_500), PorterDuff.Mode.SRC_IN)
        }
        holder.mTimestamp.text = SimpleDateFormat("dd MMM - HH:mm", mLocale).format(Date(model.timestamp))
        setAnimation(holder.mLayout, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(mFragment.context, R.anim.item_slide_in_from_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.clearAnimation()
    }

    fun addAll(models: List<RollModel>) {
        mList.addAll(models)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.count()
    }
}