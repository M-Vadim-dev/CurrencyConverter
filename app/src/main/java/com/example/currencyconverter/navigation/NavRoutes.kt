package com.example.currencyconverter.navigation

import android.net.Uri

sealed class NavRoutes(val route: String) {
    object Currency : NavRoutes("currency")
    object Exchange : NavRoutes(
        "exchange?fromCurrency={fromCurrency}&toCurrency={toCurrency}&rate={rate}&amount={amount}"
    ) {
        fun createRoute(
            fromCurrency: String,
            toCurrency: String,
            rate: Double,
            amount: Double,
        ) = "exchange" +
                "?fromCurrency=${Uri.encode(fromCurrency)}" +
                "&toCurrency=${Uri.encode(toCurrency)}" +
                "&rate=${rate.toFloat()}" +
                "&amount=${amount.toFloat()}"
    }

    object Transactions : NavRoutes("transactions")
}