package com.spendtrack.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.ui.util.formatExpenseDate
import com.spendtrack.ui.util.formatMoney

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chartItems = remember(summary.categoryTotals, summary.monthlyTotalCents) {
        buildCategoryChartItems(summary.categoryTotals, summary.monthlyTotalCents)
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
}

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
