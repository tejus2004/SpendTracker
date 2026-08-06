package com.spendtrack.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class ExpenseDraft(
    val amountCents: Long,
    val category: ExpenseCategory,
    val note: String?
)

object ExpenseInputValidator {
    fun validate(amountText: String, category: ExpenseCategory, note: String): ValidationResult {
        val normalizedAmount = amountText.trim().replace(",", ".")
        val parsedAmount = normalizedAmount.toBigDecimalOrNull()
            ?.setScale(2, RoundingMode.HALF_UP)
            ?: return ValidationResult.Error("Enter a valid amount")

        if (parsedAmount <= BigDecimal.ZERO) {
            return ValidationResult.Error("Amount must be greater than zero")
        }

        val amountCents = parsedAmount.movePointRight(2).longValueExact()
        val trimmedNote = note.trim().takeIf { it.isNotBlank() }
        return ValidationResult.Success(
            ExpenseDraft(
                amountCents = amountCents,
                category = category,
                note = trimmedNote
            )
        )
    }
}

sealed interface ValidationResult {
    data class Success(val draft: ExpenseDraft) : ValidationResult
    data class Error(val message: String) : ValidationResult
}
