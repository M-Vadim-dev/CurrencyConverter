package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.dataSource.remote.RatesService
import com.example.currencyconverter.data.mapper.toDomain
import com.example.currencyconverter.domain.entity.Rate
import com.example.currencyconverter.domain.repository.RateRepository
import javax.inject.Inject

class RateRepositoryImpl @Inject constructor(
    private val ratesService: RatesService,
) : RateRepository {

    override suspend fun getRates(baseCurrencyCode: String, amount: Double): List<Rate> =
        ratesService.getRates(baseCurrencyCode, amount).map { it.toDomain() }

}
