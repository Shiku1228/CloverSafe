package com.example.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Notification
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile

@Dao
interface TransactionDao {
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income'")
    fun getTotalIncome(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense'")
    fun getTotalExpense(): LiveData<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND category = :category AND strftime('%m-%Y', timestamp / 1000, 'unixepoch') = :monthYear")
    fun getCategoryExpenseForMonth(category: String, monthYear: String): LiveData<Double?>
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getAllBudgets(monthYear: String): LiveData<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget)

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()
}

@Dao
interface NotificationDao {
    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): LiveData<List<Notification>>

    @Insert
    suspend fun insertNotification(notification: Notification)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): LiveData<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfile(profile: UserProfile)
}
