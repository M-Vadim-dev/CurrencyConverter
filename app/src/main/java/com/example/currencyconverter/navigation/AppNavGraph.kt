package com.example.currencyconverter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.currencyconverter.navigation.NavRoutes.Companion.AMOUNT_ARG
import com.example.currencyconverter.navigation.NavRoutes.Companion.FROM_CURRENCY_ARG
import com.example.currencyconverter.navigation.NavRoutes.Companion.RATE_ARG
import com.example.currencyconverter.navigation.NavRoutes.Companion.TO_CURRENCY_ARG
import com.example.currencyconverter.ui.screens.currency.CurrencyScreen
import com.example.currencyconverter.ui.screens.exchange.ExchangeScreen
import com.example.currencyconverter.ui.screens.transactions.TransactionsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Currency.route,
    ) {
        composable(
            route = NavRoutes.Currency.route,
        ) {
            CurrencyScreen(
                onNavigateToExchange = { from, to, amount, rate ->
                    navController.navigate(
                        NavRoutes.Exchange.createRoute(from, to, amount, rate)
                    )
                },
                onNavigateToTransactions = { navController.navigate(NavRoutes.Transactions.route) }
            )
        }
        composable(
            route = NavRoutes.Exchange.route,
            arguments = listOf(
                navArgument(FROM_CURRENCY_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(TO_CURRENCY_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(RATE_ARG) {
                    type = NavType.FloatType
                    defaultValue = 0f
                },
                navArgument(AMOUNT_ARG) {
                    type = NavType.FloatType
                    defaultValue = 0f
                }
            ),
        ) {
            ExchangeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavRoutes.Transactions.route) {
            TransactionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
