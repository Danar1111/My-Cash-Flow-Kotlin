package com.example.mymoneynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.model.Transaction
import com.example.mymoneynotes.model.TransactionType
import com.example.mymoneynotes.util.ExportUtils
import android.widget.Toast
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBack: () -> Unit, transactions: List<Transaction>) {
    var startMillis by remember { mutableStateOf<Long?>(null) }
    var endMillis by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var filtered by remember { mutableStateOf(transactions) }

    val context = LocalContext.current

    val startText = remember(startMillis) {
        startMillis?.let {
            DateTimeFormatter.ISO_DATE.format(
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            )
        } ?: ""
    }
    val endText = remember(endMillis) {
        endMillis?.let {
            DateTimeFormatter.ISO_DATE.format(
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            )
        } ?: ""
    }

    val startState = rememberDatePickerState(initialSelectedDateMillis = startMillis)
    val endState = rememberDatePickerState(initialSelectedDateMillis = endMillis)

    Scaffold(topBar = { TopAppBar(title = { Text("Laporan") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (showStartPicker) {
                DatePickerDialog(
                    onDismissRequest = { showStartPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            startMillis = startState.selectedDateMillis
                            showStartPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartPicker = false }) { Text("Batal") }
                    }
                ) { DatePicker(state = startState) }
            }

            if (showEndPicker) {
                DatePickerDialog(
                    onDismissRequest = { showEndPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            endMillis = endState.selectedDateMillis
                            showEndPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndPicker = false }) { Text("Batal") }
                    }
                ) { DatePicker(state = endState) }
            }

            OutlinedTextField(
                value = startText,
                onValueChange = {},
                label = { Text("Tanggal Awal") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )
            OutlinedTextField(
                value = endText,
                onValueChange = {},
                label = { Text("Tanggal Akhir") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) { Text("Kembali") }
                Button(onClick = {
                    filtered = transactions.filter { t ->
                        (startMillis == null || t.date >= startMillis!!) &&
                                (endMillis == null || t.date <= endMillis!!)
                    }
                }) { Text("Terapkan") }
                IconButton(onClick = {
                    val file = ExportUtils.export(context, filtered)
                    if (file != null) {
                        Toast.makeText(context, "PDF disimpan di ${file.absolutePath}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF")
                }
            }

            Spacer(Modifier.height(16.dp))

            BalanceChart(filtered)

            Spacer(Modifier.height(16.dp))

            val incomeTotal = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expenseTotal = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val balance = incomeTotal - expenseTotal

            Text("Total Pemasukan: Rp ${"%,.0f".format(incomeTotal)}")
            Text("Total Pengeluaran: Rp ${"%,.0f".format(expenseTotal)}")
            Text("Total Balance: Rp ${"%,.0f".format(balance)}")

            LazyColumn {
                items(filtered) { t ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    DateTimeFormatter.ISO_DATE.format(
                                        Instant.ofEpochMilli(t.date).atZone(ZoneId.systemDefault()).toLocalDate()
                                    ),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(t.category)
                            }
                            Text(
                                "Rp ${"%,.0f".format(t.amount)}",
                                color = if (t.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceChart(transactions: List<Transaction>) {
    if (transactions.isEmpty()) return

    val sorted = transactions.sortedBy { it.date }
    var balance = 0.0
    val points = sorted.map {
        balance += if (it.type == TransactionType.INCOME) it.amount else -it.amount
        balance
    }
    val max = points.maxOrNull() ?: 0.0
    val min = points.minOrNull() ?: 0.0
    val range = (max - min).takeIf { it > 0 } ?: 1.0

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .padding(vertical = 8.dp)) {
        val stepX = size.width / (points.lastIndex.coerceAtLeast(1).toFloat())
        val textPaint = android.graphics.Paint().apply { textSize = 24f }
        var previous = Offset(0f, size.height * (1f - ((points.first() - min) / range).toFloat()))
        points.forEachIndexed { index, value ->
            val x = stepX * index
            val y = size.height * (1f - ((value - min) / range).toFloat())
            if (index > 0) {
                drawLine(Color.Blue, previous, Offset(x, y), strokeWidth = 4f)
            }
            drawContext.canvas.nativeCanvas.drawText(
                "${DateTimeFormatter.ISO_DATE.format(Instant.ofEpochMilli(sorted[index].date).atZone(ZoneId.systemDefault()).toLocalDate())}",
                x,
                size.height + 20,
                textPaint
            )
            drawContext.canvas.nativeCanvas.drawText(
                "${"%,.0f".format(value)}",
                x,
                y - 4,
                textPaint
            )
            previous = Offset(x, y)
        }
    }
}