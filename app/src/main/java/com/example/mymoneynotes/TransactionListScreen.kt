package com.example.mymoneynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.model.Transaction
import com.example.mymoneynotes.model.TransactionType
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    transactions: List<Transaction>,
    onAddClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MyMoney Notes") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            TotalBalance(transactions)
            TransactionChart(transactions)

            LazyColumn {
                items(transactions) { transaction ->
                    val isIncome = transaction.type == TransactionType.INCOME
                    val labelColor = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336) // Hijau / Merah

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {

                            // Label di pojok kanan atas
                            Text(
                                text = if (isIncome) "Pemasukan" else "Pengeluaran",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(labelColor, shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            Column(
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                // Kategori di kiri atas
                                Text(text = transaction.category)

                                // Jumlah di bawah kategori
                                Text(text = "Rp ${"%,.0f".format(transaction.amount)}")
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun TransactionChart(transactions: List<Transaction>) {
    val incomeTotal = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expenseTotal = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val total = incomeTotal + expenseTotal

    val incomePercent = if (total > 0) incomeTotal / total else 0.0
    val expensePercent = if (total > 0) expenseTotal / total else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Grafik Pemasukan vs Pengeluaran", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)) {

            val incomeWidth = size.width * incomePercent.toFloat()
            val expenseWidth = size.width * expensePercent.toFloat()

            drawRect(
                color = Color(0xFF4CAF50), // Hijau untuk pemasukan
                size = Size(incomeWidth, size.height)
            )
            drawRect(
                color = Color(0xFFF44336), // Merah untuk pengeluaran
                topLeft = Offset(incomeWidth, 0f),
                size = Size(expenseWidth, size.height)
            )
        }

    }
}

@Composable
fun TotalBalance(transactions: List<Transaction>) {
    val incomeTotal = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expenseTotal = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val balance = incomeTotal - expenseTotal

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Total Balance", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Rp ${"%,.0f".format(balance)}",
            style = MaterialTheme.typography.headlineMedium,
            color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}


