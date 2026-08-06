package com.spendtrack.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.spendtrack.SpendTrackApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ExpenseWidgetUpdater {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun refresh(context: Context) {
        val application = context.applicationContext as SpendTrackApplication
        scope.launch {
            val summary = application.repository.currentMonthSummary()
            updateWidgets(context, summary.monthlyTotalCents)
        }
    }

    suspend fun updateNow(context: Context) {
        val application = context.applicationContext as SpendTrackApplication
        val summary = application.repository.currentMonthSummary()
        updateWidgets(context, summary.monthlyTotalCents)
    }

    private fun updateWidgets(context: Context, totalCents: Long) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, ExpenseWidgetProvider::class.java)
        val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
        ExpenseWidgetProvider.updateAppWidgets(context, appWidgetManager, widgetIds, totalCents)
    }
}
