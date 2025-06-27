package com.example.currencyconverter.domain.repository

import com.example.currencyconverter.domain.entity.Rate

interface RateRepository {
    suspend fun getRates(baseCurrencyCode: String, amount: Double): List<Rate>

}
