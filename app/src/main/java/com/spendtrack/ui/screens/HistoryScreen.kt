package com.spendtrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spendtrack.data.ExpenseEntity
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.ui.util.formatExpenseDate
import com.spendtrack.ui.util.formatMoney

@Composable
fun HistoryScreen(
    expenses: List<ExpenseEntity>,
    onEdit: (ExpenseEntity) -> Unit,
    onDelete: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(expenses) { expense ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(ExpenseCategory.fromStoredValue(expense.category).displayName, style = MaterialTheme.typography.titleSmall)
                        expense.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        Text(formatExpenseDate(expense.createdAtMillis), style = MaterialTheme.typography.labelMedium)
                    }
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                        Text(formatMoney(expense.amountCents), style = MaterialTheme.typography.titleMedium)
                        Row {
                            IconButton(onClick = { onEdit(expense) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit expense")
                            }
                            IconButton(onClick = { onDelete(expense) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete expense")
                            }
                        }
                    }
                }
            }
        }
    }
}
