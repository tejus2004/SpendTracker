package com.spendtrack.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index("createdAtMillis"), Index("category")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCents: Long,
    val category: String,
    val note: String?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
