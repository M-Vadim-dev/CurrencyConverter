package com.example.currencyconverter.domain.entity

enum class Currency {
    USD,
    GBP,
    EUR,
    AUD,
    BGN,
    BRL,
    CAD,
    CHF,
    CNY,
    CZK,
    DKK,
    HKD,
    HRK,
    HUF,
    IDR,
    ILS,
    INR,
    ISK,
    JPY,
    KRW,
    MXN,
    MYR,
    NOK,
    NZD,
    PHP,
    PLN,
    RON,
    RUB,
    SEK,
    SGD,
    THB,
    TRY,
    ZAR;

    companion object {
        fun fromCode(code: String): Currency? {
            return Currency.entries.find { it.name.equals(code, ignoreCase = true) }
        }
    }
}