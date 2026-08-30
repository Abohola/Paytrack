package com.paytrack.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paytrack.app.data.Money
import com.paytrack.app.domain.Expense
import com.paytrack.app.domain.ExpenseCategory
import com.paytrack.app.ui.components.ExpenseRow
import com.paytrack.app.ui.components.GlassSurface
import com.paytrack.app.ui.theme.Crimson
import com.paytrack.app.ui.theme.Mist
import com.paytrack.app.ui.theme.OffWhite
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(
    expenses: List<Expense>,
    onEdit: (Expense) -> Unit,
    onDelete: (Expense) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<ExpenseCategory?>(null) }
    val filtered = remember(expenses, query, category) {
        expenses.filter {
            (query.isBlank() || it.description.contains(query, ignoreCase = true)) &&
                (category == null || it.category == category)
        }
    }

    Column(modifier.fillMaxSize().padding(top = 20.dp)) {
        Text(
            "History",
            style = MaterialTheme.typography.headlineMedium,
            color = OffWhite,
            modifier = Modifier.padding(horizontal = 18.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("${filtered.size} records", style = MaterialTheme.typography.bodyMedium, color = Mist)
            Text(Money.format(filtered.sumOf { it.amountMinor }), style = MaterialTheme.typography.bodyMedium, color = OffWhite)
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            placeholder = { Text("Search expenses") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Crimson,
                unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                cursorColor = Crimson,
                focusedContainerColor = Color.White.copy(alpha = 0.04f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
            ),
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(null, *ExpenseCategory.entries.toTypedArray()).forEach { item ->
                val selected = category == item
                AssistChip(
                    onClick = { category = item },
                    label = { Text(item?.label ?: "All") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) Crimson else Color.White.copy(alpha = 0.05f),
                        labelColor = OffWhite,
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = if (selected) Crimson else Color.White.copy(alpha = 0.12f),
                    ),
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (filtered.isEmpty()) {
                item {
                    GlassSurface(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (expenses.isEmpty()) "No expenses yet. Tap + to add one." else "No expenses match these filters.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Mist,
                        )
                    }
                }
            }
            itemsIndexed(filtered, key = { _, item -> item.id }) { index, expense ->
                val date = DateFormat.getDateInstance(DateFormat.FULL).format(Date(expense.occurredAt))
                val previousDate = filtered.getOrNull(index - 1)?.let {
                    DateFormat.getDateInstance(DateFormat.FULL).format(Date(it.occurredAt))
                }
                if (date != previousDate) {
                    Text(
                        date,
                        style = MaterialTheme.typography.labelLarge,
                        color = Mist,
                        modifier = Modifier.padding(top = if (index == 0) 0.dp else 8.dp, bottom = 2.dp),
                    )
                }
                GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    padding = PaddingValues(horizontal = 8.dp),
                ) {
                    ExpenseRow(expense, onEdit, onDelete)
                }
            }
        }
    }
}
