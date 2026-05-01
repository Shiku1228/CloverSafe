package com.example.expensetracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ActivityDashboardBinding
import com.example.expensetracker.viewmodel.ExpenseViewModel

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.totalIncome.observe(this) { income ->
            viewModel.totalExpense.observe(this) { expense ->
                val balance = (income ?: 0.0) - (expense ?: 0.0)
                binding.tvBalance.text = "₱ ${String.format("%.2f", balance)}"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCardAddMoney.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click))
            startActivity(Intent(this, AddMoneyActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnCardRecords.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click))
            startActivity(Intent(this, RecordsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnCardNotifications.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click))
            startActivity(Intent(this, NotificationsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnCardProfile.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click))
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
