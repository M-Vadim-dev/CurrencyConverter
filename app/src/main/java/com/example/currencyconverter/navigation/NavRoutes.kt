package com.example.currencyconverter.navigation

import android.net.Uri

sealed class NavRoutes(val route: String) {
    object Currency : NavRoutes("currency")
    object Exchange : NavRoutes("exchange?fromCurrency={fromCurrency}") {
        fun createRoute(fromCurrency: String) =
            "exchange?fromCurrency=${Uri.encode(fromCurrency)}"
    }

    object Transactions : NavRoutes("transactions")
}