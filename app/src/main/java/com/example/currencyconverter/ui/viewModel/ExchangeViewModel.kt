package com.example.currencyconverter.ui.viewModel

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import com.example.currencyconverter.data.repository.CurrencyRepository
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

enum class ExchangeError {
    INVALID_AMOUNT_OR_RATE,
    EXCHANGE_FAILED,
}

@Immutable
data class ExchangeScreenState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "",
    val fromAmount: Double = 0.0,
    val toAmount: Double = 0.0,
    val balanceAfterExchange: Double = 0.0,
    val exchangeRate: Double = 0.0,
    val accounts: List<AccountDbo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: ExchangeError? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeScreenState())
    val uiState: StateFlow<ExchangeScreenState> = _uiState.asStateFlow()

    init {
        val fromCurrency = savedStateHandle.get<String>("fromCurrency") ?: "USD"
        val toCurrency = savedStateHandle.get<String>("toCurrency") ?: ""
        val rate = savedStateHandle.get<Float>("rare")?.toDouble() ?: 0.0
        val amount = savedStateHandle.get<Float>("amount")?.toDouble() ?: 0.0
        Log.d("!!!", "курс = $rate, сумма = $amount ")

        _uiState.update {
            it.copy(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                exchangeRate = rate,
                fromAmount = amount,
                toAmount = rate,
            )
        }

        viewModelScope.launch {
            combine(
                repository.getAccountsFlow(),
                _uiState.map { it.fromAmount }
            ) { accounts, fromAmount ->
                val fromBalance = accounts.find { it.code == fromCurrency }?.amount ?: 0.0
                Triple(accounts, fromBalance, fromAmount)
            }.collect { (accounts, fromBalance, toAmount) ->
                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        balanceAfterExchange = fromBalance - toAmount
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun performExchange() {
        val state = _uiState.value

        val error = when {
            state.fromAmount <= 0.0 || state.toAmount <= 0.0 ||
                    state.exchangeRate <= 0.0 ||
                    state.fromCurrency == state.toCurrency -> ExchangeError.INVALID_AMOUNT_OR_RATE

            state.fromAmount > state.balanceAfterExchange -> ExchangeError.EXCHANGE_FAILED

            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            try {
                repository.saveTransaction(
                    fromCurrency = state.fromCurrency,
                    toCurrency = state.toCurrency,
                    fromAmount = state.fromAmount,
                    toAmount = state.toAmount,
                    dateTime = LocalDateTime.now(),
                )

                repository.updateAccountsAfterExchange(
                    fromCurrency = state.fromCurrency,
                    toCurrency = state.toCurrency,
                    fromAmount = state.fromAmount,
                    toAmount = state.toAmount
                )

                _uiState.update {
                    it.copy(isSuccess = true, isLoading = false, errorMessage = null)
                }

            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorMessage = ExchangeError.EXCHANGE_FAILED, isLoading = false)
                }
            }
        }
    }
}
