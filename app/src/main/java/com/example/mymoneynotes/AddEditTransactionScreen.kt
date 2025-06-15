package com.example.mymoneynotes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.model.Transaction
import com.example.mymoneynotes.model.TransactionType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transaction: Transaction? = null,
    onSave: (TransactionType, String, Double, Long, Int?) -> Unit,
    onDelete: ((Transaction) -> Unit)? = null,
    onCancel: () -> Unit
) {
    var type by remember { mutableStateOf(transaction?.type ?: TransactionType.INCOME) }
    var category by remember { mutableStateOf(transaction?.category ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var dateText by remember {
        mutableStateOf(
            transaction?.date?.let {
                DateTimeFormatter.ISO_DATE.format(
                    java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                )
            } ?: ""
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (transaction == null) "Tambah Transaksi" else "Edit Transaksi") })
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                RadioButton(
                    selected = type == TransactionType.INCOME,
                    onClick = { type = TransactionType.INCOME }
                )
                Text("Pemasukan")

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = type == TransactionType.EXPENSE,
                    onClick = { type = TransactionType.EXPENSE }
                )
                Text("Pengeluaran")
            }

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Nominal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Tanggal (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel) {
                    Text("Batal")
                }
                if (transaction != null && onDelete != null) {
                    Button(onClick = { onDelete(transaction) }) {
                        Text("Hapus")
                    }
                }
                Button(onClick = {
                    val date = try {
                        LocalDate.parse(dateText, DateTimeFormatter.ISO_DATE)
                    } catch (e: DateTimeParseException) {
                        LocalDate.now()
                    }
                    onSave(
                        type,
                        category,
                        amount.toDoubleOrNull() ?: 0.0,
                        date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        transaction?.id
                    )
                }) {
                    Text("Simpan")
                }
            }
        }
    }
}
