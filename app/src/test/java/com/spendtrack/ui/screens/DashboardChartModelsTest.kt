package com.spendtrack.ui.screens

import com.spendtrack.data.CategoryTotalRow
import com.spendtrack.domain.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
