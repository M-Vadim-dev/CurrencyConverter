package com.example.currencyconverter.ui.viewModel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.dataSource.remote.dto.RateDto
import com.example.currencyconverter.data.dataSource.room.account.dbo.AccountDbo
import com.example.currencyconverter.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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

    private var ratesJob: Job? = null

    init {
        viewModelScope.launch {
            currencyRepository.getAccountsFlow()
                .collect { accounts ->
                    val balanceMap = accounts.associate { it.code to it.amount.toDouble() }
                    _uiState.update { it.copy(accounts = accounts, balanceMap = balanceMap) }
                }
        }
        startRatesUpdates()
    }

    fun onCurrencyClicked(currency: String) {
        val state = _uiState.value
        when {
            state.isInputMode -> {
                _uiState.update {
                    it.copy(
                        isConfirmed = true,
                        targetCurrencyForExchange = currency
                    )
                }
            }

            state.selectedCurrency != currency -> {
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

            state.selectedCurrency == currency && !state.isInputMode -> {
                _uiState.update {
                    it.copy(
                        isInputMode = true,
                        isEditable = true,
                        amount = 1.0,
                        isConfirmed = false
                    )
                }
            }
        }
    }

    fun onAmountChange(newAmount: Double) {
        _uiState.update {
            it.copy(amount = newAmount, isConfirmed = false)
        }
    }

    fun onResetAmount() {
        _uiState.update {
            it.copy(
                amount = 1.0,
                isInputMode = false,
                isEditable = false,
                isConfirmed = false
            )
        }
    }

    fun getFilteredRates(): List<RateDto> {
        val state = _uiState.value
        return if (state.isInputMode) {
            filterRates(state.rates, state.accounts, state.amount)
        } else {
            state.rates
        }
    }

    fun setInputMode(isInputMode: Boolean) {
        _uiState.update {
            if (isInputMode) it.copy(
                isInputMode = true,
                amount = if (it.amount <= 0.0) 1.0 else it.amount
            )
            else it.copy(isInputMode = false, amount = 1.0)
        }
        startRatesUpdates()
    }

    fun onConfirmInput(targetCurrency: String) {
        _uiState.update {
            it.copy(
                isConfirmed = true,
                targetCurrencyForExchange = targetCurrency
            )
        }
    }

    fun clearConfirmation() {
        _uiState.update {
            it.copy(
                isConfirmed = false,
                targetCurrencyForExchange = null
            )
        }
    }

    private fun startRatesUpdates() {
        ratesJob?.cancel()
        ratesJob = viewModelScope.launch {
            while (isActive) {
                val state = _uiState.value
                val rates = try {
                    currencyRepository.getRates(state.selectedCurrency, state.amount)
                } catch (_: Exception) {
                    emptyList()
                }

                val filteredRates = if (state.isInputMode) {
                    filterRates(rates, state.accounts, state.amount)
                } else {
                    rates
                }

                _uiState.update {
                    it.copy(rates = rates, filteredRates = filteredRates)
                }

                delay(1000L)
            }
        }
    }

    private fun filterRates(
        rates: List<RateDto>,
        accounts: List<AccountDbo>,
        amountToBuy: Double
    ): List<RateDto> {
        val allowedCurrencies = accounts.filter { account ->
            val rate = rates.find { it.currency == account.code }?.value ?: 0.0
            val requiredAmount = amountToBuy * rate
            account.amount >= requiredAmount
        }.map { it.code }.toSet()

        return rates.filter { allowedCurrencies.contains(it.currency) }
    }

}
