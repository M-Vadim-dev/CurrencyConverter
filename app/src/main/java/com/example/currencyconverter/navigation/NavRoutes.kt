package com.example.currencyconverter.navigation

import android.net.Uri

sealed class NavRoutes(val route: String) {
    object Currency : NavRoutes("currency")
    object Exchange : NavRoutes("exchange?fromCurrency={fromCurrency}&toCurrency={toCurrency}") {
        fun createRoute(fromCurrency: String, toCurrency: String) =
            "exchange?fromCurrency=${Uri.encode(fromCurrency)}&toCurrency=${Uri.encode(toCurrency)}"
    }
    object Transactions : NavRoutes("transactions")
}