package com.example.expensetracker.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.databinding.ActivityRecordsBinding
import com.example.expensetracker.ui.adapter.TransactionAdapter
import com.example.expensetracker.viewmodel.ExpenseViewModel

class RecordsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = TransactionAdapter()
        binding.rvRecords.layoutManager = LinearLayoutManager(this)
        binding.rvRecords.adapter = adapter

        viewModel.allTransactions.observe(this) { transactions ->
            adapter.submitList(transactions)
        }
    }
}
