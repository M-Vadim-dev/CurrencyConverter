package com.example.currencyconverter.ui.screens.exchange

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.repository.AccountRepository
import com.example.currencyconverter.domain.useCase.SaveTransactionUseCase
import com.example.currencyconverter.navigation.NavRoutes.Companion.AMOUNT_ARG
import com.example.currencyconverter.navigation.NavRoutes.Companion.FROM_CURRENCY_ARG
import com.example.currencyconverter.navigation.NavRoutes.Companion.RATE_ARG
import com.example.currencyconverter.navigation.NavRoutes.Companion.TO_CURRENCY_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeScreenState())
    val uiState: StateFlow<ExchangeScreenState> = _uiState.asStateFlow()

    init {
        val fromCurrency = savedStateHandle.get<String>(FROM_CURRENCY_ARG) ?: "USD"
        val toCurrency = savedStateHandle.get<String>(TO_CURRENCY_ARG) ?: ""
        val rate = savedStateHandle.get<Float>(RATE_ARG)?.toDouble() ?: 0.0
        val amount = savedStateHandle.get<Float>(AMOUNT_ARG)?.toDouble() ?: 0.0

        _uiState.update {
            it.copy(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                exchangeRate = rate / amount,
                fromAmount = amount,
                toAmount = rate,
            )
        }

        viewModelScope.launch {
            combine(
                accountRepository.getAccountsFlow(),
                _uiState.map { it.fromAmount }
            ) { accounts, fromAmount ->
                val fromBalance = accounts.find { it.code == toCurrency }?.amount ?: 0.0
                Triple(accounts, fromBalance, fromAmount)
            }.collect { (accounts, fromBalance, toAmount) ->
                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        balanceAfterExchange = fromBalance - rate
                    )
                }
            }
        }
    }

    internal fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    internal fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    internal fun performExchange() {
        val state = _uiState.value

        val error = when {
            state.exchangeRate <= 0.0 || state.fromCurrency == state.toCurrency -> ExchangeError.INVALID_AMOUNT_OR_RATE
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                saveTransactionUseCase.invoke(
                    fromCurrency = state.fromCurrency,
                    toCurrency = state.toCurrency,
                    fromAmount = state.fromAmount,
                    toAmount = state.toAmount,
                    dateTime = LocalDateTime.now(),
                )
                _uiState.update {
                    it.copy(isSuccess = true, isLoading = false, errorMessage = null)
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = ExchangeError.EXCHANGE_FAILED,
                        isLoading = false,
                        isSuccess = false
                    )
                }
            }
        }
    }

}
