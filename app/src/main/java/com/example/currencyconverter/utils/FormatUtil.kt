package com.example.currencyconverter.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.formatTwoDecimalLocalized(): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    val format = DecimalFormat("#,##0.00", symbols)
    return format.format(this)
}
