package com.example.currencyconverter.data.mapper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.entity.Currency

object CurrencyMapping {

    private val nameResMap = mapOf(
        Currency.USD to R.string.currency_usd,
        Currency.GBP to R.string.currency_gbp,
        Currency.EUR to R.string.currency_eur,
        Currency.AUD to R.string.currency_aud,
        Currency.BGN to R.string.currency_bgn,
        Currency.BRL to R.string.currency_brl,
        Currency.CAD to R.string.currency_cad,
        Currency.CHF to R.string.currency_chf,
        Currency.CNY to R.string.currency_cny,
        Currency.CZK to R.string.currency_czk,
        Currency.DKK to R.string.currency_dkk,
        Currency.HKD to R.string.currency_hkd,
        Currency.HRK to R.string.currency_hrk,
        Currency.HUF to R.string.currency_huf,
        Currency.IDR to R.string.currency_idr,
        Currency.ILS to R.string.currency_ils,
        Currency.INR to R.string.currency_inr,
        Currency.ISK to R.string.currency_isk,
        Currency.JPY to R.string.currency_jpy,
        Currency.KRW to R.string.currency_krw,
        Currency.MXN to R.string.currency_mxn,
        Currency.MYR to R.string.currency_myr,
        Currency.NOK to R.string.currency_nok,
        Currency.NZD to R.string.currency_nzd,
        Currency.PHP to R.string.currency_php,
        Currency.PLN to R.string.currency_pln,
        Currency.RON to R.string.currency_ron,
        Currency.RUB to R.string.currency_rub,
        Currency.SEK to R.string.currency_sek,
        Currency.SGD to R.string.currency_sgd,
        Currency.THB to R.string.currency_thb,
        Currency.TRY to R.string.currency_try,
        Currency.ZAR to R.string.currency_zar
    )

    private val codeResMap = mapOf(
        Currency.USD to R.string.code_usd,
        Currency.GBP to R.string.code_gbp,
        Currency.EUR to R.string.code_eur,
        Currency.AUD to R.string.code_aud,
        Currency.BGN to R.string.code_bgn,
        Currency.BRL to R.string.code_brl,
        Currency.CAD to R.string.code_cad,
        Currency.CHF to R.string.code_chf,
        Currency.CNY to R.string.code_cny,
        Currency.CZK to R.string.code_czk,
        Currency.DKK to R.string.code_dkk,
        Currency.HKD to R.string.code_hkd,
        Currency.HRK to R.string.code_hrk,
        Currency.HUF to R.string.code_huf,
        Currency.IDR to R.string.code_idr,
        Currency.ILS to R.string.code_ils,
        Currency.INR to R.string.code_inr,
        Currency.ISK to R.string.code_isk,
        Currency.JPY to R.string.code_jpy,
        Currency.KRW to R.string.code_krw,
        Currency.MXN to R.string.code_mxn,
        Currency.MYR to R.string.code_myr,
        Currency.NOK to R.string.code_nok,
        Currency.NZD to R.string.code_nzd,
        Currency.PHP to R.string.code_php,
        Currency.PLN to R.string.code_pln,
        Currency.RON to R.string.code_ron,
        Currency.RUB to R.string.code_rub,
        Currency.SEK to R.string.code_sek,
        Currency.SGD to R.string.code_sgd,
        Currency.THB to R.string.code_thb,
        Currency.TRY to R.string.code_try,
        Currency.ZAR to R.string.code_zar
    )

