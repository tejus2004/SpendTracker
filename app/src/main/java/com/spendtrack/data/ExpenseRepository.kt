package com.spendtrack.data

import com.spendtrack.domain.ExpenseDraft
import com.spendtrack.domain.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.YearMonth

class ExpenseRepository(
    private val dao: ExpenseDao,
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun observeHistory(): Flow<List<ExpenseEntity>> = dao.observeAllExpenses()

    fun observeDashboardSummary(): Flow<DashboardSummary> {
        val range = currentMonthRange(clock)
        return combine(
            dao.observeTotalForRange(range.startInclusive, range.endExclusive),
            dao.observeCategoryTotals(range.startInclusive, range.endExclusive),
            dao.observeRecentExpenses(range.startInclusive, range.endExclusive, limit = 5)
        ) { totalCents, categoryRows, recentExpenses ->
            DashboardSummary(
                monthlyTotalCents = totalCents,
                categoryTotals = categoryRows,
                recentExpenses = recentExpenses
            )
        }
    }

    suspend fun currentMonthSummary(): DashboardSummary {
        val range = currentMonthRange(clock)
        val total = dao.observeTotalForRange(range.startInclusive, range.endExclusive).first()
        val categories = dao.observeCategoryTotals(range.startInclusive, range.endExclusive).first()
        val recent = dao.observeRecentExpenses(range.startInclusive, range.endExclusive, limit = 5).first()
        return DashboardSummary(total, categories, recent)
    }

    suspend fun addExpense(draft: ExpenseDraft): Long {
        val now = clock.millis()
        return dao.insert(
            ExpenseEntity(
                amountCents = draft.amountCents,
                category = draft.category.name,
                note = draft.note,
                createdAtMillis = now,
                updatedAtMillis = now
            )
        )
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        dao.update(expense.copy(updatedAtMillis = clock.millis()))
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        dao.delete(expense)
    }

    suspend fun findExpense(expenseId: Long): ExpenseEntity? = dao.getExpenseById(expenseId)
}

private data class MonthRange(
    val startInclusive: Long,
    val endExclusive: Long
)

private fun currentMonthRange(clock: Clock): MonthRange {
    val zone = clock.zone
    val yearMonth = YearMonth.now(clock)
    val startInclusive = yearMonth.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
    val endExclusive = yearMonth.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
    return MonthRange(startInclusive, endExclusive)
}
