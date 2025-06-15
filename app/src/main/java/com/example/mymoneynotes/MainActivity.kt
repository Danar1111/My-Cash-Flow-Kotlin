package com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.model.Transaction
import com.example.mymoneynotes.model.TransactionType
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme
import com.example.mymoneynotes.AddEditTransactionScreen
import com.example.mymoneynotes.TransactionListScreen
import com.example.mymoneynotes.ReportScreen
import com.example.mymoneynotes.util.ExportUtils
import java.lang.System
import java.lang.System

class MainActivity : ComponentActivity() {

    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesTheme {
                var current by remember { mutableStateOf<Transaction?>(null) }
                var showForm by remember { mutableStateOf(false) }
                var showReport by remember { mutableStateOf(false) }

                when {
                    showForm -> {
                        AddEditTransactionScreen(
                            transaction = current,
                            onSave = { type, category, amount, date, id ->
                                if (id == null) {
                                    viewModel.addTransaction(type, category, amount, date)
                                } else {
                                    viewModel.updateTransaction(Transaction(id, type, category, amount, date))
                                }
                                showForm = false
                            },
                            onDelete = {
                                if (it.id != 0) viewModel.deleteTransaction(it)
                                showForm = false
                            },
                            onCancel = { showForm = false }
                        )
                    }
                    showReport -> {
                        ReportScreen(onBack = { showReport = false }, transactions = viewModel.transactions)
                    }
                    else -> {
                        TransactionListScreen(
                            transactions = viewModel.transactions,
                            onAddClicked = {
                                current = null
                                showForm = true
                            },
                            onItemClick = {
                                current = it
                                showForm = true
                            },
                            onExportPdf = { ExportUtils.export(this, viewModel.transactions) },
                            onReport = { showReport = true }
                        )
                    }
                }
            }
        }
    }
}
