package com.example.mymoneynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.model.Transaction
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBack: () -> Unit, transactions: List<Transaction>) {
    var startText by remember { mutableStateOf("") }
    var endText by remember { mutableStateOf("") }
    var filtered by remember { mutableStateOf(transactions) }

    Scaffold(topBar = { TopAppBar(title = { Text("Laporan") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = startText,
                onValueChange = { startText = it },
                label = { Text("Tanggal Awal (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endText,
                onValueChange = { endText = it },
                label = { Text("Tanggal Akhir (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) { Text("Kembali") }
                Button(onClick = {
                    val formatter = DateTimeFormatter.ISO_DATE
                    val start = try { LocalDate.parse(startText, formatter) } catch (e: DateTimeParseException) { null }
                    val end = try { LocalDate.parse(endText, formatter) } catch (e: DateTimeParseException) { null }
                    filtered = transactions.filter { t ->
                        val d = LocalDate.ofInstant(java.time.Instant.ofEpochMilli(t.date), ZoneId.systemDefault())
                        (start == null || !d.isBefore(start)) && (end == null || !d.isAfter(end))
                    }
                }) { Text("Terapkan") }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(filtered) { t ->
                    Text("${DateTimeFormatter.ISO_DATE.format(java.time.Instant.ofEpochMilli(t.date).atZone(ZoneId.systemDefault()).toLocalDate())} - ${t.category} : Rp ${"%,.0f".format(t.amount)}")
                }
            }
        }
    }
}
