package com.example.currencyconverter.utils

import java.text.DecimalFormat
import java.util.Locale

fun Double.formatTwoDecimalLocalized(): String {
    val format = DecimalFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
    format.applyPattern("#,##0.00")
    return format.format(this)
}
