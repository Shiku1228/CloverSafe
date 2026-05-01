package com.example.expensetracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.model.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = getItem(position)
        holder.bind(notification)
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left))
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(notification: Notification) {
            tvMessage.text = notification.message
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvTime.text = sdf.format(Date(notification.timestamp))
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean = oldItem == newItem
    }
}
