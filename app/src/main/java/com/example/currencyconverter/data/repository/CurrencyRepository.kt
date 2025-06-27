package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.dataSource.remote.RatesService
import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.data.dataSource.room.account.dao.AccountDao
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import com.example.currencyconverter.data.dataSource.room.transaction.dao.TransactionDao
import com.example.currencyconverter.data.dataSource.room.transaction.dbo.TransactionDbo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val ratesService: RatesService,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
) {

    fun getAccountsFlow(): Flow<List<AccountDbo>> = accountDao.getAllAsFlow()

    suspend fun getAccounts(): List<AccountDbo> = accountDao.getAll()

    suspend fun getTransactions(): List<TransactionDbo> = transactionDao.getAll()

    suspend fun insertOrUpdateAccounts(vararg accounts: AccountDbo) {
        accountDao.insertAll(*accounts)
    }

    suspend fun insertTransaction(transaction: TransactionDbo) {
        transactionDao.insertAll(transaction)
    }

    suspend fun getRates(baseCurrencyCode: String, amount: Double): List<RateDto> =
        ratesService.getRates(baseCurrencyCode, amount)

    suspend fun saveTransaction(
        fromCurrency: String,
        toCurrency: String,
        fromAmount: Double,
        toAmount: Double,
        dateTime: LocalDateTime,
    ) {
        val transaction = TransactionDbo(
            id = 0,
            from = fromCurrency,
            to = toCurrency,
            fromAmount = fromAmount,
            toAmount = toAmount,
            dateTime = dateTime,
        )
        insertTransaction(transaction)
    }

}
