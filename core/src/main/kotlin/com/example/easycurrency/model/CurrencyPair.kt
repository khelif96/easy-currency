package com.example.easycurrency.model

data class CurrencyPair(
    val from: CurrencyCode,
    val to: CurrencyCode,
) {
    override fun toString(): String = "${from.name} → ${to.name}"
}
