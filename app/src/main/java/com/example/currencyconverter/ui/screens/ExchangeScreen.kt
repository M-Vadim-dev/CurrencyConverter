package com.example.currencyconverter.ui.screens

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconverter.R
import com.example.currencyconverter.data.mapper.CurrencyMapping
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyNameRes
import com.example.currencyconverter.domain.entity.Currency.Companion.fromCode
import com.example.currencyconverter.ui.components.CurrencyItem
import com.example.currencyconverter.ui.viewModel.ExchangeError
import com.example.currencyconverter.ui.viewModel.ExchangeViewModel
import com.example.currencyconverter.utils.formatTwoDecimalLocalized


@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            val message = when (error) {
                ExchangeError.INVALID_AMOUNT_OR_RATE -> context.getString(R.string.error_invalid_amount_or_rate)
                ExchangeError.EXCHANGE_FAILED -> context.getString(R.string.error_exchange_failed)
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(8.dp)
    ) {

        Text(
            text = stringResource(
                R.string.exchange_format, uiState.toCurrency, uiState.fromCurrency
            ),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(
                R.string.exchange_rate_format,
                getCurrencySymbol(uiState.fromCurrency),
                getCurrencySymbol(uiState.toCurrency),
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
            rate = "+${uiState.fromAmount.formatTwoDecimalLocalized()}",
            amount = uiState.fromAmount.formatTwoDecimalLocalized(),
            balance = "",
        )

        Spacer(modifier = Modifier.height(16.dp))

        CurrencyItem(
            currencyCode = uiState.toCurrency,
            rate = "-${uiState.toAmount.formatTwoDecimalLocalized()}",
            amount = uiState.toAmount.formatTwoDecimalLocalized(),
            balance = uiState.balanceAfterExchange.formatTwoDecimalLocalized(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.performExchange() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = !uiState.isLoading && uiState.fromAmount > 0.0
        ) {
            Text(
                text = stringResource(
                    id = R.string.buy_currency_format,
                    stringResource(id = getCurrencyNameRes(fromCode(uiState.toCurrency)!!)),
                    stringResource(id = getCurrencyNameRes(fromCode(uiState.fromCurrency)!!))
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun getCurrencySymbol(currencyCode: String): String {
    val currency = fromCode(currencyCode) ?: return ""

    val symbolResId = try {
        CurrencyMapping.getCurrencySymbolRes(currency)
    } catch (_: Exception) {
        return currencyCode
    }

    return stringResource(id = symbolResId)
}
