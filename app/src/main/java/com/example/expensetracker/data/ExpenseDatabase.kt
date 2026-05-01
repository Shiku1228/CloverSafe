package com.example.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensetracker.data.dao.BudgetDao
import com.example.expensetracker.data.dao.NotificationDao
import com.example.expensetracker.data.dao.TransactionDao
import com.example.expensetracker.data.dao.UserProfileDao
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Notification
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile

@Database(
    entities = [Transaction::class, Notification::class, UserProfile::class, Budget::class],
    version = 2,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                )
                .fallbackToDestructiveMigration() // Simple way to handle schema changes during development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
