package com.example.currencyconverter.data.repository

import com.example.currencyconverter.data.dataSource.room.account.dao.AccountDao
import com.example.currencyconverter.data.mapper.toDbo
import com.example.currencyconverter.data.mapper.toDomain
import com.example.currencyconverter.domain.entity.Account
import com.example.currencyconverter.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
) : AccountRepository {

    override fun getAccountsFlow(): Flow<List<Account>> =
        accountDao.getAllAsFlow().map { it.map { dbo -> dbo.toDomain() } }

    override suspend fun getAccounts(): List<Account> =
        accountDao.getAll().map { it.toDomain() }

    override suspend fun insertOrUpdateAccounts(vararg accounts: Account) {
        accountDao.insertAll(*accounts.map { it.toDbo() }.toTypedArray())
    }

}
