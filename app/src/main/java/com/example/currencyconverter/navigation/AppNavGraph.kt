package com.example.currencyconverter.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.currencyconverter.ui.CurrencyScreen
import com.example.currencyconverter.ui.ExchangeScreen
import com.example.currencyconverter.ui.TransactionsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Currency.route,
    ) {
        composable(
            route = NavRoutes.Currency.route,
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        ) {
            CurrencyScreen(
                onNavigateToExchange = { fromCurrency, toCurrency ->
                    navController.navigate(NavRoutes.Exchange.createRoute(fromCurrency, toCurrency))
                }
            )
        }
        composable(
            route = NavRoutes.Exchange.route,
            arguments = listOf(
                navArgument("fromCurrency") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("toCurrency") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            ),
        ) {
            ExchangeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTransactions = { navController.navigate(NavRoutes.Transactions.route) }
            )
        }
        composable(NavRoutes.Transactions.route) {
            TransactionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
