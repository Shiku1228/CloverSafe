package com.example.expensetracker.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.databinding.ActivityAddMoneyBinding
import com.example.expensetracker.viewmodel.ExpenseViewModel

class AddMoneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMoneyBinding
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDouble()
                viewModel.addTransaction(amount, "income")
                Toast.makeText(this, "Income added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
