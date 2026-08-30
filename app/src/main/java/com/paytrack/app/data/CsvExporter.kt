package com.paytrack.app.data

import com.paytrack.app.domain.Expense
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {
    fun create(expenses: List<Expense>, currencyCode: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        return buildString {
            append('\uFEFF')
            appendLine("Date,Time,Description,Category,Amount,Currency")
            expenses.forEach { expense ->
                append(csv(dateFormat.format(Date(expense.occurredAt))))
                append(',')
                append(csv(timeFormat.format(Date(expense.occurredAt))))
                append(',')
                append(csv(excelSafeText(expense.description)))
                append(',')
                append(csv(expense.category.label))
                append(',')
                append(BigDecimal(expense.amountMinor).movePointLeft(2).setScale(2, RoundingMode.UNNECESSARY))
                append(',')
                appendLine(csv(currencyCode))
            }
        }
    }

    private fun csv(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    private fun excelSafeText(value: String): String =
        if (value.trimStart().firstOrNull() in setOf('=', '+', '-', '@')) "'$value" else value
}
