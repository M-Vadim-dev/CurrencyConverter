package com.example.currencyconverter.ui.viewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import com.example.currencyconverter.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class CurrencyScreenState(
    val selectedCurrency: String = "USD",
    val amount: Double = 1.0,
    val rates: List<RateDto> = emptyList(),
    val filteredRates: List<RateDto> = emptyList(),
    val accounts: List<AccountDbo> = emptyList(),
    val balanceMap: Map<String, Double> = emptyMap(),
    val isInputMode: Boolean = false,
    val isEditable: Boolean = false,
    val isConfirmed: Boolean = false,
    val targetCurrencyForExchange: String? = ""
)

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyScreenState())
    val uiState: StateFlow<CurrencyScreenState> = _uiState.asStateFlow()

    init {
        observeAccounts()
        observeRates()
    }

    fun onCurrencyClicked(currency: String) = with(_uiState.value) {
        when {
            isInputMode -> confirmExchange(currency)
            selectedCurrency != currency -> selectCurrency(currency)
            else -> enterInputMode()
        }
    }

    fun onAmountChange(newAmount: Double) {
        _uiState.update { it.copy(amount = newAmount, isConfirmed = false) }
    }

    fun onResetAmount() {
        _uiState.update {
            it.resetUiFlags().copy(amount = 1.0)
        }
    }

    fun setInputMode(enabled: Boolean) {
        _uiState.update {
            if (enabled) {
                it.copy(
                    isInputMode = true,
                    amount = if (it.amount <= 0.0) 1.0 else it.amount
                )
            } else {
                it.resetUiFlags().copy(amount = 1.0)
            }
        }
    }

    fun onConfirmInput(targetCurrency: String) = confirmExchange(targetCurrency)

    fun clearConfirmation() {
        _uiState.update {
            it.copy(
                isConfirmed = false,
                targetCurrencyForExchange = null
            )
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            currencyRepository.getAccountsFlow().collect { accounts ->
                val balanceMap = accounts.associate { it.code to it.amount.toDouble() }
                _uiState.update { it.copy(accounts = accounts, balanceMap = balanceMap) }
            }
        }
    }

    private fun observeRates() {
        viewModelScope.launch {
            tickerFlow(1000L)
                .combine(_uiState) { _, state -> state }
                .collect { state ->
                    val rates = runCatching {
                        currencyRepository.getRates(state.selectedCurrency, state.amount)
                    }.getOrDefault(emptyList())

                    val filtered = if (state.isInputMode) {
                        filterRates(rates, state.accounts, state.amount)
                    } else rates

                    _uiState.update { it.copy(rates = rates, filteredRates = filtered) }
                }
        }
    }

    fun tickerFlow(periodMillis: Long): kotlinx.coroutines.flow.Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(periodMillis)
        }
    }

    private fun confirmExchange(targetCurrency: String) {
        _uiState.update {
            it.copy(isConfirmed = true, targetCurrencyForExchange = targetCurrency)
        }
    }

    private fun selectCurrency(currency: String) {
        _uiState.update {
            it.copy(
                selectedCurrency = currency,
                isInputMode = false,
                isEditable = false,
                amount = 1.0,
                isConfirmed = false
            )
        }
    }

    private fun enterInputMode() {
        _uiState.update {
            it.copy(
                isInputMode = true,
                isEditable = true,
                amount = 1.0,
                isConfirmed = false
            )
        }
    }

    private fun filterRates(
        rates: List<RateDto>,
        accounts: List<AccountDbo>,
        amountToBuy: Double
    ): List<RateDto> {
        val allowed = accounts.filter { account ->
            val rate = rates.find { it.currency == account.code }?.value ?: 0.0
            account.amount >= amountToBuy * rate
        }.map { it.code }.toSet()

        return rates.filter { rate ->
            rate.currency in allowed || rate.currency == _uiState.value.selectedCurrency
        }
    }

    private fun CurrencyScreenState.resetUiFlags() = copy(
        isInputMode = false,
        isEditable = false,
        isConfirmed = false
    )

}
