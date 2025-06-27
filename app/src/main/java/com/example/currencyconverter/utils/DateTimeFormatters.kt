package com.example.currencyconverter.utils

import java.time.format.DateTimeFormatter

object DateTimeFormatters {
    val DEFAULT_DATE_TIME_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
}