package com.example.currencyconverter.ui.screens.transactions

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.entity.Transaction
import com.example.currencyconverter.ui.theme.CurrencyConverterTheme
import com.example.currencyconverter.utils.DateTimeFormatters.DEFAULT_DATE_TIME_FORMATTER
import com.example.currencyconverter.utils.formatTwoDecimalLocalized
import java.time.LocalDateTime

@Composable
internal fun TransactionItem(
    transaction: Transaction,
) {
    val formatter = remember { DEFAULT_DATE_TIME_FORMATTER }
    val formattedDate = remember(transaction.dateTime) {
        transaction.dateTime.format(formatter)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${transaction.toAmount.formatTwoDecimalLocalized()} ${transaction.to} → ${transaction.fromAmount.formatTwoDecimalLocalized()} ${transaction.from}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedDate,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ru")
@Composable
fun TransactionItemPreview() {
    val sampleTransaction = Transaction(
        from = stringResource(R.string.currency_rub),
        to = stringResource(R.string.currency_eur),
        fromAmount = 880.0,
        toAmount = 100.0,
        dateTime = LocalDateTime.now()
    )
    CurrencyConverterTheme {
        TransactionItem(transaction = sampleTransaction)
    }
}
