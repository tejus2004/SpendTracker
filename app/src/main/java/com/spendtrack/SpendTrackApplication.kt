package com.spendtrack

import android.app.Application
import com.spendtrack.data.ExpenseDatabase
import com.spendtrack.data.ExpenseRepository

class SpendTrackApplication : Application() {
    val database by lazy { ExpenseDatabase.create(this) }
    val repository by lazy { ExpenseRepository(database.expenseDao()) }
}
