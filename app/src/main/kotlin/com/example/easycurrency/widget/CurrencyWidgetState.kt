package com.example.easycurrency.widget

import com.example.easycurrency.model.CurrencyCode
import com.example.easycurrency.model.CurrencyPair

val DEFAULT_PAIRS: List<CurrencyPair> = listOf(
    CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD),
    CurrencyPair(CurrencyCode.EUR, CurrencyCode.USD),
)

data class CurrencyWidgetState(
    val inputAmount: Double = 1000.0,
    val selectedPairIndex: Int = 0,
    val convertedAmount: String = "",
    val rateDate: String = "",
    val isStale: Boolean = false,
    val errorMessage: String? = null,
    val availablePairs: List<CurrencyPair> = DEFAULT_PAIRS,
) {
    val selectedPair: CurrencyPair
        get() = availablePairs[selectedPairIndex.coerceIn(0, availablePairs.lastIndex)]
}
