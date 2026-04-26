package com.example.easycurrency.data

import com.example.easycurrency.model.CurrencyCode
import com.example.easycurrency.model.CurrencyPair
import com.example.easycurrency.model.ExchangeRate
import java.time.LocalDate

object StaticExchangeRateProvider : ExchangeRateProvider {

    private val RATE_DATE = LocalDate.of(2024, 1, 1)
    private const val SOURCE = "Static placeholder (not real-time)"

    private val rates: Map<CurrencyPair, ExchangeRate> = buildMap {
        fun add(from: CurrencyCode, to: CurrencyCode, rate: Double) {
            val pair = CurrencyPair(from, to)
            put(pair, ExchangeRate(pair, rate, RATE_DATE, SOURCE))
        }

        add(CurrencyCode.JPY, CurrencyCode.USD, 0.0067)
        add(CurrencyCode.EUR, CurrencyCode.USD, 1.08)
        add(CurrencyCode.USD, CurrencyCode.JPY, 149.50)
        add(CurrencyCode.USD, CurrencyCode.EUR, 0.93)
        add(CurrencyCode.GBP, CurrencyCode.USD, 1.27)
        add(CurrencyCode.CAD, CurrencyCode.USD, 0.74)
        add(CurrencyCode.KRW, CurrencyCode.USD, 0.00075)
    }

    override fun getRate(pair: CurrencyPair): ExchangeRate? = rates[pair]

    override fun supportedPairs(): List<CurrencyPair> = rates.keys.toList()
}
