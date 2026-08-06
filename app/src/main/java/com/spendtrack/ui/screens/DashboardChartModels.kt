package com.spendtrack.ui.screens

import com.spendtrack.data.CategoryTotalRow
import com.spendtrack.domain.ExpenseCategory

data class CategoryChartItem(
    val category: ExpenseCategory,
    val totalCents: Long,
    val percentage: Float
)

fun buildCategoryChartItems(
    categoryTotals: List<CategoryTotalRow>,
    monthlyTotalCents: Long
): List<CategoryChartItem> {
    if (monthlyTotalCents <= 0L) {
        return emptyList()
    }

    return categoryTotals
        .mapNotNull { row ->
            val category = ExpenseCategory.fromStoredValue(row.category)
            val totalCents = row.totalCents
            if (totalCents <= 0L) {
                null
            } else {
                CategoryChartItem(
                    category = category,
                    totalCents = totalCents,
                    percentage = totalCents.toFloat() / monthlyTotalCents.toFloat()
                )
            }
        }
        .sortedByDescending { it.totalCents }
}
