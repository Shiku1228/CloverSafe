package com.example.expensetracker.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetracker.databinding.ActivityProfileBinding
import com.example.expensetracker.viewmodel.ExpenseViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.userProfile.observe(this) { profile ->
            profile?.let {
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty()) {
                viewModel.updateProfile(name, email)
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
