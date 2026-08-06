package com.spendtrack.ui.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatMoney(cents: Long): String = NumberFormat.getCurrencyInstance().format(cents / 100.0)

fun formatExpenseDate(epochMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, h:mm a")
    return Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

fun amountCentsToInput(cents: Long): String = String.format("%.2f", cents / 100.0)
