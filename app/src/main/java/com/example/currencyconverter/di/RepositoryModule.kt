package com.example.currencyconverter.di

import com.example.currencyconverter.data.repository.AccountRepositoryImpl
import com.example.currencyconverter.domain.repository.AccountRepository
import com.example.currencyconverter.data.repository.TransactionRepositoryImpl
import com.example.currencyconverter.domain.repository.TransactionRepository
import com.example.currencyconverter.data.repository.RateRepositoryImpl
import com.example.currencyconverter.domain.repository.RateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    abstract fun bindRateRepository(
        impl: RateRepositoryImpl
    ): RateRepository

}
