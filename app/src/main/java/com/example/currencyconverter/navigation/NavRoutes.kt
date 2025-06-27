package com.example.currencyconverter.navigation

import android.net.Uri

sealed class NavRoutes(val route: String) {
    object Currency : NavRoutes(CURRENCY_ROUTE)

    object Exchange : NavRoutes(
        "exchange?" +
                "${FROM_CURRENCY_ARG}={${FROM_CURRENCY_ARG}}&" +
                "${TO_CURRENCY_ARG}={${TO_CURRENCY_ARG}}&" +
                "${RATE_ARG}={${RATE_ARG}}&" +
                "${AMOUNT_ARG}={${AMOUNT_ARG}}"
    ) {
        fun createRoute(
            fromCurrency: String,
            toCurrency: String,
            rate: Double,
            amount: Double,
        ) = EXCHANGE_ROUTE +
                "?${FROM_CURRENCY_ARG}=${Uri.encode(fromCurrency)}" +
                "&${TO_CURRENCY_ARG}=${Uri.encode(toCurrency)}" +
                "&${RATE_ARG}=${rate.toFloat()}" +
                "&${AMOUNT_ARG}=${amount.toFloat()}"
    }

    object Transactions : NavRoutes(TRANSACTIONS_ROUTE)

    companion object {
        const val CURRENCY_ROUTE = "currency"
        const val EXCHANGE_ROUTE = "exchange"
        const val TRANSACTIONS_ROUTE = "transactions"

        const val FROM_CURRENCY_ARG = "fromCurrency"
        const val TO_CURRENCY_ARG = "toCurrency"
        const val RATE_ARG = "rate"
        const val AMOUNT_ARG = "amount"
    }
}