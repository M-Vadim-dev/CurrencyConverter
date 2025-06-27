package com.example.currencyconverter.ui.screens.exchange

import androidx.compose.runtime.Immutable
import com.example.currencyconverter.domain.entity.Account

@Immutable
data class ExchangeScreenState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "",
    val fromAmount: Double = 0.0,
    val toAmount: Double = 0.0,
    val balanceAfterExchange: Double = 0.0,
    val exchangeRate: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: ExchangeError? = null,
    val isSuccess: Boolean = false,
)
