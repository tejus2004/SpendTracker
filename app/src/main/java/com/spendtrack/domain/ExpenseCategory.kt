package com.spendtrack.domain

enum class ExpenseCategory(val displayName: String) {
    Food("Food"),
    Transport("Transport"),
    Bills("Bills"),
    Shopping("Shopping"),
    Health("Health"),
    Entertainment("Entertainment"),
    Other("Other");

    companion object {
        val defaultCategory = Food

        fun fromStoredValue(value: String?): ExpenseCategory {
            return entries.firstOrNull { it.name == value } ?: defaultCategory
        }
    }
}
