package com.spendtrack.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.spendtrack.SpendTrackApplication
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.ui.components.ExpenseEntryForm
import com.spendtrack.ui.theme.SpendTrackTheme

class QuickAddExpenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialCategory = ExpenseCategory.fromStoredValue(intent.getStringExtra(EXTRA_CATEGORY))
        setContent {
            SpendTrackTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    QuickAddExpenseContent(initialCategory = initialCategory)
                }
            }
        }
    }

    @Composable
    private fun QuickAddExpenseContent(initialCategory: ExpenseCategory) {
        val context = LocalContext.current
        val application = context.applicationContext as SpendTrackApplication

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                ExpenseEntryForm(
                    title = "Quick add",
                    subtitle = "Save a local expense without opening the full app.",
                    initialCategory = initialCategory,
                    showNoteField = false,
                    onSave = { draft ->
                        application.repository.addExpense(draft)
                        ExpenseWidgetUpdater.refresh(context)
                    },
                    onSaved = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
}
