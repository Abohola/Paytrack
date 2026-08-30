package com.paytrack.app.domain

import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeAll(): Flow<List<Expense>>
    suspend fun add(expense: Expense): Long
    suspend fun update(expense: Expense)
    suspend fun delete(id: Long)
    suspend fun between(startInclusive: Long, endExclusive: Long): List<Expense>
}
