package com.example.currencyconverter.ui.screens.transactions

import androidx.compose.runtime.Immutable
import com.example.currencyconverter.domain.entity.Transaction

@Immutable
data class TransactionsScreenState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
