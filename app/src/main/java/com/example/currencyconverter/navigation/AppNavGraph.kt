package com.example.currencyconverter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.currencyconverter.ui.screens.CurrencyScreen
import com.example.currencyconverter.ui.screens.ExchangeScreen
import com.example.currencyconverter.ui.screens.TransactionsScreen

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
                navArgument("fromCurrency") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("toCurrency") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("rate") {
                    type = NavType.FloatType
                    defaultValue = 0f
                },
                navArgument("amount") {
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
