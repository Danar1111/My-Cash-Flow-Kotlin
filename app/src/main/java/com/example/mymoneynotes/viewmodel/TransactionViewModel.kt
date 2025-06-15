package com.example.mymoneynotes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mymoneynotes.model.Transaction
import com.example.mymoneynotes.model.TransactionType

class TransactionViewModel : ViewModel() {
    private var nextId = 0
    val transactions = mutableStateListOf<Transaction>()

    fun addTransaction(type: TransactionType, category: String, amount: Double) {
        transactions.add(Transaction(nextId++, type, category, amount))
    }
}