package com.paytrack.app.data

import com.paytrack.app.domain.Expense
import com.paytrack.app.domain.ExpenseCategory
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExporterTest {
    @Test fun escapesExcelSensitiveTextAndWritesStableAmount() {
        val csv = CsvExporter.create(
            listOf(
                Expense(
                    amountMinor = 123456,
                    description = "Coffee, \"with friend\"",
                    category = ExpenseCategory.FOOD,
                    occurredAt = 0,
                ),
            ),
            "EGP",
        )

        assertTrue(csv.startsWith("\uFEFFDate,Time"))
        assertTrue(csv.contains("\"Coffee, \"\"with friend\"\"\""))
        assertTrue(csv.contains(",1234.56,\"EGP\""))
    }

    @Test fun neutralizesSpreadsheetFormulasInDescriptions() {
        val csv = CsvExporter.create(
            listOf(
                Expense(
                    amountMinor = 100,
                    description = "=HYPERLINK(\"bad\")",
                    category = ExpenseCategory.OTHER,
                    occurredAt = 0,
                ),
            ),
            "USD",
        )

        assertTrue(csv.contains("\"'=HYPERLINK(\"\"bad\"\")\""))
    }
}
