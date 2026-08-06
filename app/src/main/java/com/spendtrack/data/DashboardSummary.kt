package com.spendtrack.data

data class DashboardSummary(
    val monthlyTotalCents: Long,
    val categoryTotals: List<CategoryTotalRow>,
    val recentExpenses: List<ExpenseEntity>
)
