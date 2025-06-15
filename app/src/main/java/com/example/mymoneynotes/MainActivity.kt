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
import com.example.mymoneynotes.model.TransactionType
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import com.example.mymoneynotes.ui.theme.MyMoneyNotesTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesTheme {
                var showAddScreen by remember { mutableStateOf(false) }

                if (showAddScreen) {
                    AddTransactionScreen(
                        onAdd = { type, category, amount ->
                            viewModel.addTransaction(type, category, amount)
                            showAddScreen = false
                        },
                        onCancel = { showAddScreen = false }
                    )
                } else {
                    TransactionListScreen(
                        transactions = viewModel.transactions,
                        onAddClicked = { showAddScreen = true }
                    )
                }
            }
        }
    }
}
