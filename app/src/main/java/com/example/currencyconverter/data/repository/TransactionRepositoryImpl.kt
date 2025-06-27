package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.dataSource.room.transaction.dao.TransactionDao
import com.example.currencyconverter.data.mapper.toDbo
import com.example.currencyconverter.data.mapper.toDomain
import com.example.currencyconverter.domain.entity.Transaction
import com.example.currencyconverter.domain.repository.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {

    override suspend fun getTransactions(): List<Transaction> {
        val transactionsDbo = transactionDao.getAll()
        return transactionsDbo.map { it.toDomain() }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertAll(transaction.toDbo())
    }

}
