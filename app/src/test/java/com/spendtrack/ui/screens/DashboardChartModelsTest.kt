package com.spendtrack.ui.screens

import com.spendtrack.data.CategoryTotalRow
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.domain.ExpenseInputValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DashboardChartModelsTest {
    @Test
    fun buildCategoryChartItems_returnsEmptyWhenMonthlyTotalIsZero() {
        val items = buildCategoryChartItems(emptyList(), 0L)
        assertTrue(items.isEmpty())
    }

    @Test
    fun buildCategoryChartItems_sortsByLargestTotalAndKeepsPercentagesRoundedByShare() {
        val totals = listOf(
            CategoryTotalRow("Food", 3000L),
            CategoryTotalRow("Transport", 1000L),
            CategoryTotalRow("Bills", 2000L)
        )

        val items = buildCategoryChartItems(totals, 6000L)

        assertEquals(3, items.size)
        assertEquals(ExpenseCategory.Food, items.first().category)
        assertEquals(0.5f, items.first().percentage, 0.0001f)
        assertEquals(0.33333334f, items[1].percentage, 0.0001f)
    }

    @Test
    fun expenseInputValidator_rejectsFutureDates() {
        val futureDate = LocalDate.now().plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val result = ExpenseInputValidator.validate("25.50", ExpenseCategory.Food, "Dinner", futureDate)

        assertTrue(result.toString().contains("cannot be later than today"))
    }

    @Test
    fun expenseInputValidator_keepsSelectedDayForPastExpense() {
        val pastDate = LocalDate.now().minusDays(3)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val result = ExpenseInputValidator.validate("25.50", ExpenseCategory.Food, "Dinner", pastDate)

        assertTrue(result is com.spendtrack.domain.ValidationResult.Success)
        val draft = (result as com.spendtrack.domain.ValidationResult.Success).draft
        assertEquals(pastDate, draft.dateMillis)
    }
}
