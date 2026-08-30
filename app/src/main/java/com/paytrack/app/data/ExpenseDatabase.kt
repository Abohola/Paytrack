package com.paytrack.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class ExpenseDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_EXPENSES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_AMOUNT_MINOR INTEGER NOT NULL,
                $COL_DESCRIPTION TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_OCCURRED_AT INTEGER NOT NULL,
                $COL_CREATED_AT INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE INDEX index_expenses_occurred_at ON $TABLE_EXPENSES($COL_OCCURRED_AT)",
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    companion object {
        const val TABLE_EXPENSES = "expenses"
        const val COL_ID = "id"
        const val COL_AMOUNT_MINOR = "amount_minor"
        const val COL_DESCRIPTION = "description"
        const val COL_CATEGORY = "category"
        const val COL_OCCURRED_AT = "occurred_at"
        const val COL_CREATED_AT = "created_at"
        private const val DATABASE_NAME = "paytrack.db"
        private const val DATABASE_VERSION = 1
    }
}
