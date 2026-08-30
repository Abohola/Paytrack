package com.paytrack.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.paytrack.app.data.Money
import com.paytrack.app.domain.Expense
import com.paytrack.app.domain.ExpenseCategory
import com.paytrack.app.ui.theme.Crimson
import com.paytrack.app.ui.theme.DeepNoir
import com.paytrack.app.ui.theme.Mist
import com.paytrack.app.ui.theme.OffWhite
import com.paytrack.app.ui.theme.Scooter
import java.math.BigDecimal
import java.text.DateFormat
import java.util.Date

fun categoryIcon(category: ExpenseCategory): ImageVector = when (category) {
    ExpenseCategory.FOOD -> Icons.Outlined.Restaurant
    ExpenseCategory.TRANSPORT -> Icons.Outlined.DirectionsCar
    ExpenseCategory.BILLS -> Icons.AutoMirrored.Outlined.ReceiptLong
    ExpenseCategory.SHOPPING -> Icons.Outlined.ShoppingBag
    ExpenseCategory.HEALTH -> Icons.Outlined.FavoriteBorder
    ExpenseCategory.LEISURE -> Icons.Outlined.SportsEsports
    ExpenseCategory.OTHER -> Icons.Outlined.Payments
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseRow(
    expense: Expense,
    onEdit: (Expense) -> Unit,
    onDelete: (Expense) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(onClick = { onEdit(expense) }, onLongClick = { onDelete(expense) })
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = categoryIcon(expense.category),
                contentDescription = expense.category.label,
                tint = if (expense.category == ExpenseCategory.OTHER) Scooter else Crimson,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.description,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                maxLines = 1,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "${expense.category.label}  •  ${DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(expense.occurredAt))}",
                style = MaterialTheme.typography.bodyMedium,
                color = Mist,
                maxLines = 1,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = Money.format(expense.amountMinor),
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                fontWeight = FontWeight.Bold,
            )
            Row {
                IconButton(onClick = { onEdit(expense) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit expense", tint = Mist, modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = { onDelete(expense) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete expense", tint = Mist, modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseEditor(
    existing: Expense?,
    onDismiss: () -> Unit,
    onSave: (String, String, ExpenseCategory) -> Unit,
) {
    var amount by remember(existing) {
        mutableStateOf(
            existing?.let {
                BigDecimal(it.amountMinor).movePointLeft(2).stripTrailingZeros().toPlainString()
            } ?: "",
        )
    }
    var description by remember(existing) { mutableStateOf(existing?.description.orEmpty()) }
    var category by remember(existing) { mutableStateOf(existing?.category ?: ExpenseCategory.OTHER) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepNoir,
        contentColor = OffWhite,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    ) {
        Column(Modifier.padding(horizontal = 22.dp, vertical = 24.dp)) {
            Text(
                text = if (existing == null) "Add expense" else "Edit expense",
                style = MaterialTheme.typography.headlineMedium,
                color = OffWhite,
            )
            Text(
                text = if (existing == null) "Saved with the current date and time" else "The original date and time will be kept",
                style = MaterialTheme.typography.bodyMedium,
                color = Mist,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                prefix = { Text("${Money.currencyCode()} ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 80) description = it },
                label = { Text("What was it for?") },
                supportingText = { Text("${description.length}/80") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors(),
            )
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge,
                color = Mist,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpenseCategory.entries.forEach { item ->
                    AssistChip(
                        onClick = { category = item },
                        label = { Text(item.label) },
                        leadingIcon = {
                            Icon(categoryIcon(item), contentDescription = null, modifier = Modifier.size(17.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (category == item) Crimson else Color.White.copy(alpha = 0.06f),
                            labelColor = OffWhite,
                            leadingIconContentColor = if (category == item) Color.White else Mist,
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = if (category == item) Crimson else Color.White.copy(alpha = 0.12f),
                        ),
                    )
                }
            }
            Button(
                onClick = { onSave(amount, description, category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Crimson, contentColor = Color.White),
            ) {
                Text(if (existing == null) "Save expense" else "Save changes")
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = OffWhite,
    unfocusedTextColor = OffWhite,
    focusedBorderColor = Crimson,
    unfocusedBorderColor = Color.White.copy(alpha = 0.16f),
    focusedLabelColor = Crimson,
    unfocusedLabelColor = Mist,
    cursorColor = Crimson,
    focusedContainerColor = Color.White.copy(alpha = 0.045f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
)
