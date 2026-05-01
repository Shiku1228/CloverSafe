package com.example.expensetracker.repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.data.dao.BudgetDao
import com.example.expensetracker.data.dao.NotificationDao
import com.example.expensetracker.data.dao.TransactionDao
import com.example.expensetracker.data.dao.UserProfileDao
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Notification
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile

class ExpenseRepository(
    private val transactionDao: TransactionDao,
    private val notificationDao: NotificationDao,
    private val userProfileDao: UserProfileDao,
    private val budgetDao: BudgetDao
) {
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val allNotifications: LiveData<List<Notification>> = notificationDao.getAllNotifications()
    val userProfile: LiveData<UserProfile?> = userProfileDao.getUserProfile()
    val totalIncome: LiveData<Double?> = transactionDao.getTotalIncome()
    val totalExpense: LiveData<Double?> = transactionDao.getTotalExpense()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun insertNotification(notification: Notification) {
        notificationDao.insertNotification(notification)
    }

    suspend fun clearAllData() {
        transactionDao.deleteAllTransactions()
        notificationDao.deleteAllNotifications()
        budgetDao.deleteAllBudgets()
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.updateProfile(profile)
    }

    fun getBudgets(monthYear: String): LiveData<List<Budget>> {
        return budgetDao.getAllBudgets(monthYear)
    }

    suspend fun upsertBudget(budget: Budget) {
        budgetDao.insertOrUpdateBudget(budget)
    }

    fun getCategorySpending(category: String, monthYear: String): LiveData<Double?> {
        return transactionDao.getCategoryExpenseForMonth(category, monthYear)
    }
}
