package com.example.currencyconverter.ui.screens.currency

import androidx.compose.runtime.Immutable
import com.example.currencyconverter.domain.entity.Account
import com.example.currencyconverter.domain.entity.Rate

@Immutable
data class CurrencyScreenState(
    val selectedCurrency: String = "USD",
    val amount: Double = 1.0,
    val rates: List<Rate> = emptyList(),
    val filteredRates: List<Rate> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val balanceMap: Map<String, Double> = emptyMap(),
    val mode: CurrencyScreenMode = CurrencyScreenMode.VIEW,
    val targetCurrencyForExchange: String? = null,
    val isConfirmed: Boolean = false,
)
