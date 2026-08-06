package com.spendtrack.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.spendtrack.R
import com.spendtrack.domain.ExpenseCategory
import com.spendtrack.ui.MainActivity
import java.text.NumberFormat

class ExpenseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        ExpenseWidgetUpdater.refresh(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE_VISIBILITY) {
            toggleAmountVisibility(context)
            ExpenseWidgetUpdater.refresh(context)
        }
    }

    companion object {
        private const val PREF_NAME = "spend_track_widget_prefs"
        private const val PREF_AMOUNT_VISIBLE = "widget_amount_visible"
        private const val ACTION_TOGGLE_VISIBILITY = "com.spendtrack.action.TOGGLE_WIDGET_VISIBILITY"

        fun updateAppWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
            totalCents: Long
        ) {
            appWidgetIds.forEach { widgetId ->
                val views = RemoteViews(context.packageName, R.layout.widget_expense_quick_add)
                val isVisible = isAmountVisible(context)
                views.setTextViewText(R.id.widget_total_value, if (isVisible) formatCurrency(totalCents) else "••••")
                views.setImageViewResource(R.id.widget_toggle_visibility, if (isVisible) android.R.drawable.ic_menu_view else android.R.drawable.ic_menu_close_clear_cancel)
                views.setContentDescription(R.id.widget_toggle_visibility, if (isVisible) "Hide monthly total" else "Show monthly total")
                views.setOnClickPendingIntent(R.id.widget_open_quick_add, quickAddIntent(context))
                views.setOnClickPendingIntent(R.id.widget_open_history, historyIntent(context))
                views.setOnClickPendingIntent(R.id.widget_toggle_visibility, toggleVisibilityIntent(context, widgetId))
                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }

        private fun quickAddIntent(context: Context): PendingIntent {
            val intent = Intent(context, QuickAddExpenseActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun historyIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(MainActivity.EXTRA_OPEN_TAB, "history")
            }
            return PendingIntent.getActivity(
                context,
                1002,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun toggleVisibilityIntent(context: Context, widgetId: Int): PendingIntent {
            val intent = Intent(context, ExpenseWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_VISIBILITY
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            return PendingIntent.getBroadcast(
                context,
                widgetId + 1000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun isAmountVisible(context: Context): Boolean {
            return prefs(context).getBoolean(PREF_AMOUNT_VISIBLE, true)
        }

        private fun toggleAmountVisibility(context: Context) {
            val preferences = prefs(context)
            preferences.edit().putBoolean(PREF_AMOUNT_VISIBLE, !preferences.getBoolean(PREF_AMOUNT_VISIBLE, true)).apply()
        }

        private fun prefs(context: Context): SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        private fun formatCurrency(cents: Long): String {
            return NumberFormat.getCurrencyInstance().format(cents / 100.0)
        }
    }
}
