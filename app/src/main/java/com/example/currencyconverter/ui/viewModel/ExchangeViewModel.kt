package com.example.currencyconverter.ui.viewModel

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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class ExchangeScreenState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "",
    val fromAmount: Double = 0.0,
    val toAmount: Double = 0.0,
    val fromBalance: Double = 0.0,
    val toBalance: Double = 0.0,
    val exchangeRate: Double = 0.0,
    val accounts: List<AccountDbo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
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

        _uiState.update {
            it.copy(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency
            )
        }

        viewModelScope.launch {
            repository.getAccountsFlow().collect { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
            }
        }

        viewModelScope.launch {
            combine(
                _uiState.map { it.fromAmount },
                _uiState.map { it.fromCurrency },
                _uiState.map { it.toCurrency }
            ) { fromAmount, fromCurrency, toCurrency ->
                Triple(fromAmount, fromCurrency, toCurrency)
            }.distinctUntilChanged()
                .collect {
                    recalculateToAmount()
                }
        }
    }

    fun setFromAmount(amount: Double) {
        _uiState.update { it.copy(fromAmount = amount) }
    }

    private fun recalculateToAmount() {
        val state = _uiState.value
        if (state.fromCurrency.isEmpty() || state.toCurrency.isEmpty()) return

        viewModelScope.launch {
            try {
                val rates = repository.getRates(state.fromCurrency, state.fromAmount)
                val rate = rates.find { it.currency == state.toCurrency }?.value ?: 0.0
                _uiState.update {
                    it.copy(
                        exchangeRate = rate,
                        toAmount = state.fromAmount * rate,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Ошибка загрузки курса") }
            }
        }
    }

}

