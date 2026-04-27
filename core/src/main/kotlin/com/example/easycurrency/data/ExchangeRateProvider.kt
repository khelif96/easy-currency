package com.example.easycurrency.data

import com.example.easycurrency.model.CurrencyPair
import com.example.easycurrency.model.ExchangeRate

interface ExchangeRateProvider {
    fun getRate(pair: CurrencyPair): ExchangeRate?
    fun supportedPairs(): List<CurrencyPair>
}
