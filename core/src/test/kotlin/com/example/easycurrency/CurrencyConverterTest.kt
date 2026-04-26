package com.example.easycurrency

import com.example.easycurrency.data.StaticExchangeRateProvider
import com.example.easycurrency.domain.ConversionResult
import com.example.easycurrency.domain.CurrencyConverter
import com.example.easycurrency.model.CurrencyCode
import com.example.easycurrency.model.CurrencyPair
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrencyConverterTest {

    private lateinit var converter: CurrencyConverter

    @Before
    fun setUp() {
        converter = CurrencyConverter(StaticExchangeRateProvider)
    }

    @Test
    fun `convert JPY to USD returns success`() {
        val result = converter.convert(1000.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
        assertTrue(result is ConversionResult.Success)
    }

    @Test
    fun `convert 1000 JPY to USD gives expected amount`() {
        val result = converter.convert(1000.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        assertEquals(6.70, result.convertedAmount, 0.001)
    }

    @Test
    fun `convert JPY to USD result is formatted with dollar sign`() {
        val result = converter.convert(1000.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        assertTrue(result.formattedAmount.startsWith("$"))
    }

    @Test
    fun `convert EUR to USD returns success`() {
        val result = converter.convert(100.0, CurrencyPair(CurrencyCode.EUR, CurrencyCode.USD))
        assertTrue(result is ConversionResult.Success)
    }

    @Test
    fun `convert 100 EUR to USD gives expected amount`() {
        val result = converter.convert(100.0, CurrencyPair(CurrencyCode.EUR, CurrencyCode.USD))
            as ConversionResult.Success
        assertEquals(108.0, result.convertedAmount, 0.001)
    }

    @Test
    fun `convert EUR to USD result is formatted with dollar sign`() {
        val result = converter.convert(100.0, CurrencyPair(CurrencyCode.EUR, CurrencyCode.USD))
            as ConversionResult.Success
        assertTrue(result.formattedAmount.startsWith("$"))
    }

    @Test
    fun `unsupported pair returns UnsupportedPair result`() {
        val unsupportedPair = CurrencyPair(CurrencyCode.KRW, CurrencyCode.GBP)
        val result = converter.convert(1000.0, unsupportedPair)
        assertTrue(result is ConversionResult.UnsupportedPair)
    }

    @Test
    fun `unsupported pair contains the requested pair`() {
        val unsupportedPair = CurrencyPair(CurrencyCode.KRW, CurrencyCode.GBP)
        val result = converter.convert(1000.0, unsupportedPair) as ConversionResult.UnsupportedPair
        assertEquals(unsupportedPair, result.pair)
    }

    @Test
    fun `result is rounded to two decimal places`() {
        val result = converter.convert(1.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        val str = result.convertedAmount.toString()
        val decimalIndex = str.indexOf('.')
        if (decimalIndex >= 0) {
            assertTrue("More than 2 decimal places", str.length - decimalIndex - 1 <= 2)
        }
    }

    @Test
    fun `convert zero amount gives zero result`() {
        val result = converter.convert(0.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        assertEquals(0.0, result.convertedAmount, 0.0)
    }

    @Test
    fun `large JPY amount formats with comma separator`() {
        // 1,000,000 JPY * 0.0067 = 6,700 USD — above the 1,000 threshold that adds commas
        val result = converter.convert(1_000_000.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        assertTrue(result.formattedAmount.contains(","))
    }

    @Test
    fun `static rates are marked stale since they use a past date`() {
        val result = converter.convert(1000.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        assertTrue(result.isStale)
    }

    @Test
    fun `stale result still contains a valid rate date`() {
        val result = converter.convert(1000.0, CurrencyPair(CurrencyCode.JPY, CurrencyCode.USD))
            as ConversionResult.Success
        assertNotNull(result.rateDate)
        assertTrue(result.rateDate.isNotBlank())
    }
}
