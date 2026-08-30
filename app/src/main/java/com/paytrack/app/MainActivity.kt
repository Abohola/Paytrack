package com.paytrack.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paytrack.app.domain.Expense
import com.paytrack.app.ui.PaytrackViewModel
import com.paytrack.app.ui.components.ExpenseEditor
import com.paytrack.app.ui.screens.DashboardScreen
import com.paytrack.app.ui.screens.ExportScreen
import com.paytrack.app.ui.screens.HistoryScreen
import com.paytrack.app.ui.theme.Burgundy
import com.paytrack.app.ui.theme.Crimson
import com.paytrack.app.ui.theme.DeepNoir
import com.paytrack.app.ui.theme.Mist
import com.paytrack.app.ui.theme.OffWhite
import com.paytrack.app.ui.theme.PaytrackTheme
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as PaytrackApplication).expenseRepository
        setContent {
            PaytrackTheme {
                val viewModel: PaytrackViewModel = viewModel(factory = PaytrackViewModel.Factory(repository))
                PaytrackApp(viewModel)
            }
        }
    }
}

private enum class AppTab(val label: String) {
    HOME("Home"), HISTORY("History"), EXPORT("Export")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaytrackApp(viewModel: PaytrackViewModel) {
    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val notice by viewModel.notice.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }
    var editorExpense by remember { mutableStateOf<Expense?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var deleteCandidate by remember { mutableStateOf<Expense?>(null) }
    var pendingCsv by remember { mutableStateOf<String?>(null) }
    var pendingCount by remember { mutableIntStateOf(0) }

    val createDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri ->
        if (uri != null && pendingCsv != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(pendingCsv!!.toByteArray(StandardCharsets.UTF_8))
                } ?: error("Unable to open destination")
            }.onSuccess {
                Toast.makeText(context, "Exported $pendingCount rows", Toast.LENGTH_LONG).show()
            }.onFailure {
                Toast.makeText(context, "Could not save CSV", Toast.LENGTH_LONG).show()
            }
        }
        pendingCsv = null
    }

    LaunchedEffect(notice) {
        notice?.let {
            snackbar.showSnackbar(it)
            viewModel.clearNotice()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Burgundy.copy(alpha = 0.34f), DeepNoir, Color(0xFF05060A)),
                    radius = 1200f,
                ),
            ),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbar) },
            floatingActionButton = {
                if (selectedTab != AppTab.EXPORT) {
                    FloatingActionButton(
                        onClick = { editorExpense = null; showEditor = true },
                        containerColor = Crimson,
                        contentColor = Color.White,
                    ) { Icon(Icons.Filled.Add, contentDescription = "Add expense") }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xE6111218), contentColor = OffWhite) {
                    AppTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    when (tab) {
                                        AppTab.HOME -> Icons.Outlined.Home
                                        AppTab.HISTORY -> Icons.AutoMirrored.Outlined.ReceiptLong
                                        AppTab.EXPORT -> Icons.Outlined.FileDownload
                                    },
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = OffWhite,
                                indicatorColor = Crimson,
                                unselectedIconColor = Mist,
                                unselectedTextColor = Mist,
                            ),
                        )
                    }
                }
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                label = "tab transition",
                modifier = Modifier.padding(innerPadding),
            ) { tab ->
                val edit: (Expense) -> Unit = { expense -> editorExpense = expense; showEditor = true }
                val delete: (Expense) -> Unit = { deleteCandidate = it }
                when (tab) {
                    AppTab.HOME -> DashboardScreen(expenses, edit, delete)
                    AppTab.HISTORY -> HistoryScreen(expenses, edit, delete)
                    AppTab.EXPORT -> ExportScreen(expenses = expenses, onExport = { start, end, fileName ->
                        viewModel.buildExport(start, end) { csv, count ->
                            pendingCsv = csv
                            pendingCount = count
                            createDocument.launch(fileName)
                        }
                    })
                }
            }
        }
    }

    if (showEditor) {
        ExpenseEditor(
            existing = editorExpense,
            onDismiss = { showEditor = false },
            onSave = { amount, description, category ->
                viewModel.save(editorExpense, amount, description, category) {
                    showEditor = false
                    editorExpense = null
                }
            },
        )
    }

    deleteCandidate?.let { expense ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            icon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null) },
            title = { Text("Delete expense?") },
            text = { Text("${expense.description} • ${com.paytrack.app.data.Money.format(expense.amountMinor)}") },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(expense); deleteCandidate = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { deleteCandidate = null }) { Text("Cancel") } },
        )
    }
}
