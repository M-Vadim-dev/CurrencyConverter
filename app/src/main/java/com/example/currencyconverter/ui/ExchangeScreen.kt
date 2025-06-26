package com.example.currencyconverter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconverter.R
import com.example.currencyconverter.data.mapper.CurrencyMapping
import com.example.currencyconverter.domain.entity.Currency
import com.example.currencyconverter.ui.viewModel.ExchangeViewModel
import com.example.currencyconverter.utils.formatTwoDecimalLocalized


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
            .padding(8.dp)
    ) {

        Text(
            text = stringResource(
                id = R.string.exchange_format,
                uiState.fromCurrency,
                uiState.toCurrency
            ),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(
                id = R.string.exchange_rate_format,
                getCurrencySymbol(uiState.fromCurrency),
                uiState.exchangeRate.formatTwoDecimalLocalized()
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
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
            onClick = { onNavigateToTransactions },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = !uiState.isLoading && uiState.fromAmount > 0.0 && uiState.toAmount > 0.0
        ) {
            Text(
                text = stringResource(
                    id = R.string.buy_currency_format,
                    stringResource(id = CurrencyMapping.getCurrencyNameRes(Currency.fromCode(uiState.toCurrency)!!)),
                    stringResource(id = CurrencyMapping.getCurrencyNameRes(Currency.fromCode(uiState.fromCurrency)!!))
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
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

@Composable
fun getCurrencySymbol(currencyCode: String): String {
    val currency = Currency.fromCode(currencyCode)
        ?: return ""

    val symbolResId = try {
        CurrencyMapping.getCurrencySymbolRes(currency)
    } catch (_: Exception) {
        return currencyCode
    }

    return stringResource(id = symbolResId)
}
