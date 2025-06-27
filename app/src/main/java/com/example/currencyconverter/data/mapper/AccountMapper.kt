package com.example.currencyconverter.data.mapper

import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import com.example.currencyconverter.domain.entity.Account

fun AccountDbo.toDomain(): Account =
    Account(
        code = code,
        amount = amount,
    )

fun Account.toDbo(): AccountDbo =
    AccountDbo(
        code = code,
        amount = amount,
    )
