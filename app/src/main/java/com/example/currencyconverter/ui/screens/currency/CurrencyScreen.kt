package com.example.currencyconverter.ui.screens.currency

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconverter.utils.formatTwoDecimalLocalized

@Composable
internal fun CurrencyScreen(
    viewModel: CurrencyViewModel = hiltViewModel(),
    onNavigateToExchange: (fromCurrency: String, toCurrency: String, rate: Double, amount: Double) -> Unit,
    onNavigateToTransactions: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedCurrency) {
        val index = state.filteredRates.indexOfFirst { it.currency == state.selectedCurrency }
        if (index >= 0) listState.animateScrollToItem(index)
    }

    LaunchedEffect(state.isConfirmed) {
        if (state.isConfirmed) {
            state.targetCurrencyForExchange?.let { targetCurrency ->
                val rate =
                    state.filteredRates.firstOrNull { it.currency == targetCurrency }?.value ?: 0.0
                onNavigateToExchange(state.selectedCurrency, targetCurrency, rate, state.amount)
                viewModel.onNavigationHandled()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface, floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTransactions) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = null,
                )
            }
        }) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            LazyColumn(state = listState) {
                items(state.filteredRates, key = { it.currency }) { rate ->

                    CurrencyItem(
                        currencyCode = rate.currency,
                        rate = rate.value.formatTwoDecimalLocalized(),
                        isSelected = rate.currency == state.selectedCurrency,
                        isInputMode = state.mode == CurrencyScreenMode.EDIT_AMOUNT,
                        amount = state.amount.toString(),
                        balance = (state.balanceMap[rate.currency]
                            ?: 0.0).formatTwoDecimalLocalized(),
                        onAmountChange = { newAmount -> viewModel.onAmountChange(newAmount) },
                        onResetAmount = { viewModel.onResetAmount() },
                        onClick = { viewModel.onCurrencyClicked(rate.currency) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
