package com.example.currencyconverter.data.mapper

import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.domain.entity.Rate

fun RateDto.toDomain(): Rate =
    Rate(
        currency = currency,
        value = value,
    )
