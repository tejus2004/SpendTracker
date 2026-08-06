package com.spendtrack.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.spendtrack.SpendTrackApplication
import com.spendtrack.data.DashboardSummary
import com.spendtrack.data.ExpenseEntity
import com.spendtrack.ui.screens.AddExpenseScreen
import com.spendtrack.ui.screens.DashboardScreen
import com.spendtrack.ui.screens.EditExpenseDialog
import com.spendtrack.ui.screens.HistoryScreen
import com.spendtrack.ui.theme.SpendTrackTheme
import com.spendtrack.widget.ExpenseWidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialTab = intent?.getStringExtra(EXTRA_OPEN_TAB)?.let(AppTab::fromValue) ?: AppTab.Dashboard
        setContent {
            SpendTrackTheme {
                val application = LocalContext.current.applicationContext as SpendTrackApplication
                SpendTrackApp(application, initialTab)
            }
        }
    }

    companion object {
        const val EXTRA_OPEN_TAB = "extra_open_tab"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendTrackApp(application: SpendTrackApplication, initialTab: AppTab = AppTab.Dashboard) {
    val repository = application.repository
    val dashboardSummaryState = remember { mutableStateOf(DashboardSummary(0, emptyList(), emptyList())) }
    val historyExpensesState = remember { mutableStateOf<List<ExpenseEntity>>(emptyList()) }

    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var deleteCandidate by remember { mutableStateOf<ExpenseEntity?>(null) }

    LaunchedEffect(repository) {
        repository.observeDashboardSummary().collect { summary ->
            dashboardSummaryState.value = summary
        }
    }

    LaunchedEffect(repository) {
        repository.observeHistory().collect { expenses ->
            historyExpensesState.value = expenses
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(selectedTab.title) })
        },
        bottomBar = {
            NavigationBar {
                for (tab in AppTab.entries) {
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.title) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { selectedTab = AppTab.Add }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            AppTab.Dashboard -> DashboardScreen(
                modifier = Modifier.padding(paddingValues),
                summary = dashboardSummaryState.value,
                onQuickAdd = { selectedTab = AppTab.Add }
            )

            AppTab.Add -> AddExpenseScreen(
                modifier = Modifier.padding(paddingValues),
                onExpenseSaved = { draft ->
                    repository.addExpense(draft)
                    ExpenseWidgetUpdater.refresh(application)
                },
                onSaved = {
                    selectedTab = AppTab.History
                }
            )

            AppTab.History -> HistoryScreen(
                modifier = Modifier.padding(paddingValues),
                expenses = historyExpensesState.value,
                onEdit = { editingExpense = it },
                onDelete = { deleteCandidate = it }
            )
        }

        deleteCandidate?.let { expense ->
            AlertDialog(
                onDismissRequest = { deleteCandidate = null },
                title = { Text("Delete expense?") },
                text = { Text("This removes the entry from your local database.") },
                confirmButton = {
                    TextButton(onClick = {
                        deleteCandidate = null
                        CoroutineScope(Dispatchers.IO).launch {
                            repository.deleteExpense(expense)
                            ExpenseWidgetUpdater.refresh(application)
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteCandidate = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        editingExpense?.let { expense ->
            EditExpenseDialog(
                expense = expense,
                onDismiss = { editingExpense = null },
                onSave = { draft ->
                    repository.updateExpense(
                        expense.copy(
                            amountCents = draft.amountCents,
                            category = draft.category.name,
                            note = draft.note
                        )
                    )
                    ExpenseWidgetUpdater.refresh(application)
                }
            )
        }
    }
}

enum class AppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val key: String) {
    Dashboard("Dashboard", Icons.Filled.Dashboard, "dashboard"),
    Add("Add", Icons.Filled.AddCircle, "add"),
    History("History", Icons.Filled.History, "history");

    companion object {
        fun fromValue(value: String?): AppTab = entries.firstOrNull { it.key == value } ?: Dashboard
    }
}
