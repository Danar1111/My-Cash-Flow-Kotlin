package com.example.mymoneynotes.model

data class Transaction(
    val id: Int,
    val type: TransactionType,
    val category: String,
    val amount: Double
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
