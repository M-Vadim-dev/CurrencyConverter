package com.example.currencyconverter.domain.repository

import com.example.currencyconverter.domain.entity.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountsFlow(): Flow<List<Account>>

    suspend fun getAccounts(): List<Account>

    suspend fun insertOrUpdateAccounts(vararg accounts: Account)

}
