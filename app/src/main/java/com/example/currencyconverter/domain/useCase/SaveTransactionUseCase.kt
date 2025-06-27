package com.example.currencyconverter.domain.useCase

import com.example.currencyconverter.domain.entity.Transaction
import com.example.currencyconverter.domain.repository.TransactionRepository
import java.time.LocalDateTime
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(
        fromCurrency: String,
        toCurrency: String,
        fromAmount: Double,
        toAmount: Double,
        dateTime: LocalDateTime,
    ) {
        val transaction = Transaction(
            id = 0,
            from = fromCurrency,
            to = toCurrency,
            fromAmount = fromAmount,
            toAmount = toAmount,
            dateTime = dateTime,
        )
        repository.insertTransaction(transaction)
    }

}
