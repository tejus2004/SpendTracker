package com.spendtrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY createdAtMillis DESC")
    fun observeAllExpenses(): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT COALESCE(SUM(amountCents), 0) FROM expenses
        WHERE createdAtMillis >= :startInclusive AND createdAtMillis < :endExclusive
        """
    )
    fun observeTotalForRange(startInclusive: Long, endExclusive: Long): Flow<Long>

    @Query(
        """
        SELECT category, COALESCE(SUM(amountCents), 0) AS totalCents
        FROM expenses
        WHERE createdAtMillis >= :startInclusive AND createdAtMillis < :endExclusive
        GROUP BY category
        ORDER BY totalCents DESC
        """
    )
    fun observeCategoryTotals(startInclusive: Long, endExclusive: Long): Flow<List<CategoryTotalRow>>

    @Query(
        """
        SELECT * FROM expenses
        WHERE createdAtMillis >= :startInclusive AND createdAtMillis < :endExclusive
        ORDER BY createdAtMillis DESC
        LIMIT :limit
        """
    )
    fun observeRecentExpenses(startInclusive: Long, endExclusive: Long, limit: Int = 5): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity?
}
