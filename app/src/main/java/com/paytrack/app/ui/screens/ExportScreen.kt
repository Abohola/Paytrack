package com.paytrack.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paytrack.app.data.Money
import com.paytrack.app.domain.Expense
import com.paytrack.app.ui.components.GlassSurface
import com.paytrack.app.ui.theme.Crimson
import com.paytrack.app.ui.theme.Mist
import com.paytrack.app.ui.theme.OffWhite
import com.paytrack.app.ui.theme.Scooter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    expenses: List<Expense>,
    onExport: (startInclusive: Long, endExclusive: Long, fileName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { startOfDay(System.currentTimeMillis()) }
    var start by remember { mutableLongStateOf(startOfMonth(today)) }
    var end by remember { mutableLongStateOf(today) }
    var selectingStart by remember { mutableStateOf<Boolean?>(null) }
    val endExclusive = addDays(end, 1)
    val selected = remember(expenses, start, endExclusive) {
        expenses.filter { it.occurredAt in start until endExclusive }
    }
    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 110.dp),
    ) {
        Text("Export", style = MaterialTheme.typography.headlineMedium, color = OffWhite)
        Text(
            "Choose any date range, then save a file ready for Excel.",
            style = MaterialTheme.typography.bodyMedium,
            color = Mist,
            modifier = Modifier.padding(top = 3.dp, bottom = 18.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { start = startOfMonth(today); end = today },
                modifier = Modifier.weight(1f),
            ) { Text("This month") }
            OutlinedButton(
                onClick = { start = addDays(today, -29); end = today },
                modifier = Modifier.weight(1f),
            ) { Text("Last 30 days") }
        }
        if (expenses.isNotEmpty()) {
            OutlinedButton(
                onClick = { start = startOfDay(expenses.minOf { it.occurredAt }); end = today },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("All records") }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DateCard(
                label = "FROM",
                value = dateFormat.format(Date(start)),
                onClick = { selectingStart = true },
                modifier = Modifier.weight(1f),
            )
            DateCard(
                label = "TO",
                value = dateFormat.format(Date(end)),
                onClick = { selectingStart = false },
                modifier = Modifier.weight(1f),
            )
        }

        GlassSurface(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            accent = Scooter,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.GridOn, contentDescription = null, tint = Scooter)
                    Text(
                        "EXPORT PREVIEW",
                        style = MaterialTheme.typography.labelLarge,
                        color = Mist,
                        modifier = Modifier.padding(start = 9.dp),
                    )
                }
                Text(
                    Money.format(selected.sumOf { it.amountMinor }),
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    modifier = Modifier.padding(top = 18.dp),
                )
                Text(
                    "${selected.size} ${if (selected.size == 1) "row" else "rows"} • Date, time, description, category, amount, currency",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Mist,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        Button(
            onClick = {
                val stamp = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
                onExport(start, endExclusive, "paytrack_${stamp.format(Date(start))}_${stamp.format(Date(end))}.csv")
            },
            enabled = selected.isNotEmpty() && start <= end,
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp).height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Crimson,
                contentColor = Color.White,
                disabledContainerColor = Color.White.copy(alpha = 0.08f),
                disabledContentColor = Mist,
            ),
        ) {
            Icon(Icons.Outlined.FileDownload, contentDescription = null)
            Text("Export CSV for Excel", modifier = Modifier.padding(start = 9.dp))
        }
        Text(
            "The file stays on your device and is saved only where you choose.",
            style = MaterialTheme.typography.bodyMedium,
            color = Mist,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }

    selectingStart?.let { isStart ->
        val initial = if (isStart) start else end
        val pickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = localDayToUtcPicker(initial),
        )
        DatePickerDialog(
            onDismissRequest = { selectingStart = null },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { picked ->
                        val local = utcPickerToLocalDay(picked)
                        if (isStart) {
                            start = local
                            if (start > end) end = start
                        } else {
                            end = local
                            if (end < start) start = end
                        }
                    }
                    selectingStart = null
                }) { Text("Select") }
            },
            dismissButton = { TextButton(onClick = { selectingStart = null }) { Text("Cancel") } },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun DateCard(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassSurface(
        modifier = modifier.clickable(onClick = onClick),
        cornerRadius = 20.dp,
        padding = PaddingValues(16.dp),
    ) {
        Column {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Crimson)
            Text(label, style = MaterialTheme.typography.labelLarge, color = Mist, modifier = Modifier.padding(top = 12.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = OffWhite, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private fun startOfDay(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun startOfMonth(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.DAY_OF_MONTH, 1)
}.timeInMillis

private fun addDays(time: Long, days: Int): Long = Calendar.getInstance().apply {
    timeInMillis = time
    add(Calendar.DAY_OF_YEAR, days)
}.timeInMillis

private fun localDayToUtcPicker(time: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = time }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun utcPickerToLocalDay(time: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = time }
    return Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
