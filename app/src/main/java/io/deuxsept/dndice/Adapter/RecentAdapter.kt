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

    private var mFragment: RecentFragment? = null
    private val mList = ArrayList<RollModel>()
    private var lastPosition = 0
    var mLocale: Locale? = null

    companion object {
        var mListener: ViewHolder.onItemClick? = null
    }

    @Suppress("DEPRECATION")
    constructor(fragment: RecentFragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocale = fragment.context.resources.configuration.locales.get(0)
        } else {
            mLocale = fragment.context.resources.configuration.locale
        }
        mFragment = fragment
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
        val itemView = LayoutInflater.from(mFragment?.context).inflate(R.layout.row_recent, parent, false)
        return ViewHolder(itemView, object : ViewHolder.onItemClick {
            override fun onClick(recentView: View, position: Int) {
                val model: RollModel = mList[position]
                model.fav = !model.fav
                notifyItemChanged(position)
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: RollModel = mList[position]

        holder.mResult.text = model.result
        holder.mFormula.text = model.formula
        holder.mDetail.text = model.detail
        // Pourquoi ça marche pas laaaaaaaaaaaaaaaaaaaaaa
        //holder.mFavButton.setImageResource((model.fav) R.drawable.ic_favorite_24dp else R.drawable.ic_favorite_outlined_24dp)
        if (model.fav)  {
            holder.mFavButton.setImageResource(R.drawable.ic_favorite_24dp)
            holder.mFavButton.setColorFilter(ContextCompat.getColor(mFragment?.context, R.color.colorAccent), PorterDuff.Mode.SRC_IN)
        } else {
            holder.mFavButton.setImageResource(R.drawable.ic_favorite_outlined_24dp)
            holder.mFavButton.setColorFilter(ContextCompat.getColor(mFragment?.context, R.color.md_grey_500), PorterDuff.Mode.SRC_IN)
        }

        val dateString = SimpleDateFormat("dd MMM\nHH:mm", mLocale).format(Date(model.timestamp))
        holder.mTimestamp.text = dateString

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

    fun removeItem(id: Int) {
        var i: Int = 0
        for (id1 in mList) {
            if (id1.id == id) {
                mList.remove(id1)
                notifyItemRemoved(i)
                break
            }
            i++
        }
    }

    fun addAll(models: List<RollModel>) {
        mList.addAll(models)
        notifyDataSetChanged()
    }

    fun addItemAndReorder(model: RollModel) {
        val tempList = ArrayList<RollModel>()
        tempList.addAll(mList)
        tempList.add(model)
        Collections.sort(tempList, { r1, r2 -> r1.timestamp.compareTo(r2.timestamp) })
        mList.add(tempList.indexOf(model), model)
        notifyItemInserted(tempList.indexOf(model))
    }

    override fun getItemCount(): Int {
        return mList.count()
    }
}