    private val symbolResMap = mapOf(
        Currency.USD to R.string.symbol_usd,
        Currency.GBP to R.string.symbol_gbp,
        Currency.EUR to R.string.symbol_eur,
        Currency.AUD to R.string.symbol_aud,
        Currency.BGN to R.string.symbol_bgn,
        Currency.BRL to R.string.symbol_brl,
        Currency.CAD to R.string.symbol_cad,
        Currency.CHF to R.string.symbol_chf,
        Currency.CNY to R.string.symbol_cny,
        Currency.CZK to R.string.symbol_czk,
        Currency.DKK to R.string.symbol_dkk,
        Currency.HKD to R.string.symbol_hkd,
        Currency.HRK to R.string.symbol_hrk,
        Currency.HUF to R.string.symbol_huf,
        Currency.IDR to R.string.symbol_idr,
        Currency.ILS to R.string.symbol_ils,
        Currency.INR to R.string.symbol_inr,
        Currency.ISK to R.string.symbol_isk,
        Currency.JPY to R.string.symbol_jpy,
        Currency.KRW to R.string.symbol_krw,
        Currency.MXN to R.string.symbol_mxn,
        Currency.MYR to R.string.symbol_myr,
        Currency.NOK to R.string.symbol_nok,
        Currency.NZD to R.string.symbol_nzd,
        Currency.PHP to R.string.symbol_php,
        Currency.PLN to R.string.symbol_pln,
        Currency.RON to R.string.symbol_ron,
        Currency.RUB to R.string.symbol_rub,
        Currency.SEK to R.string.symbol_sek,
        Currency.SGD to R.string.symbol_sgd,
        Currency.THB to R.string.symbol_thb,
        Currency.TRY to R.string.symbol_try,
        Currency.ZAR to R.string.symbol_zar
    )

    private val iconResMap = mapOf(
        Currency.USD to R.drawable.ic_usd,
        Currency.GBP to R.drawable.ic_gbp,
        Currency.EUR to R.drawable.ic_eur,
        Currency.AUD to R.drawable.ic_aud,
        Currency.BGN to R.drawable.ic_bgn,
        Currency.BRL to R.drawable.ic_brl,
        Currency.CAD to R.drawable.ic_cad,
        Currency.CHF to R.drawable.ic_chf,
        Currency.CNY to R.drawable.ic_cny,
        Currency.CZK to R.drawable.ic_czk,
        Currency.DKK to R.drawable.ic_dkk,
        Currency.HKD to R.drawable.ic_hkd,
        Currency.HRK to R.drawable.ic_hrk,
        Currency.HUF to R.drawable.ic_huf,
        Currency.IDR to R.drawable.ic_idr,
        Currency.ILS to R.drawable.ic_ils,
        Currency.INR to R.drawable.ic_inr,
        Currency.ISK to R.drawable.ic_isk,
        Currency.JPY to R.drawable.ic_jpy,
        Currency.KRW to R.drawable.ic_krw,
        Currency.MXN to R.drawable.ic_mxn,
        Currency.MYR to R.drawable.ic_myr,
        Currency.NOK to R.drawable.ic_nok,
        Currency.NZD to R.drawable.ic_nzd,
        Currency.PHP to R.drawable.ic_php,
        Currency.PLN to R.drawable.ic_pln,
        Currency.RON to R.drawable.ic_ron,
        Currency.RUB to R.drawable.ic_rub,
        Currency.SEK to R.drawable.ic_sek,
        Currency.SGD to R.drawable.ic_sgd,
        Currency.THB to R.drawable.ic_thb,
        Currency.TRY to R.drawable.ic_try,
        Currency.ZAR to R.drawable.ic_zar
    )

    @StringRes
    fun getCurrencyNameRes(currency: Currency): Int =
        nameResMap[currency] ?: error("Name resource not found for $currency")

    @StringRes
    fun getCurrencyCodeRes(currency: Currency): Int =
        codeResMap[currency] ?: error("Code resource not found for $currency")

    @StringRes
    fun getCurrencySymbolRes(currency: Currency): Int =
        symbolResMap[currency] ?: error("Symbol resource not found for $currency")

    @DrawableRes
    fun getCurrencyIconRes(currency: Currency): Int =
        iconResMap[currency] ?: error("Icon resource not found for $currency")
}
