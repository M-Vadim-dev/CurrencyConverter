package com.example.currencyconverter.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.currencyconverter.R
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyCodeRes
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyIconRes
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyNameRes
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencySymbolRes
import com.example.currencyconverter.domain.entity.Currency
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme
import com.example.currencyconverter.ui.viewModel.CurrencyViewModel
import com.example.currencyconverter.utils.formatTwoDecimalLocalized

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    onNavigateToExchange: (String) -> Unit,
    viewModel: CurrencyViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedCurrency) {
        val index =
            viewModel.getFilteredRates().indexOfFirst { it.currency == state.selectedCurrency }
        if (index >= 0) listState.animateScrollToItem(index)
    }

    LaunchedEffect(state.isConfirmed, state.targetCurrencyForExchange) {
        if (state.isConfirmed && state.targetCurrencyForExchange != null) {
            val fromCurrency = state.targetCurrencyForExchange
            onNavigateToExchange(fromCurrency.toString())

            viewModel.setInputMode(false)
            viewModel.clearConfirmation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()

            .systemBarsPadding()
    ) {
        LazyColumn(state = listState) {
            items(state.filteredRates, key = { it.currency }) { rate ->

                val isEditableThisItem =
                    state.isInputMode && rate.currency == state.selectedCurrency

                CurrencyItem(
                    currencyCode = rate.currency,
                    rate = rate.value,
                    isInputMode = state.isInputMode,
                    isEditable = isEditableThisItem,
                    amount = state.amount,
                    balance = state.balanceMap[rate.currency] ?: 0.0,
                    onAmountChange = { newAmount ->
                        viewModel.onAmountChange(newAmount)
                    },
                    onResetAmount = { viewModel.onResetAmount() },
                    onClick = {
                        viewModel.onCurrencyClicked(rate.currency)
                    },
                    onConfirmInput = {
                        viewModel.onConfirmInput(rate.currency)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CurrencyItem(
    currencyCode: String,
    rate: Double,
    isInputMode: Boolean,
    isEditable: Boolean,
    amount: Double = 0.0,
    balance: Double = 0.0,
    onAmountChange: (Double) -> Unit = {},
    onResetAmount: () -> Unit = {},
    onClick: () -> Unit = {},
    onConfirmInput: () -> Unit = {}
) {
    val currency = Currency.fromCode(currencyCode)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (currency != null) {
            Icon(
                painter = painterResource(getCurrencyIconRes(currency)),
                contentDescription = stringResource(getCurrencyNameRes(currency)),
                modifier = Modifier.size(70.dp),
                tint = Color.Unspecified
            )
        } else {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (currency != null) {
                Text(
                    text = stringResource(getCurrencyCodeRes(currency)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(getCurrencyNameRes(currency)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (!isInputMode == !isEditable) {
                    Text(
                        text = stringResource(R.string.balance_label) + ": " +
                                stringResource(getCurrencySymbolRes(currency)) +
                                balance.formatTwoDecimalLocalized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            currency?.let {
                Text(
                    text = stringResource(getCurrencySymbolRes(it)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            val displayValue = if (isInputMode) {
                amount.formatTwoDecimalLocalized()
            } else {
                rate.formatTwoDecimalLocalized()
            }

            BasicTextField(
                value = displayValue,
                onValueChange = {
                    if (isEditable) {

                        val value = it.toDoubleOrNull()
                        if (value != null) {
                            onAmountChange(value)
                        }
                    }
                },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onConfirmInput()
                    }
                ),
                modifier = Modifier.width(IntrinsicSize.Min)
            )
            if (isInputMode && isEditable) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onResetAmount() }
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CurrencyItemPreview() {
    CurrencyConverterTheme {
        CurrencyItem(
            currencyCode = "EUR",
            rate = 0.85,
            isInputMode = false,
            isEditable = false,
            amount = 100.0,
            balance = 1000.0,
            onAmountChange = {},
            onResetAmount = {},
            onClick = {},

            )
    }
}

@Preview(showBackground = true, locale = "ru")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ru")
@Composable
fun CurrencyItemPreview2() {
    CurrencyConverterTheme {
        CurrencyItem(
            currencyCode = "RUB",
            rate = 0.85,
            isInputMode = true,
            isEditable = false,
            amount = 1000.0,
            balance = 75000.0,
            onAmountChange = {},
            onResetAmount = {},
            onClick = {})
    }
}
