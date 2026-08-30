package com.paytrack.app.domain

data class ExpenseUseCases(
    val observe: ObserveExpenses,
    val save: SaveExpense,
    val delete: DeleteExpense,
    val exportRange: GetExpensesForRange,
) {
    constructor(repository: ExpenseRepository) : this(
        observe = ObserveExpenses(repository),
        save = SaveExpense(repository),
        delete = DeleteExpense(repository),
        exportRange = GetExpensesForRange(repository),
    )
}

class ObserveExpenses(private val repository: ExpenseRepository) {
    operator fun invoke() = repository.observeAll()
}

class SaveExpense(private val repository: ExpenseRepository) {
    suspend operator fun invoke(expense: Expense): Long {
        require(expense.amountMinor > 0) { "Amount must be greater than zero" }
        require(expense.description.isNotBlank()) { "Description is required" }
        return if (expense.id == 0L) repository.add(expense) else {
            repository.update(expense)
            expense.id
        }
    }
}

class DeleteExpense(private val repository: ExpenseRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}

class GetExpensesForRange(private val repository: ExpenseRepository) {
    suspend operator fun invoke(startInclusive: Long, endExclusive: Long): List<Expense> {
        require(startInclusive < endExclusive) { "Start date must be before end date" }
        return repository.between(startInclusive, endExclusive)
    }
}
