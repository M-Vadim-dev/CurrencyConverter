package com.example.currencyconverter.ui.screens.currency

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.currencyconverter.R
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyCodeRes
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyIconRes
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencyNameRes
import com.example.currencyconverter.data.mapper.CurrencyMapping.getCurrencySymbolRes
import com.example.currencyconverter.domain.entity.Currency
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme

@Composable
internal fun CurrencyItem(
    currencyCode: String,
    rate: String,
    isSelected: Boolean = false,
    isInputMode: Boolean = false,
    amount: String,
    balance: String = "",
    prefix: String = "",
    showBalance: Boolean = true,
    onAmountChange: (String) -> Unit = {},
    onResetAmount: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val currency = Currency.fromCode(currencyCode)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
        if (currency != null) {
            Icon(
                painter = painterResource(getCurrencyIconRes(currency)),
                contentDescription = stringResource(getCurrencyNameRes(currency)),
                modifier = Modifier.size(70.dp),
                tint = Color.Unspecified
            )
        } else {
            Icon(
                imageVector = Icons.Default.Warning,
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

                if (showBalance && (!isInputMode || !isSelected)) {
                    Text(
                        text = stringResource(R.string.balance_label) + ": " +
                                stringResource(getCurrencySymbolRes(currency)) + balance,
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
                    text = prefix + stringResource(getCurrencySymbolRes(it)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            BasicTextField(
                value = if (isSelected && isInputMode) amount else rate,
                onValueChange = { onAmountChange(it) },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                ),
                enabled = isInputMode && isSelected,
                modifier = Modifier.width(IntrinsicSize.Min)
            )
            if (isInputMode && isSelected) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onResetAmount() })
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
            rate = "0.85",
            isInputMode = false,
            isSelected = false,
            amount = "100.0",
            balance = "1000.0",
            onAmountChange = {},
            onResetAmount = {},
            onClick = {},
        )
    }
}

@Preview(showBackground = true, locale = "ru")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ru")
@Composable
fun CurrencyItemEditPreview() {
    CurrencyConverterTheme {
        CurrencyItem(
            currencyCode = "RUB",
            rate = "0.85",
            isInputMode = true,
            isSelected = true,
            amount = "1000.0",
            balance = "75000.0",
            onAmountChange = {},
            onResetAmount = {},
            onClick = {},
        )
    }
}
