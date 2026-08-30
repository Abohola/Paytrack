package com.paytrack.app.domain

data class Expense(
    val id: Long = 0,
    val amountMinor: Long,
    val description: String,
    val category: ExpenseCategory,
    val occurredAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
)

enum class ExpenseCategory(val label: String) {
    FOOD("Food"),
    TRANSPORT("Transport"),
    BILLS("Bills"),
    SHOPPING("Shopping"),
    HEALTH("Health"),
    LEISURE("Leisure"),
    OTHER("Other"),
}
