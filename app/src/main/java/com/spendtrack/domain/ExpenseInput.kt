package com.spendtrack.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class ExpenseDraft(
    val amountCents: Long,
    val category: ExpenseCategory,
    val note: String?,
    val dateMillis: Long
)

object ExpenseInputValidator {
    fun validate(amountText: String, category: ExpenseCategory, note: String, dateMillis: Long): ValidationResult {
        val normalizedAmount = amountText.trim().replace(",", ".")
        val parsedAmount = normalizedAmount.toBigDecimalOrNull()
            ?.setScale(2, RoundingMode.HALF_UP)
            ?: return ValidationResult.Error("Enter a valid amount")

        if (parsedAmount <= BigDecimal.ZERO) {
            return ValidationResult.Error("Amount must be greater than zero")
        }

        val selectedDate = java.time.Instant.ofEpochMilli(dateMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        val today = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
        if (selectedDate.isAfter(today)) {
            return ValidationResult.Error("Expense date cannot be later than today")
        }

        val normalizedDateMillis = selectedDate
            .atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val amountCents = parsedAmount.movePointRight(2).longValueExact()
        val trimmedNote = note.trim().takeIf { it.isNotBlank() }
        return ValidationResult.Success(
            ExpenseDraft(
                amountCents = amountCents,
                category = category,
                note = trimmedNote,
                dateMillis = normalizedDateMillis
            )
        )
    }
}

sealed interface ValidationResult {
    data class Success(val draft: ExpenseDraft) : ValidationResult
    data class Error(val message: String) : ValidationResult
}
