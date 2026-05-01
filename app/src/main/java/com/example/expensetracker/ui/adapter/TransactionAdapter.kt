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
import com.example.expensetracker.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
        // Add animation
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.fade_in))
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(transaction: Transaction) {
            tvAmount.text = "₱ ${String.format("%.2f", transaction.amount)}"
            tvType.text = transaction.type.capitalize()
            tvType.setTextColor(if (transaction.type == "income") 0xFF4CAF50.toInt() else 0xFFF44336.toInt())
            
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            tvDate.text = sdf.format(Date(transaction.timestamp))
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean = oldItem == newItem
    }
}
