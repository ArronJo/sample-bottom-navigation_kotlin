package com.hanwhalife.ui.activity.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snc.sample.bottom_navigation_kotlin.R

class PackageInfoAdapter(
    private val activity: Activity?,
    private val recyclerView: RecyclerView,
    private var listItems: ArrayList<PackageInfo>? = null,
    private val callback: Callback? = null
) : RecyclerView.Adapter<PackageInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(activity)
                .inflate(R.layout.cell_package_info, parent, false)
        return ViewHolder(view, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activity, listItems?.get(position))
    }

    override fun getItemCount(): Int {
        return listItems?.size ?: 0
    }

    class ViewHolder(itemView: View, callback: Callback? = null) :
        RecyclerView.ViewHolder(itemView) {

        private var appIconView: ImageView? = null
        private var appNameView: TextView? = null
        private var appPackageNameView: TextView? = null

        init {
            appIconView = itemView.findViewById(R.id.app_icon)
            appNameView = itemView.findViewById(R.id.app_name)
            appPackageNameView = itemView.findViewById(R.id.app_package_name)

            val remove = itemView.findViewById<AppCompatButton>(R.id.remove)
            remove.setOnClickListener {
                val position = adapterPosition
                callback?.onRemoveClicked(position)
            }
        }

        fun bind(activity: Activity?, item: PackageInfo?) {
            item?.let {
                activity?.let { iit ->
                    val icon: Drawable = item.applicationInfo.loadIcon(iit.packageManager)
                    val appName: String =
                        item.applicationInfo.loadLabel(iit.packageManager).toString()

                    this.appIconView?.setImageDrawable(icon)
                    this.appNameView?.text = appName
                }
                val packageName: String = item.packageName
                this.appPackageNameView?.text = packageName
            }
        }
    }

    private fun setItems(list: ArrayList<PackageInfo>) {
        this.listItems = list
    }

    fun submit(list: ArrayList<PackageInfo>) {
        setItems(list)

        val lm: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val first = lm.findFirstVisibleItemPosition()
        val last = lm.findLastVisibleItemPosition()
        if (first < 0 || last <= 0) {
            reload()
        } else {
            refresh(first, last)
        }
    }

    fun getItems(): ArrayList<PackageInfo>? {
        return listItems
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reload() {
        notifyDataSetChanged()
    }

    private fun refresh(positionStart: Int, itemCount: Int) {
        notifyItemRangeChanged(positionStart, itemCount)
    }

    interface Callback {
        fun onRemoveClicked(position: Int)
    }
}