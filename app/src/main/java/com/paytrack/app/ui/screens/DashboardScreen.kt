package com.paytrack.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paytrack.app.data.Money
import com.paytrack.app.domain.Expense
import com.paytrack.app.ui.components.ExpenseRow
import com.paytrack.app.ui.components.GlassSurface
import com.paytrack.app.ui.theme.Crimson
import com.paytrack.app.ui.theme.Mist
import com.paytrack.app.ui.theme.OffWhite
import com.paytrack.app.ui.theme.Scooter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(
    expenses: List<Expense>,
    onEdit: (Expense) -> Unit,
    onDelete: (Expense) -> Unit,
    modifier: Modifier = Modifier,
) {
    val calendar = Calendar.getInstance()
    val now = calendar.timeInMillis
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val monthStart = calendar.timeInMillis
    val monthExpenses = expenses.filter { it.occurredAt in monthStart..now }
    val monthTotal = monthExpenses.sumOf { it.amountMinor }
    val largest = monthExpenses.maxOfOrNull { it.amountMinor } ?: 0
    val activeDays = monthExpenses.map {
        SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(it.occurredAt)
    }.distinct().size.coerceAtLeast(1)
    val dailyAverage = if (monthExpenses.isEmpty()) 0 else monthTotal / activeDays

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Paytrack", style = MaterialTheme.typography.headlineMedium, color = OffWhite)
            Text(
                "Your money, clearly tracked.",
                style = MaterialTheme.typography.bodyMedium,
                color = Mist,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        item {
            GlassSurface(modifier = Modifier.fillMaxWidth(), accent = Crimson) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Crimson)
                        Text(
                            text = "THIS MONTH",
                            style = MaterialTheme.typography.labelLarge,
                            color = Mist,
                            modifier = Modifier.padding(start = 9.dp),
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = Money.format(monthTotal),
                        style = MaterialTheme.typography.displaySmall,
                        color = OffWhite,
                    )
                    Text(
                        text = "${monthExpenses.size} ${if (monthExpenses.size == 1) "expense" else "expenses"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Mist,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InsightCard(
                    label = "Daily average",
                    value = Money.format(dailyAverage),
                    icon = Icons.Outlined.CalendarMonth,
                    accent = Scooter,
                    modifier = Modifier.weight(1f),
                )
                InsightCard(
                    label = "Largest",
                    value = Money.format(largest),
                    icon = Icons.AutoMirrored.Outlined.TrendingUp,
                    accent = Crimson,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Recent expenses", style = MaterialTheme.typography.titleLarge, color = OffWhite)
                Text("Latest 5", style = MaterialTheme.typography.bodyMedium, color = Mist)
            }
        }
        if (expenses.isEmpty()) {
            item { EmptyExpenses() }
        } else {
            items(expenses.take(5), key = { it.id }) { expense ->
                GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    padding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    accent = if (expense.id % 2L == 0L) Scooter else Crimson,
                ) {
                    ExpenseRow(expense, onEdit, onDelete)
                }
            }
        }
    }
}

@Composable
private fun InsightCard(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        modifier = modifier,
        cornerRadius = 20.dp,
        padding = PaddingValues(16.dp),
        accent = accent,
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = accent)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhite,
                modifier = Modifier.padding(top = 12.dp),
                maxLines = 1,
            )
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Mist)
        }
    }
}

@Composable
private fun EmptyExpenses() {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        accent = Scooter,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = null, tint = Scooter)
            Text(
                "No expenses yet",
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                modifier = Modifier.padding(top = 10.dp),
            )
            Text("Tap + to record your first one.", style = MaterialTheme.typography.bodyMedium, color = Mist)
        }
    }
}
