package com.example.currencyconverter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconverter.R
import com.example.currencyconverter.ui.viewModel.ExchangeViewModel


@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTransactions: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
Row() {
    Text(
        text = uiState.fromCurrency,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(
        text = " to ",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(
        text = uiState.toCurrency,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}
        Row() {
        Text(
            text = stringResource(R.string.balance_label) + " = ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = uiState.exchangeRate.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        CurrencyItem(
            currencyCode = uiState.fromCurrency,
            rate = 1.0,
            isInputMode = false,
            isEditable = false,
            amount = uiState.fromAmount,
            balance = uiState.toBalance,
            onAmountChange = { viewModel.setFromAmount(it) },
            onResetAmount = { viewModel.setFromAmount(0.0) },
            onClick = { },

        )

        Spacer(modifier = Modifier.height(16.dp))

        CurrencyItem(
            currencyCode = uiState.toCurrency,
            rate = uiState.exchangeRate,
            isInputMode = false,
            isEditable = false,
            amount = uiState.toAmount,
            balance = uiState.toBalance,
            onAmountChange = {},
            onResetAmount = {},
            onClick = {},
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {

            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && uiState.fromAmount > 0.0 && uiState.toAmount > 0.0
        ) {
            Text(text = "Buy Euro for Russia Rouble")
        }

        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
