package com.example.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Notification
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository
    val allTransactions: LiveData<List<Transaction>>
    val allNotifications: LiveData<List<Notification>>
    val userProfile: LiveData<UserProfile?>
    val totalIncome: LiveData<Double?>
    val totalExpense: LiveData<Double?>

    init {
        val db = ExpenseDatabase.getDatabase(application)
        repository = ExpenseRepository(
            db.transactionDao(),
            db.notificationDao(),
            db.userProfileDao(),
            db.budgetDao()
        )
        allTransactions = repository.allTransactions
        allNotifications = repository.allNotifications
        userProfile = repository.userProfile
        totalIncome = repository.totalIncome
        totalExpense = repository.totalExpense
    }

    fun addTransaction(amount: Double, type: String, category: String = "General") = viewModelScope.launch {
        repository.insertTransaction(
            Transaction(
                amount = amount,
                type = type.lowercase(),
                category = category
            )
        )
        val message = if (type.lowercase() == "income") {
            "Added ₱$amount to your wallet ($category)."
        } else {
            "Spent ₱$amount on $category."
        }
        repository.insertNotification(Notification(message = message))
    }

    fun updateProfile(name: String, email: String) = viewModelScope.launch {
        repository.updateProfile(UserProfile(name = name, email = email))
    }

    fun clearAllData() = viewModelScope.launch {
        repository.clearAllData()
    }

    fun getBudgets(monthYear: String): LiveData<List<Budget>> {
        return repository.getBudgets(monthYear)
    }

    fun setBudget(category: String, amount: Double, monthYear: String) = viewModelScope.launch {
        repository.upsertBudget(Budget(category = category, limitAmount = amount, monthYear = monthYear))
    }
    
    fun getCategorySpending(category: String, monthYear: String): LiveData<Double?> {
        return repository.getCategorySpending(category, monthYear)
    }
}
