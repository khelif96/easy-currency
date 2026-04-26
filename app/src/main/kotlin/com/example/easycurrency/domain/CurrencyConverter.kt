package com.example.easycurrency.domain

import com.example.easycurrency.data.ExchangeRateProvider
import com.example.easycurrency.model.CurrencyPair
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val STALE_THRESHOLD_DAYS = 7L

sealed class ConversionResult {
    data class Success(
        val convertedAmount: Double,
        val formattedAmount: String,
        val isStale: Boolean,
        val rateDate: String,
    ) : ConversionResult()

    data class UnsupportedPair(val pair: CurrencyPair) : ConversionResult()
}

class CurrencyConverter(private val provider: ExchangeRateProvider) {

    fun convert(amount: Double, pair: CurrencyPair): ConversionResult {
        val rate = provider.getRate(pair) ?: return ConversionResult.UnsupportedPair(pair)

        val rawResult = amount * rate.rate
        val rounded = BigDecimal(rawResult)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()

        val isStale = isStale(rate.lastUpdated)
        val formatted = formatAmount(rounded, pair)

        return ConversionResult.Success(
            convertedAmount = rounded,
            formattedAmount = formatted,
            isStale = isStale,
            rateDate = rate.lastUpdated.toString(),
        )
    }

    private fun isStale(lastUpdated: LocalDate): Boolean {
        val daysSinceUpdate = ChronoUnit.DAYS.between(lastUpdated, LocalDate.now())
        return daysSinceUpdate > STALE_THRESHOLD_DAYS
    }

    private fun formatAmount(amount: Double, pair: CurrencyPair): String {
        val symbol = pair.to.symbol
        return if (amount >= 1_000) {
            "$symbol%,.2f".format(amount)
        } else {
            "$symbol%.2f".format(amount)
        }
    }
}
