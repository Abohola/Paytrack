package com.paytrack.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.paytrack.app.data.ExpenseDatabase.Companion.COL_AMOUNT_MINOR
import com.paytrack.app.data.ExpenseDatabase.Companion.COL_CATEGORY
import com.paytrack.app.data.ExpenseDatabase.Companion.COL_CREATED_AT
import com.paytrack.app.data.ExpenseDatabase.Companion.COL_DESCRIPTION
import com.paytrack.app.data.ExpenseDatabase.Companion.COL_ID
import com.paytrack.app.data.ExpenseDatabase.Companion.COL_OCCURRED_AT
import com.paytrack.app.data.ExpenseDatabase.Companion.TABLE_EXPENSES
import com.paytrack.app.domain.Expense
import com.paytrack.app.domain.ExpenseCategory
import com.paytrack.app.domain.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class SqliteExpenseRepository(context: Context) : ExpenseRepository {
    private val database = ExpenseDatabase(context.applicationContext)
    private val expenses = MutableStateFlow(emptyList<Expense>())

    init {
        expenses.value = queryAll()
    }

    override fun observeAll(): Flow<List<Expense>> = expenses

    override suspend fun add(expense: Expense): Long = withContext(Dispatchers.IO) {
        val id = database.writableDatabase.insertOrThrow(
            TABLE_EXPENSES,
            null,
            expense.toValues(includeId = false),
        )
        refresh()
        id
    }

    override suspend fun update(expense: Expense) = withContext(Dispatchers.IO) {
        database.writableDatabase.update(
            TABLE_EXPENSES,
            expense.toValues(includeId = false),
            "$COL_ID = ?",
            arrayOf(expense.id.toString()),
        )
        refresh()
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        database.writableDatabase.delete(
            TABLE_EXPENSES,
            "$COL_ID = ?",
            arrayOf(id.toString()),
        )
        refresh()
    }

    override suspend fun between(startInclusive: Long, endExclusive: Long): List<Expense> =
        withContext(Dispatchers.IO) {
            query(
                selection = "$COL_OCCURRED_AT >= ? AND $COL_OCCURRED_AT < ?",
                selectionArgs = arrayOf(startInclusive.toString(), endExclusive.toString()),
            )
        }

    private fun refresh() {
        expenses.value = queryAll()
    }

    private fun queryAll(): List<Expense> = query(null, null)

    private fun query(selection: String?, selectionArgs: Array<String>?): List<Expense> {
        val result = mutableListOf<Expense>()
        database.readableDatabase.query(
            TABLE_EXPENSES,
            COLUMNS,
            selection,
            selectionArgs,
            null,
            null,
            "$COL_OCCURRED_AT DESC, $COL_ID DESC",
        ).use { cursor ->
            while (cursor.moveToNext()) result += cursor.toExpense()
        }
        return result
    }

    private fun Expense.toValues(includeId: Boolean): ContentValues = ContentValues().apply {
        if (includeId) put(COL_ID, id)
        put(COL_AMOUNT_MINOR, amountMinor)
        put(COL_DESCRIPTION, description.trim())
        put(COL_CATEGORY, category.name)
        put(COL_OCCURRED_AT, occurredAt)
        put(COL_CREATED_AT, createdAt)
    }

    private fun Cursor.toExpense() = Expense(
        id = getLong(getColumnIndexOrThrow(COL_ID)),
        amountMinor = getLong(getColumnIndexOrThrow(COL_AMOUNT_MINOR)),
        description = getString(getColumnIndexOrThrow(COL_DESCRIPTION)),
        category = getString(getColumnIndexOrThrow(COL_CATEGORY))
            .let { stored -> ExpenseCategory.entries.firstOrNull { it.name == stored } }
            ?: ExpenseCategory.OTHER,
        occurredAt = getLong(getColumnIndexOrThrow(COL_OCCURRED_AT)),
        createdAt = getLong(getColumnIndexOrThrow(COL_CREATED_AT)),
    )

    private companion object {
        val COLUMNS = arrayOf(
            COL_ID,
            COL_AMOUNT_MINOR,
            COL_DESCRIPTION,
            COL_CATEGORY,
            COL_OCCURRED_AT,
            COL_CREATED_AT,
        )
    }
}
