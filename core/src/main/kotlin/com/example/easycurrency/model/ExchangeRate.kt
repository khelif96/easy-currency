package com.example.easycurrency.model

import java.time.LocalDate

data class ExchangeRate(
    val pair: CurrencyPair,
    val rate: Double,
    val lastUpdated: LocalDate,
    val source: String,
)
