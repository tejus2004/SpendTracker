package com.spendtrack.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.domain.ExpenseDraft
import com.spendtrack.ui.components.ExpenseEntryForm

@Composable
fun AddExpenseScreen(
    onExpenseSaved: suspend (ExpenseDraft) -> Unit,
    onSaved: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ExpenseEntryForm(
        title = "Add expense",
        subtitle = "Capture a cost and keep the monthly total current.",
        initialCategory = ExpenseCategory.defaultCategory,
        showNoteField = true,
        saveButtonText = "Save expense",
        onSave = onExpenseSaved,
        onSaved = onSaved,
        modifier = modifier
    )
}
