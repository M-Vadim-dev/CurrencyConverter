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
    val mode: CurrencyScreenMode = CurrencyScreenMode.VIEW,
    val targetCurrencyForExchange: String? = null,
    val isConfirmed: Boolean = false,
)

enum class CurrencyScreenMode {
    VIEW,
    EDIT_AMOUNT,
    SELECT_TARGET,
}

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
        when (mode) {
            CurrencyScreenMode.VIEW -> {
                if (selectedCurrency == currency) {
                    _uiState.update {
                        it.copy(
                            mode = CurrencyScreenMode.EDIT_AMOUNT,
                            amount = if (amount <= 0.0) 1.0 else amount,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            selectedCurrency = currency,
                            amount = 1.0,
                            mode = CurrencyScreenMode.VIEW,
                        )
                    }
                }
            }

            CurrencyScreenMode.EDIT_AMOUNT -> {
                if (selectedCurrency == currency) _uiState.update { it.copy(mode = CurrencyScreenMode.SELECT_TARGET) }
                else confirmExchange(currency)
            }

            CurrencyScreenMode.SELECT_TARGET -> {
                confirmExchange(currency)
            }
        }
    }

    fun onAmountChange(newAmount: String) {
        val amountDouble = newAmount.toDouble()
        if (amountDouble.isNaN() || amountDouble.isInfinite() || amountDouble < 0) return

        _uiState.update { it.copy(amount = amountDouble, isConfirmed = false) }

        val state = _uiState.value
        val filtered =
            if (state.mode == CurrencyScreenMode.EDIT_AMOUNT || state.mode == CurrencyScreenMode.SELECT_TARGET) {
                filterAndCalculateRatesWithBalanceCheck(
                    state.rates, state.accounts, amountDouble, state.selectedCurrency
                )
            } else {
                calculateRatesWithoutFilter(state.rates, amountDouble, state.selectedCurrency)
            }

        _uiState.update { it.copy(filteredRates = filtered) }
    }

    fun onResetAmount() {
        _uiState.update { it.copy(amount = 1.0, mode = CurrencyScreenMode.VIEW) }
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
            while (isActive) {
                val state = _uiState.value
                val baseAmount = state.amount
                val selectedCurrency = state.selectedCurrency

                val rates = runCatching {
                    currencyRepository.getRates(selectedCurrency, 1.0)
                }.getOrDefault(emptyList())

                val filtered =
                    if (state.mode == CurrencyScreenMode.EDIT_AMOUNT || state.mode == CurrencyScreenMode.SELECT_TARGET) {
                        filterAndCalculateRatesWithBalanceCheck(
                            rates, state.accounts, baseAmount, selectedCurrency
                        )
                    } else {
                        calculateRatesWithoutFilter(rates, baseAmount, selectedCurrency)
                    }

                _uiState.update {
                    it.copy(rates = rates, filteredRates = filtered)
                }

                delay(1000L)
            }
        }
    }

    private fun filterAndCalculateRatesWithBalanceCheck(
        rates: List<RateDto>,
        accounts: List<AccountDbo>,
        amountToBuy: Double,
        selectedCurrency: String
    ): List<RateDto> {
        val balanceMap = accounts.associate { it.code to it.amount.toDouble() }

        return rates.map { rate ->
            if (rate.currency == selectedCurrency) {
                rate.copy(value = amountToBuy)
            } else {
                rate.copy(value = amountToBuy * rate.value)
            }
        }.filter { rate ->
            if (rate.currency == selectedCurrency) true
            else {
                val balance = balanceMap[rate.currency] ?: 0.0
                balance >= rate.value
            }
        }
    }

    private fun calculateRatesWithoutFilter(
        rates: List<RateDto>, amountToBuy: Double, selectedCurrency: String
    ): List<RateDto> {
        return rates.map { rate ->
            if (rate.currency == selectedCurrency) rate.copy(value = amountToBuy)
            else rate.copy(value = amountToBuy * rate.value)
        }
    }

    private fun confirmExchange(targetCurrency: String) {
        _uiState.update {
            it.copy(
                mode = CurrencyScreenMode.VIEW,
                isConfirmed = true,
                targetCurrencyForExchange = targetCurrency
            )
        }
    }

}
