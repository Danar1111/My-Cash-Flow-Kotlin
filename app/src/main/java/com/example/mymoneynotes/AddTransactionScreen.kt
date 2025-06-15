package com.example.mymoneynotes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onAdd: (TransactionType, String, Double) -> Unit,
    onCancel: () -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.INCOME) }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tambah Transaksi") })
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

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel) {
                    Text("Batal")
                }
                Button(onClick = {
                    onAdd(type, category, amount.toDoubleOrNull() ?: 0.0)
                }) {
                    Text("Simpan")
                }
            }
        }
    }
}
