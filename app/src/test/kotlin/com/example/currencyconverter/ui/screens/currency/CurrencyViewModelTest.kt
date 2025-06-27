package com.example.currencyconverter.ui.screens.currency

import com.example.currencyconverter.domain.entity.Account
import com.example.currencyconverter.domain.entity.Rate
import com.example.currencyconverter.domain.repository.AccountRepository
import com.example.currencyconverter.domain.repository.RateRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class CurrencyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: AccountRepository
    private lateinit var rateRepository: RateRepository
    private lateinit var viewModel: CurrencyViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        accountRepository = mockk()
        rateRepository = mockk()

        coEvery { accountRepository.getAccountsFlow() } returns flowOf(
            listOf(Account("USD", 1000.0), Account("EUR", 500.0))
        )
        coEvery { rateRepository.getRates(any(), any()) } returns listOf(
            Rate("USD", 1.0),
            Rate("EUR", 0.9),
            Rate("JPY", 110.0)
        )

        viewModel = CurrencyViewModel(accountRepository, rateRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains accounts and rates`() = runTest {
        advanceTimeBy(1500L)

        val state = viewModel.uiState.value

        assertEquals(2, state.accounts.size)
        assertEquals("USD", state.accounts[0].code)

        assert(state.rates.isNotEmpty())
    }

    @Test
    fun `onCurrencyClicked in VIEW mode selects currency and resets amount`() = runTest {
        viewModel.onCurrencyClicked("EUR")

        val state = viewModel.uiState.value

        assertEquals("EUR", state.selectedCurrency)
        assertEquals(1.0, state.amount)
        assertEquals(CurrencyScreenMode.VIEW, state.mode)
    }

    @Test
    fun `onCurrencyClicked in VIEW mode clicking selected currency changes mode to EDIT_AMOUNT`() = runTest {
        viewModel.onCurrencyClicked("EUR")
        viewModel.onCurrencyClicked("EUR")

        val state = viewModel.uiState.value
        assertEquals(CurrencyScreenMode.EDIT_AMOUNT, state.mode)
        assert(state.amount > 0.0)
    }

    @Test
    fun `onAmountChange updates amount and filteredRates`() = runTest {
        viewModel.onCurrencyClicked("EUR")
        viewModel.onCurrencyClicked("EUR")
        viewModel.onAmountChange("10")

        val state = viewModel.uiState.value
        assertEquals(10.0, state.amount)
        assert(state.filteredRates.isNotEmpty())

        val eurRate = state.filteredRates.find { it.currency == "EUR" }
        assertEquals(10.0, eurRate?.value)
    }

    @Test
    fun `onResetAmount resets amount to 1 and mode to VIEW`() = runTest {
        viewModel.onAmountChange("10")
        viewModel.onResetAmount()

        val state = viewModel.uiState.value
        assertEquals(1.0, state.amount)
        assertEquals(CurrencyScreenMode.VIEW, state.mode)
    }

    @Test
    fun `onCurrencyClicked in EDIT_AMOUNT mode selecting same currency sets mode SELECT_TARGET`() = runTest {
        viewModel.onCurrencyClicked("USD")
        viewModel.onCurrencyClicked("USD")
        viewModel.onCurrencyClicked("USD")

        val state = viewModel.uiState.value
        assertEquals(CurrencyScreenMode.SELECT_TARGET, state.mode)
    }

    @Test
    fun `onCurrencyClicked in EDIT_AMOUNT mode selecting different currency confirms exchange`() = runTest {
        viewModel.onCurrencyClicked("USD")
        viewModel.onCurrencyClicked("EUR")

        val state = viewModel.uiState.value
        assertEquals(CurrencyScreenMode.VIEW, state.mode)
        assert(state.isConfirmed)
        assertEquals("EUR", state.targetCurrencyForExchange)
    }

    @Test
    fun `onCurrencyClicked in SELECT_TARGET mode confirms exchange`() = runTest {
        viewModel.onCurrencyClicked("USD")
        viewModel.onCurrencyClicked("USD")
        viewModel.onCurrencyClicked("EUR")
        viewModel.onCurrencyClicked("RUB")

        val state = viewModel.uiState.value
        assertEquals(CurrencyScreenMode.VIEW, state.mode)
        assert(state.isConfirmed)
        assertEquals("RUB", state.targetCurrencyForExchange)
    }

    @Test
    fun `onNavigationHandled resets targetCurrencyForExchange`() = runTest {
        viewModel.onCurrencyClicked("USD")
        viewModel.onCurrencyClicked("EUR")

        viewModel.onNavigationHandled()

        val state = viewModel.uiState.value
        assertEquals(null, state.targetCurrencyForExchange)
    }

}
