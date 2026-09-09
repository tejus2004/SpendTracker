package com.spendtrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.domain.ExpenseDraft
import com.spendtrack.domain.ExpenseInputValidator
import com.spendtrack.domain.ValidationResult
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryForm(
    title: String,
    subtitle: String,
    initialAmount: String = "",
    initialCategory: ExpenseCategory = ExpenseCategory.defaultCategory,
    initialNote: String = "",
    initialDateMillis: Long = System.currentTimeMillis(),
    showNoteField: Boolean = true,
    saveButtonText: String = "Save expense",
    onSave: suspend (ExpenseDraft) -> Unit,
    onSaved: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var amountText by rememberSaveable { mutableStateOf(initialAmount) }
    var category by rememberSaveable { mutableStateOf(initialCategory) }
    var note by rememberSaveable { mutableStateOf(initialNote) }
    var selectedDateMillis by rememberSaveable { mutableStateOf(normalizeToStartOfDay(initialDateMillis)) }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }

    LaunchedEffect(initialAmount, initialCategory, initialNote, initialDateMillis) {
        amountText = initialAmount
        category = initialCategory
        note = initialNote
        selectedDateMillis = normalizeToStartOfDay(initialDateMillis)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorText = null
                },
                label = { Text("Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            CategorySelector(
                category = category,
                onCategorySelected = {
                    category = it
                    errorText = null
                }
            )

            OutlinedTextField(
                value = Instant.ofEpochMilli(selectedDateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(dateFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (showNoteField) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            errorText?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    when (val validation = ExpenseInputValidator.validate(amountText, category, note, selectedDateMillis)) {
                        is ValidationResult.Error -> errorText = validation.message
                        is ValidationResult.Success -> {
                            scope.launch {
                                isSaving = true
                                errorText = null
                                try {
                                    onSave(validation.draft)
                                    onSaved()
                                } catch (exception: Exception) {
                                    errorText = exception.message ?: "Unable to save expense"
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                },
                enabled = !isSaving,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Saving..." else saveButtonText)
            }
        }
    }

    if (showDatePicker) {
        val today = System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            yearRange = IntRange(LocalDate.now().year - 5, LocalDate.now().year + 5),
            selectableDates = object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean = year in IntRange(2000, LocalDate.now().year + 5)
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= today
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val chosenDateMillis = datePickerState.selectedDateMillis ?: selectedDateMillis
                        selectedDateMillis = normalizeToStartOfDay(chosenDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun normalizeToStartOfDay(epochMillis: Long): Long = Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
    .atStartOfDay(ZoneId.systemDefault())
    .toInstant()
    .toEpochMilli()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    category: ExpenseCategory,
    onCategorySelected: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = category.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (item in ExpenseCategory.entries) {
                DropdownMenuItem(
                    text = { Text(item.displayName) },
                    onClick = {
                        onCategorySelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
