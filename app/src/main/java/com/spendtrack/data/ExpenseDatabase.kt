package com.spendtrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        fun create(context: Context): ExpenseDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ExpenseDatabase::class.java,
                "spendtrack.db"
            ).build()
        }
    }
}
