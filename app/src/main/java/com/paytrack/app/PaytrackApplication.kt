package com.paytrack.app

import android.app.Application
import com.paytrack.app.data.SqliteExpenseRepository
import com.paytrack.app.domain.ExpenseRepository

class PaytrackApplication : Application() {
    val expenseRepository: ExpenseRepository by lazy {
        SqliteExpenseRepository(applicationContext)
    }
}
