package com.example.currencyconverter.domain.repository

import com.example.currencyconverter.domain.entity.Transaction

interface TransactionRepository {
    suspend fun getTransactions(): List<Transaction>

    suspend fun insertTransaction(transaction: Transaction)

}