package com.spendtrack.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendtrack.data.DashboardSummary
import com.spendtrack.data.ExpenseEntity
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.ui.util.formatExpenseDate
import com.spendtrack.ui.util.formatMoney
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    expenses: List<ExpenseEntity>,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chartItems = remember(summary.categoryTotals, summary.monthlyTotalCents) {
        buildCategoryChartItems(summary.categoryTotals, summary.monthlyTotalCents)
    }
    val today = LocalDate.now()
    val monthDateFormatter = remember { DateTimeFormatter.ofPattern("MMM d") }
    val monthYearFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }
    var calendarOpen by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var calendarMonth by remember { mutableStateOf(YearMonth.from(today)) }
    val monthExpenses = remember(expenses, calendarMonth) {
        expenses.filter { expense ->
            val date = Instant.ofEpochMilli(expense.createdAtMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            YearMonth.from(date) == calendarMonth
        }
    }
    val dailyTotals = remember(monthExpenses) {
        monthExpenses.groupBy { expense ->
            Instant.ofEpochMilli(expense.createdAtMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.mapValues { (_, entries) ->
            entries.sumOf { it.amountCents }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.animateContentSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Monthly summary", style = MaterialTheme.typography.labelLarge)
                                Text("Your smartest view of the month", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        IconButton(onClick = { calendarOpen = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Open spending calendar")
                        }
                    }

                    Text(formatMoney(summary.monthlyTotalCents), style = MaterialTheme.typography.displaySmall)
                    Text(
                        "A calm and focused overview that stays in sync with your widget and local history.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onQuickAdd,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quick add expense")
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Monthly spending", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    SimpleBarChart(chartItems)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Category mix", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    CategoryPieChart(chartItems)
                }
            }
        }

        item {
            Text("Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        items(chartItems) { item ->
            CategoryCard(item = item)
        }

        item {
            Text("Recent expenses", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        if (summary.recentExpenses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No expenses yet for this month. Your next save will appear here instantly.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(summary.recentExpenses) { expense ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.animateContentSize()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                ExpenseCategory.fromStoredValue(expense.category).displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(formatMoney(expense.amountCents), style = MaterialTheme.typography.titleSmall)
                        }
                        expense.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        Text(formatExpenseDate(expense.createdAtMillis), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }

    if (calendarOpen) {
        val monthDateEntries = (1..calendarMonth.lengthOfMonth()).map { day ->
            val date = calendarMonth.atDay(day)
            val total = dailyTotals[date] ?: 0L
            val entries = monthExpenses.filter { expense ->
                Instant.ofEpochMilli(expense.createdAtMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate() == date
            }
            MonthDayEntry(date, total, entries)
        }

        AlertDialog(
            onDismissRequest = { calendarOpen = false },
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { calendarMonth = calendarMonth.minusMonths(1) }
                        ) {
                            Text("<", style = MaterialTheme.typography.titleLarge)
                        }
                        Text(calendarMonth.format(monthYearFormatter), style = MaterialTheme.typography.titleMedium)
                        IconButton(
                            onClick = { calendarMonth = calendarMonth.plusMonths(1) },
                            enabled = calendarMonth < YearMonth.from(today)
                        ) {
                            Text(">", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Text(
                        "Tap a date to see its expense breakdown",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    monthDateEntries.forEach { dayEntry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedDay = dayEntry.date; calendarOpen = false }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(dayEntry.date.format(monthDateFormatter), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                if (dayEntry.totalCents > 0L) formatMoney(dayEntry.totalCents) else "No spend",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (dayEntry.totalCents > 0L) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { calendarOpen = false }) {
                    Text("Close")
                }
            }
        )
    }

    selectedDay?.let { selectedDate ->
        val dayEntries = monthExpenses.filter { expense ->
            Instant.ofEpochMilli(expense.createdAtMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == selectedDate
        }
        val dateTotal = dayEntries.sumOf { it.amountCents }

        AlertDialog(
            onDismissRequest = { selectedDay = null },
            title = { Text(selectedDate.format(monthDateFormatter)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Total: ${formatMoney(dateTotal)}", style = MaterialTheme.typography.titleSmall)
                    if (dayEntries.isEmpty()) {
                        Text("No expenses recorded for this date.")
                    } else {
                        dayEntries.forEach { expense ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        ExpenseCategory.fromStoredValue(expense.category).displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    expense.note?.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                                }
                                Text(formatMoney(expense.amountCents), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedDay = null }) {
                    Text("Done")
                }
            }
        )
    }
}

private data class MonthDayEntry(
    val date: LocalDate,
    val totalCents: Long,
    val entries: List<ExpenseEntity>
)

@Composable
private fun SimpleBarChart(items: List<CategoryChartItem>) {
    if (items.isEmpty()) {
        Text("Your category totals will appear here as soon as you add expenses.", style = MaterialTheme.typography.bodyMedium)
        return
    }

    val maxValue = items.maxOfOrNull { it.totalCents } ?: 1L
    Row(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (item in items) {
            val barHeight by animateFloatAsState(targetValue = if (maxValue <= 0L) 0f else item.totalCents.toFloat() / maxValue.toFloat(), label = "bar")
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(18.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(fraction = barHeight)
                            .width(18.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(categoryAccent(item.category))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.category.displayName, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun CategoryPieChart(items: List<CategoryChartItem>) {
    if (items.isEmpty()) {
        return
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val totalAngle = 360f
            var startAngle = -90f
            items.forEach { item ->
                val sweep = (item.percentage * totalAngle).coerceAtLeast(4f)
                drawArc(
                    color = categoryAccent(item.category),
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
            drawCircle(
                color = Color(0xFF111A2E),
                radius = 32f,
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            for (item in items) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(categoryAccent(item.category))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.category.displayName, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${(item.percentage * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(item: CategoryChartItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.animateContentSize()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(categoryAccent(item.category).copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon(item.category),
                        contentDescription = null,
                        tint = categoryAccent(item.category)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.category.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("${(item.percentage * 100).toInt()}% of this month", style = MaterialTheme.typography.bodySmall)
                }
                Text(formatMoney(item.totalCents), style = MaterialTheme.typography.titleSmall)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        }
    }
}

private fun categoryAccent(category: ExpenseCategory): Color = when (category) {
    ExpenseCategory.Food -> Color(0xFFFF7A59)
    ExpenseCategory.Transport -> Color(0xFF5DADE2)
    ExpenseCategory.Bills -> Color(0xFF7DCEA0)
    ExpenseCategory.Shopping -> Color(0xFFF4B400)
    ExpenseCategory.Health -> Color(0xFFB39DDB)
    ExpenseCategory.Entertainment -> Color(0xFFEC407A)
    ExpenseCategory.Other -> Color(0xFF90CAF9)
}

private fun categoryIcon(category: ExpenseCategory) = when (category) {
    ExpenseCategory.Food -> Icons.Outlined.Fastfood
    ExpenseCategory.Transport -> Icons.Outlined.DirectionsCar
    ExpenseCategory.Bills -> Icons.Outlined.ReceiptLong
    ExpenseCategory.Shopping -> Icons.Outlined.ShoppingBag
    ExpenseCategory.Health -> Icons.Outlined.MedicalServices
    ExpenseCategory.Entertainment -> Icons.Outlined.Movie
    ExpenseCategory.Other -> Icons.Outlined.Category
}
