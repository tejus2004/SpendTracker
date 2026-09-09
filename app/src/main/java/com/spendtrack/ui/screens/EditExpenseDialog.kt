package com.spendtrack.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.spendtrack.data.ExpenseEntity
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.domain.ExpenseDraft
import com.spendtrack.ui.components.ExpenseEntryForm
import com.spendtrack.ui.util.amountCentsToInput

@Composable
fun EditExpenseDialog(
    expense: ExpenseEntity,
    onDismiss: () -> Unit,
    onSave: suspend (ExpenseDraft) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text("Edit expense") },
        text = {
            ExpenseEntryForm(
                title = "Edit expense",
                subtitle = "Update the amount, category, note, or date.",
                initialAmount = amountCentsToInput(expense.amountCents),
                initialCategory = ExpenseCategory.fromStoredValue(expense.category),
                initialNote = expense.note.orEmpty(),
                initialDateMillis = expense.createdAtMillis,
                saveButtonText = "Update expense",
                onSave = onSave,
                onSaved = onDismiss
            )
        }
    )
}
