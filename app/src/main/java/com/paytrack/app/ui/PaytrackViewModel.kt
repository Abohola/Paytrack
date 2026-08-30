package com.paytrack.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paytrack.app.data.CsvExporter
import com.paytrack.app.data.Money
import com.paytrack.app.domain.Expense
import com.paytrack.app.domain.ExpenseCategory
import com.paytrack.app.domain.ExpenseRepository
import com.paytrack.app.domain.ExpenseUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaytrackViewModel(repository: ExpenseRepository) : ViewModel() {
    private val useCases = ExpenseUseCases(repository)

    val expenses: StateFlow<List<Expense>> = useCases.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val notice = MutableStateFlow<String?>(null)

    fun save(
        existing: Expense?,
        amountInput: String,
        description: String,
        category: ExpenseCategory,
        onSuccess: () -> Unit,
    ) {
        val amountMinor = Money.parseMinor(amountInput)
        when {
            amountMinor == null -> notice.value = "Enter a valid amount greater than zero"
            description.isBlank() -> notice.value = "Add what this expense was for"
            else -> viewModelScope.launch {
                runCatching {
                    useCases.save(
                        Expense(
                            id = existing?.id ?: 0,
                            amountMinor = amountMinor,
                            description = description.trim(),
                            category = category,
                            occurredAt = existing?.occurredAt ?: System.currentTimeMillis(),
                            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                        ),
                    )
                }.onSuccess {
                    notice.value = if (existing == null) "Expense saved" else "Expense updated"
                    onSuccess()
                }.onFailure {
                    notice.value = "Could not save expense"
                }
            }
        }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch {
            runCatching { useCases.delete(expense.id) }
                .onSuccess { notice.value = "Expense deleted" }
                .onFailure { notice.value = "Could not delete expense" }
        }
    }

    fun buildExport(
        startInclusive: Long,
        endExclusive: Long,
        onReady: (String, Int) -> Unit,
    ) {
        viewModelScope.launch {
            runCatching {
                val selected = useCases.exportRange(startInclusive, endExclusive)
                CsvExporter.create(selected, Money.currencyCode()) to selected.size
            }.onSuccess { (csv, count) -> onReady(csv, count) }
                .onFailure { notice.value = "Could not prepare export" }
        }
    }

    fun clearNotice() {
        notice.value = null
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PaytrackViewModel(repository) as T
    }
}
