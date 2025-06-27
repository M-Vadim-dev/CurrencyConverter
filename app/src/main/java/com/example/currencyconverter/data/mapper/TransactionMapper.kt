package com.example.currencyconverter.data.mapper

import com.example.currencyconverter.data.dataSource.room.transaction.dbo.TransactionDbo
import com.example.currencyconverter.domain.entity.Transaction

fun TransactionDbo.toDomain(): Transaction =
    Transaction(
        id = id,
        from = from,
        to = to,
        fromAmount = fromAmount,
        toAmount = toAmount,
        dateTime = dateTime,
    )

fun Transaction.toDbo(): TransactionDbo =
    TransactionDbo(
        id = id,
        from = from,
        to = to,
        fromAmount = fromAmount,
        toAmount = toAmount,
        dateTime = dateTime,
    )
